/**
 * Personium
 * Copyright 2014 - 2018 FUJITSU LIMITED
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
package io.personium.engine.rs;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.personium.engine.PersoniumEngineContext;
import io.personium.engine.PersoniumEngineException;
import io.personium.engine.source.ISourceManager;
import io.personium.engine.utils.PersoniumEngineConfig;

/**
 * 抽象Serviceクラス.
 */
public abstract class AbstractService {
    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(AbstractService.class);

    /** デバッグ実行時のローカルパス. */
    private String sourcePath = "";
    /** Service名. */
    private String serviceName = "";
    /** サーブレットコンテキストオブジェクト. */
    @Context
    private ServletContext context;
    /** 基底URLが格納されているヘッダ名. */
    private static final String KEY_HEADER_BASEURL = "X-Baseurl";
    /** リクエストURLが格納されているヘッダ名. */
    private static final String KEY_HEADER_REQUEST_URI = "X-Request-Uri";

    /** HTTPポート. */
    private static final int PORT_HTTP = 80;
    /** HTTPSポート. */
    private static final int PORT_HTTPS = 443;

    /** リクエストヘッダから取得するFsPath. */
    @HeaderParam("X-Personium-Fs-Path")
    private String fsPath;
    /** FsRoutingId obtained from the request header. */
    @HeaderParam("X-Personium-Fs-Routing-Id")
    private String fsRoutingId;

    /** サービスサブジェクト. */
    String serviceSubject;

    /** ソース情報管理. */
    ISourceManager sourceManager;

    /**
     * デバッグ実行時のローカルパスの取得.
     * @return ローカルパス
     */
    public final String getSourcePath() {
        return this.sourcePath;
    }

    /**
     * デバッグ実行時のローカルパスの設定.
     * @param value ローカルパス
     */
    public final void setSourcePath(final String value) {
        this.sourcePath = value;
    }

    /**
     * Service名を取得する.
     * @return Service名
     */
    public final String getServiceName() {
        return this.serviceName;
    }

    /**
     * Service名を設定する.
     * @param value Service名
     */
    public final void setServiceName(final String value) {
        this.serviceName = value;
    }

    /**
     * FsPathを取得する.
     * @return the fsPath
     */
    public final String getFsPath() {
        return fsPath;
    }

    /**
     * Get fsRoutingId.
     * @return fsRoutingId
     */
    public String getFsRoutingId() {
        return fsRoutingId;
    }

    /**
     * GETメソッド.
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param svcName サービス名
     * @param request リクエストオブジェクト
     * @param response レスポンスオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Responseオブジェクト
     */
    @GET
    public final Response evalJsgiForGet(@PathParam("cell") final String cell,
            @PathParam("schema") final String schema,
            @PathParam("id") final String svcName,
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final InputStream is) {
        return run(cell, schema, svcName, request, response, is);
    }

    /**
     * POSTメソッド.
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param svcName サービス名
     * @param request リクエストオブジェクト
     * @param response レスポンスオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Responseオブジェクト
     */
    @POST
    public final Response evalJsgiForPost(@PathParam("cell") final String cell,
            @PathParam("schema") final String schema,
            @PathParam("id") final String svcName,
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final InputStream is) {
        return run(cell, schema, svcName, request, response, is);
    }

    /**
     * PUTメソッド.
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param svcName サービス名
     * @param request リクエストオブジェクト
     * @param response レスポンスオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Responseオブジェクト
     */
    @PUT
    public final Response evalJsgiForPutMethod(@PathParam("cell") final String cell,
            @PathParam("schema") final String schema,
            @PathParam("id") final String svcName,
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final InputStream is) {
        return run(cell, schema, svcName, request, response, is);
    }

    /**
     * DELETEメソッド.
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param svcName サービス名
     * @param request リクエストオブジェクト
     * @param response レスポンスオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Responseオブジェクト
     */
    @DELETE
    public final Response evalJsgiForDeleteMethod(@PathParam("cell") final String cell,
            @PathParam("schema") final String schema,
            @PathParam("id") final String svcName,
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final InputStream is) {
        return run(cell, schema, svcName, request, response, is);
    }

    /**
     * 使用するサーブレットコンテキストオブジェクトの指定.
     * @param value サーブレットコンテキストオブジェクト
     */
    public final void setServletContext(final ServletContext value) {
        this.context = value;
    }

    /**
     * サーブレットコンテキストオブジェクトを取得.
     * @return サーブレットコンテキストオブジェクト
     */
    public final ServletContext getServletContext() {
        return this.context;
    }

    /**
     * Service実行.
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param svcName サーバー名
     * @param req Requestオブジェクト
     * @param res Responseオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Response
     */
    public final Response run(final String cell,
            final String schema,
            final String svcName,
            final HttpServletRequest req,
            final HttpServletResponse res,
            final InputStream is) {

        //XXX Debug
        long requestStartTime = System.currentTimeMillis();
        long previousPhaseTime = System.currentTimeMillis();
        StringBuilder timeBuilder = new StringBuilder();

        StringBuilder msg = new StringBuilder();
        msg.append("[" + PersoniumEngineConfig.getVersion() + "] " + ">>> Request Started ");
        msg.append(" method:");
        msg.append(req.getMethod());
        msg.append(" method:");
        msg.append(req.getRequestURL());
        msg.append(" url:");
        msg.append(cell);
        msg.append(" schema:");
        msg.append(schema);
        msg.append(" svcName:");
        msg.append(svcName);
        log.info(msg);

        // デバッグ用 すべてのヘッダをログ出力
        Enumeration<String> multiheaders = req.getHeaderNames();
        for (String headerName : Collections.list(multiheaders)) {
            Enumeration<String> headers = req.getHeaders(headerName);
            for (String header : Collections.list(headers)) {
                log.debug("RequestHeader['" + headerName + "'] = " + header);
            }
        }
        this.setServiceName(svcName);

        // サービスのURL情報の取得。
        String targetCell = cell;
        if (cell == null) {
            targetCell = getCell();
        }
        String targetSchema = schema;
        if (schema == null) {
            targetSchema = getSchemaURI();
        }
        String targetServiceName = svcName;

        String baseUrl;
        try {
            baseUrl = parseRequestUri(req, res);
        } catch (MalformedURLException e) {
            // URLに異常があった場合のエラー
            return makeErrorResponse("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR);
        }

        Response response = null;
        PersoniumEngineContext pecx = null;
        try {
            try {
                pecx = new PersoniumEngineContext(timeBuilder);
            } catch (PersoniumEngineException e) {
                return errorResponse(e);
            }

            // ソースに関する情報を取得
            try {
                this.sourceManager = this.getServiceCollectionManager();
                pecx.setSourceManager(this.sourceManager);
                this.serviceSubject = this.sourceManager.getServiceSubject();
            } catch (PersoniumEngineException e) {
                return errorResponse(e);
            }
            // グローバルオブジェクトのロード
            pecx.loadGlobalObject(baseUrl, targetCell, targetSchema, targetSchema, targetServiceName);
            // ユーザスクリプトを取得（設定及びソース）
            String source = "";
            String sourceName = "";
            try {
                sourceName = this.sourceManager.getScriptNameForServicePath(targetServiceName);
                source = this.sourceManager.getSource(sourceName);
            } catch (PersoniumEngineException e) {
                return errorResponse(e);
            } catch (Exception e) {
                log.info("User Script not found to targetCell(" + targetCell
                       + ", targetschema(" + targetSchema + "), targetServiceName(" + targetServiceName + ")");
                log.info(e.getMessage(), e);
                return errorResponse(new PersoniumEngineException("404 Not Found (User Script)",
                        PersoniumEngineException.STATUSCODE_NOTFOUND));
            }
            // JSGI実行
            try {
                response = pecx.runJsgi(source, req, res, is, this.serviceSubject, previousPhaseTime, sourceName);
            } catch (PersoniumEngineException e) {
                return errorResponse(e);
            } catch (Exception e) {
                log.warn(" unknown Exception(" + e.getMessage() + ")", e);
                return errorResponse(new PersoniumEngineException("404 Not Found (Service Execute Error)",
                        PersoniumEngineException.STATUSCODE_NOTFOUND));
            }
        } finally {
            IOUtils.closeQuietly(pecx);
            timeBuilder.append("Total,");
            timeBuilder.append(System.currentTimeMillis() - requestStartTime);
            log.debug("========== Engine timestamp. " + timeBuilder.toString());
        }
        return response;
    }

    /**
     * エラーレスポンス生成.
     * @param e Exceptionオブジェクト
     * @return Response
     */
    final Response errorResponse(final PersoniumEngineException e) {
        return makeErrorResponse(e.getMessage(), e.getStatusCode());
    }

    /**
     * エラー時のレスポンスを生成.
     * @param msg メッセージ本文
     * @param code ステータスコード
     * @return Responseオブジェクト
     */
    private Response makeErrorResponse(final String msg, final int code) {
        return Response.status(code)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_LENGTH, msg.getBytes().length)
                .entity(msg).build();
    }

    /**
     * Cell名取得.
     * @return Cell名
     */
    public abstract String getCell();

    /**
     * データスキーマURI取得.
     * @return データスキーマURI
     */
    public abstract String getSchemaURI();

    /**
     * リクエストＵＲＬの解析.
     * @param req Requestオブジェクト
     * @param res Responseオブジェクト
     * @return 基底URL文字列
     * @throws MalformedURLException 例外
     */
    public final String parseRequestUri(final HttpServletRequest req, final HttpServletResponse res)
            throws MalformedURLException {
        String baseUrl = "";

        // リクエストURIを取得する。リクエストURI＝＞ホスト名よりあとのクエリ含んだ文字列。
        // 例） /personium-engine/engine-test/ds-engine-test/service/hello?a=b&c=d
        // （通常動作）DCから送られてきたURIがヘッダに指定されていたらそれを利用する。
        // （デバッグ動作）Personium-Engine呼び出しのURIから生成する。
        String requestUri = req.getHeader(KEY_HEADER_REQUEST_URI);
        if (requestUri == null || requestUri.length() == 0) {
            requestUri = req.getRequestURI();
            String query = req.getQueryString();
            if (query != null && query.length() > 0) {
                requestUri += "?" + query;
            }
        }

        // リクエストURIからクエリ部分を切り取って、スクリプト名を抜き出す。
        // 例）/personium-engine/engine-test/personium-engine-test/service/hello
        int indexQ = requestUri.indexOf("?");
        String scriptName = requestUri;
        if (indexQ > 0) {
            scriptName = requestUri.substring(0, indexQ);
        }

        // サーブレットリクエストオブジェクトに値を保存。あとでJSGIオブジェクトに設定するため。
        req.setAttribute("env.requestUri", requestUri);
        req.setAttribute("scriptName", scriptName);

        // クライアントから受け付けたリクエストURL（ホスト？）の取得
        // （通常動作）DCから送られてきたURLがヘッダに指定されていたらそれを利用する。
        baseUrl = req.getHeader(KEY_HEADER_BASEURL);

        // サーブレットリクエストオブジェクトに値を保存。あとでJSGIオブジェクトに設定するため。
        URL baseUrlObj = new URL(baseUrl);
        int port = baseUrlObj.getPort();
        String proto = baseUrlObj.getProtocol();
        String host = baseUrlObj.getHost();
        String hostHeader = host;
        if (port < 0) {
            if ("http".endsWith(proto)) {
                port = PORT_HTTP;
            }
            if ("https".endsWith(proto)) {
                port = PORT_HTTPS;
            }
        } else {
            hostHeader += ":" + port;
        }
        req.setAttribute("HostHeader", hostHeader);
        req.setAttribute("host", baseUrlObj.getHost());
        req.setAttribute("port", port);
        req.setAttribute("scheme", proto);
        return baseUrl;
    }
    /**
     * ServiceCollectionManager取得.
     * @return ISourceManager
     * @throws PersoniumEngineException Exception about Engine
     */
    public abstract ISourceManager getServiceCollectionManager() throws PersoniumEngineException;

}
