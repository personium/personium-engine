/**
 * Personium
 * Copyright 2014-2021 - Personium Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.personium.engine.jsgi;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.engine.extension.wrapper.PersoniumInputStream;

/**
 * A converter class from a JSGI response JSON (in JavaScript) to JAX-RS response.
 */
@SuppressWarnings("serial")
public final class PersoniumResponse extends ScriptableObject {
    private static final int BUFFER_SIZE = 1024;

    /** logger. */
    private static Logger log = LoggerFactory.getLogger(PersoniumResponse.class);

    int status = 0;
    String charset = "utf-8";
    Map<String, String> headers = new HashMap<String, String>();
    String body = null;
    StreamingOutput streaming;
    OutputStream output;

    /**
     * Receive a JSGI response JSON returned from the user's script and convert to a java PersoniumResponse object.
     * TODO change the method name to "create" since this is typical Factory pattern
     * @param jsgiResponse JSGI response JSON returned in user JavaScript
     * @return PersoniumResponse
     * @throws Exception 
     */
    public static PersoniumResponse parseJsgiResponse(Object jsgiResponse) throws Exception {

        final PersoniumResponse personiumResponse = new PersoniumResponse();
        // Input should be a JSON via RHINO.
        if (!(jsgiResponse instanceof NativeObject)) {
            String msg = "not NativeObject";
            log.info(msg);
            // TODO more suitable exception
            throw new Exception(msg);
        }
        NativeObject response = (NativeObject) jsgiResponse;

        // check status value in the input JSON
        // status should be a number
        Object oStatus = ScriptableObject.getProperty(response, "status");
        if (!(oStatus instanceof Number)) {
            String msg = "response status illegal type.";
            log.info(msg + ":" + oStatus.getClass());
            // TODO more suitable exception
            throw new Exception(msg);
        }
        // Error if the status code is invalid (100 Series)
        if (isInvalidResCode((Number) oStatus)) {
            String msg = String.format("response status illegal type. status: %s",
                    String.valueOf(Context.toString(oStatus)));
            log.info(msg + ":" + oStatus);
            throw new Exception(msg);
        }
        personiumResponse.setStatus((int) Context.toNumber(oStatus));

        // check headers
        Object oHeaders = ScriptableObject.getProperty(response, "headers");
        // should be JSON key value format 
        if (!(oHeaders instanceof NativeObject)) {
            String msg = "not headers";
            log.info(msg);
            throw new Exception(msg);
        }
        NativeObject nHeaders = (NativeObject) oHeaders;
        Object[] os = nHeaders.getIds();
        for (Object o : os) {
            if (!(o instanceof String)) {
                String msg = "header key format error";
                log.info(msg);
                throw new Exception(msg);
            }
            String key = Context.toString(o);
            // Transfer-Encoding will be ignored
            if ("Transfer-Encoding".equalsIgnoreCase(key)) {
                continue;
            }
            Object value = nHeaders.get(key, nHeaders);
            // Header value should be String
            if (!(value instanceof String)) {
                String msg = "header value format error";
                log.info(msg);
                throw new Exception(msg);
            }
            personiumResponse.addHeader(key, Context.toString(value));
        }

        // check body
        Object oBody = ScriptableObject.getProperty(response, "body");
        // Should be a ScriptableObject
        if (!(oBody instanceof ScriptableObject)) {
            String msg = "response body undefined forEach.";
            log.info(msg);
            throw new Exception(msg);
        }
        final ScriptableObject scriptableBody = (ScriptableObject) oBody;
        // check if it has a property with name "forEach"
        if (!ScriptableObject.hasProperty(scriptableBody, "forEach")) {
            String msg = "response body undefined forEach.";
            log.info(msg);
            throw new Exception(msg);
        }
        Method checkMethod = personiumResponse.getForEach("bodyCheckFunction");
        final Method responseMethod = personiumResponse.getForEach("bodyResponseFunction");

        // JavaのforEach実装をJavaScriptの関数として登録
        ScriptableObject callback = new FunctionObject("bodyCheckFunction", checkMethod, personiumResponse);
        // forEach呼び出し（返却データの型チェック用）
        Object[] args = {callback};
        try {
            ScriptableObject.callMethod(scriptableBody, "forEach", args);
        } catch (JavaScriptException e) {
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }

        // Response の遅延処理登録
        StreamingOutput stremingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream resStream) throws IOException {
                // forEachのコールバックを呼び出す準備
                personiumResponse.setOutput(resStream);

                // forEachをJavaScriptの関数として登録
                ScriptableObject callback = new FunctionObject("bodyResponseFunction",
                        responseMethod, personiumResponse);

                // forEach呼び出し
                Object[] args = {callback};
                ScriptableObject.callMethod(scriptableBody, "forEach", args);
                resStream.close();
            }
        };

        personiumResponse.setBody(stremingOutput);
        return personiumResponse;
    }

    /**
     * Engineとして許容しないレスポンスコードかどうかを判定する.
     * @param oStatus レスポンスコード(Number型)
     * @return true:Engineとして許容しないレスポンスコードである false:Engineとして許容できるレスポンスコードである
     */
    private static boolean isInvalidResCode(Number oStatus) {
        // Following kinds of response codes are invalid.
        // - not 3 digits
        // - 0 series
        // - 100 Series (クライアントの挙動が不安定になるため)
        if (!String.valueOf(Context.toString(oStatus)).matches("^[2-9]\\d{2}$")) {
            return true;
        }
        return false;
    }

    /**
     * レスポンスヘッダを設定.
     * @param status ステータスコード
     */
    private void setStatus(int status) {
        this.status = status;
    }

    /**
     * レスポンスヘッダーを追加.
     * @param key ヘッダ名
     * @param value 値
     * @throws Exception Exception
     */
    private void addHeader(String key, String value) throws Exception {
        // Content-typeだったらcharsetを抜き出す。出力エンコードを知るため。
      if (key.equalsIgnoreCase("content-type")) {
            // メディアタイプ異常のままJAX-RSフレームワークへ渡すと例外になるのでここでチェック
          if (!checkMediaType(value)) {
              String msg = "Response header parsing media type.";
              log.info(msg);
              throw new Exception(msg);
          }
            // 判定するパターンを生成
          Pattern p = Pattern.compile("charset=([^;]+)");
          Matcher m = p.matcher(value);
          if (m.find()) {
              String tmp = m.group(1);
                // charset異常のままJAX-RSフレームワークへ渡すと例外になるのでここでチェック
              if (!checkCharSet(tmp)) {
                  String msg = "response charset illegal type.";
                  log.info(msg);
                  throw new Exception(msg);
              }
              this.charset = tmp;
          }
      }
      this.headers.put(key, value);
    }

    /**
     * メディアタイプが正常かチェック.
     * @param type チェック対象のメディア・タイプ
     * @return bool
     */
    private static boolean checkMediaType(String type) {
        try {
            MediaType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * char-setが正常かチェック.
     * @param value チェック対象のchar-set
     * @return bool
     */
    private static boolean checkCharSet(String value) {
        return Charset.isSupported(value);
    }

    /**
     * JavaScriptからレスポンスで返すStremingOutputを設定する. 直接StreamingOutputを扱わずにラップしたDcStremingOutputを受け取る
     * @param value DcStremingOutputオブジェクト
     */
    private void setBody(StreamingOutput value) {
        this.streaming = value;
    }

    /**
     * レスポンス生成.
     * @return レスポンス
     */
    public Response build() {
        ResponseBuilder builder = Response.status(this.status);
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        builder.entity(this.streaming);
        return builder.build();
    }

    /**
     * Setter for the OutputStream for response output target.
     * @param output target Stream
     */
    private void setOutput(OutputStream output) {
        this.output = output;
    }

    /**
     * JavaScriptのforEachをJavaで処理するためのメソッド.
     * @param element forEachの要素が一件ずつ渡される
     * @param number 渡された要素のindexが渡される
     * @param object forEach対象のオブジェクトの全要素の配列が渡される
     * @throws IOException IOException
     */
    public void bodyResponseFunction(Object element, double number, NativeArray object) throws IOException {
        if (element instanceof PersoniumInputStream) {
            // 現状はEngine上のJavaScriptでバイナリを直接扱わず
            // JavaのストリームをそのままJavaScript内で扱うことで対応
            PersoniumInputStream io = (PersoniumInputStream) element;
            byte[] buf = new byte[BUFFER_SIZE];
            int bufLength;
            while ((bufLength = io.read(buf)) != -1) {
                this.output.write(buf, 0, bufLength);
            }
        } else {
            // String はユーザスクリプトがContent-typeのcharsetで指定した文字エンコーディングで出力。
            this.output.write(((String) element).getBytes(charset));
        }
    }

    /**
     * JavaScriptのforEachをJavaで処理するためのメソッド(レスポンス内容チェック用).
     * @param element forEachの要素が一件ずつ渡される
     * @param number 渡された要素のindexが渡される
     * @param object forEach対象のオブジェクトの全要素の配列が渡される
     * @throws Exception Exception
     */
    public void bodyCheckFunction(Object element, double number, NativeArray object) throws Exception {
        if (!(element instanceof PersoniumInputStream) && !(element instanceof String)) {
            String msg = "response body illegal type.";
            log.info(msg);
            throw new Exception(msg);
        }
    }

    /**
     * JavaScriptのforEach処理をJavaで行うためのメソッド（function）を取得.
     * @param methodName Method name
     * @return function
     * @throws Exception 
     */
    private Method getForEach(String methodName) throws Exception {
        Method method;
        try {
            method = this.getClass().getMethod(methodName,
                    new Class[] {Object.class, double.class, NativeArray.class});
        } catch (SecurityException e) {
            String msg = "function not allowed.";
            log.warn(msg);
            throw new Exception(msg);
        } catch (NoSuchMethodException e) {
            String msg = "forEach function not found.";
            log.warn(msg);
            throw new Exception(msg);
        }
        return method;
    }

    @Override
    public String getClassName() {
        return "PersoniumResponse";
    }
}
