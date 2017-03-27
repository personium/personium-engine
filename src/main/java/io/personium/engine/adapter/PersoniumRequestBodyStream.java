/**
 * Personium
 * Copyright 2014 - 2017 FUJITSU LIMITED
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
package io.personium.engine.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.personium.engine.extension.wrapper.PersoniumInputStream;


/**
 * RequestボディのInputStreamをラップして提供する.
 */
public final class PersoniumRequestBodyStream {
    InputStream input;
    BufferedReader bufferReader;

    /**
     * コンストラクタ.
     * @param input InputStream
     */
    public PersoniumRequestBodyStream(InputStream input) {
        this.input = input;
    }

    /**
     * ストリーム型で返却. InputStreamのままJavaScriptに返却するとClassShutterに怒られるのでラップして返却
     * @return InputStreamのラッパーオブジェクト
     * @throws Exception Exception
     */
    public PersoniumInputStream stream() throws Exception  {
        return new PersoniumInputStream(this.input);
    }

    /**
     * １行読み込む.
     * InputStreamのreadLineへリレー
     * @param encoding 読み込み文字エンコーディング
     * @return String 読み込んだ行文字列
     * @throws IOException IOException
     */
    public String readLine(String encoding) throws IOException {
        if (this.bufferReader == null) {
            this.bufferReader = new BufferedReader(new InputStreamReader(this.input, encoding));
        }
        return bufferReader.readLine();
    }

    /**
     * １行読み込む.
     * InputStreamのreadLineへリレー
     * @return String 読み込んだ行文字列
     * @throws IOException IOException
     */
    public String readLine() throws IOException {
        return this.readLine("utf-8");
    }

    /**
     * 全体を読み込む.
     * InputStreamのreadへリレー.
     * @param encoding 読み込み文字エンコーディング
     * @return String 読み込んだ行文字列
     * @throws IOException IOException
     */
    public String readAll(String encoding) throws IOException {
        if (this.bufferReader == null) {
            this.bufferReader = new BufferedReader(new InputStreamReader(this.input, encoding));
        }
        StringBuilder sb = new StringBuilder();
        int chr;
        while ((chr = this.bufferReader.read()) != -1) {
            sb.append((char) chr);
        }
        return sb.toString();
    }
    /**
     * 全体を読み込む.
     * InputStreamのreadへリレー.
     * @return String 読み込んだ行文字列
     * @throws IOException IOException
     */
    public String readAll() throws IOException {
        return this.readAll("utf-8");
    }
}
