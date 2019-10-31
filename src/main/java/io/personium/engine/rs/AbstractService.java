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
import javax.ws.rs.Path;
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
 * Abstract Resource Class.
 */
public abstract class AbstractService {
    /** Logger Object. */
    private static Log log = LogFactory.getLog(AbstractService.class);

    /** Local path for debug execution. */
    private String sourcePath = "";
    /** Service Name. */
    private String serviceName = "";
    /** Servlet Context Object. */
    @Context
    private ServletContext context;
    /** Header Name for Base URL. */
    private static final String KEY_HEADER_BASEURL = "X-Baseurl";
    /** Header Name for Request URL. */
    private static final String KEY_HEADER_REQUEST_URI = "X-Request-Uri";

    /** HTTP Port number. */
    private static final int PORT_HTTP = 80;
    /** HTTPS port number. */
    private static final int PORT_HTTPS = 443;

    /** FsPath retrieved from the request header. */
    @HeaderParam("X-Personium-Fs-Path")
    private String fsPath;

    /** FsRoutingId obtained from the request header. */
    @HeaderParam("X-Personium-Fs-Routing-Id")
    private String fsRoutingId;

    /** Service Subject. */
    String serviceSubject;

    /** SourceManager. */
    ISourceManager sourceManager;

    /**
     * Getter for local path used in debug execution.
     * @return Local Path
     */
    public final String getSourcePath() {
        return this.sourcePath;
    }

    /**
     * Setter for the local path used in debugging.
     * @param value Local path
     */
    public final void setSourcePath(final String value) {
        this.sourcePath = value;
    }

    /**
     * Getter for Service Name.
     * @return Service Name
     */
    public final String getServiceName() {
        return this.serviceName;
    }

    /**
     * Setter for Service Name.
     * @param value Service Name
     */
    public final void setServiceName(final String value) {
        this.serviceName = value;
    }

    /**
     * Getter for FsPath.
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
     * GET method processing.
     * @param cell Cell Name
     * @param schema Data Schema URI
     * @param svcName Service Name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param is Request body InputStream
     * @return JAX-RS Response Object
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
     * POST Method processing.
     * @param cell Cell Name
     * @param schema Data Schema URI
     * @param svcName Service Name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param is Request body InputStream
     * @return JAX-RS Response Object
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
     * PUT Method processing.
     * @param cell Cell Name
     * @param schema Data Schema URI
     * @param svcName Service Name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param is Request body InputStream
     * @return JAX-RS Response Object
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
     * DELETE Method processing.
     * @param cell Cell Name
     * @param schema Data Schema URI
     * @param svcName Service Name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param is Request body InputStream
     * @return JAX-RS Response Object
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
     * setter for the ServletContext to use.
     * @param value ServletContext Object
     */
    public final void setServletContext(final ServletContext value) {
        this.context = value;
    }

    /**
     * Getter for the ServletContext object.
     * @return ServletContext object
     */
    public final ServletContext getServletContext() {
        return this.context;
    }

    /**
     * Service Execution.
     * @param cell Cell Name 
     * @param schema Data Schema URI
     * @param svcName Service Name
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param is Request body InputStream
     * @return JAX-RS Response Object
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

        // Logging all headers in Debug Level
        Enumeration<String> multiheaders = req.getHeaderNames();
        for (String headerName : Collections.list(multiheaders)) {
            Enumeration<String> headers = req.getHeaders(headerName);
            for (String header : Collections.list(headers)) {
                log.debug("RequestHeader['" + headerName + "'] = " + header);
            }
        }
        this.setServiceName(svcName);

        // Service のURL情報の取得。
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
            // If URL is Malformed
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

            // Load the Script Source 
            try {
                this.sourceManager = this.getServiceCollectionManager();
                pecx.setSourceManager(this.sourceManager);
                this.serviceSubject = this.sourceManager.getServiceSubject();
            } catch (PersoniumEngineException e) {
                return errorResponse(e);
            }
            // Loading the Global objects
            pecx.loadGlobalObject(baseUrl, targetCell, targetSchema, targetSchema, targetServiceName);

            String source = "";
            String sourceName = "";
            try {
                // discover source name
                sourceName = this.sourceManager.getScriptNameForServicePath(targetServiceName);
                // read Source content
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
            // source JSGI Function Execution
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
        // return JAX-RS Response
        return response;
    }

    /**
     * Error Response creation.
     * @param e Exception Object
     * @return JAX-RS Response
     */
    final Response errorResponse(final PersoniumEngineException e) {
        return makeErrorResponse(e.getMessage(), e.getStatusCode());
    }

    /**
     * Error Response Creation.
     * @param msg Message
     * @param code Status Code
     * @return JAX-RS Response
     */
    private Response makeErrorResponse(final String msg, final int code) {
        return Response.status(code)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_LENGTH, msg.getBytes().length)
                .entity(msg).build();
    }

    /**
     * Getter for Cell Name.
     * @return Cell Name
     */
    public abstract String getCell();

    /**
     * Getter for Data Schema URI.
     * @return Data Schema URI
     */
    public abstract String getSchemaURI();

    /**
     * Request URL Analysis.
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @return base URL String
     * @throws MalformedURLException
     */
    public final String parseRequestUri(final HttpServletRequest req, final HttpServletResponse res)
            throws MalformedURLException {
        String baseUrl = "";

        // Request URIを取得する。リクエストURI＝＞ホスト名よりあとのクエリ含んだ文字列。
        // (Exameple） /personium-engine/engine-test/ds-engine-test/service/hello?a=b&c=d
        // (Service Behavior） personium-core から送られてきたURIがヘッダに指定されていたらそれを利用する。
        // (Debug Behavior）      Personium-Engine呼び出しのURIから生成する。
        String requestUri = req.getHeader(KEY_HEADER_REQUEST_URI);
        if (requestUri == null || requestUri.length() == 0) {
            requestUri = req.getRequestURI();
            String query = req.getQueryString();
            if (query != null && query.length() > 0) {
                requestUri += "?" + query;
            }
        }

        // Request URIからクエリ部分を切り取って、スクリプト名を抜き出す。
        // (Example) /personium-engine/engine-test/personium-engine-test/service/hello
        int indexQ = requestUri.indexOf("?");
        String scriptName = requestUri;
        if (indexQ > 0) {
            scriptName = requestUri.substring(0, indexQ);
        }

        // ServletRequest オブジェクトに値を保存。あとでJSGIオブジェクトに設定するため。
        req.setAttribute("env.requestUri", requestUri);
        req.setAttribute("scriptName", scriptName);

        // Clientから受け付けたリクエストURL（ホスト？）の取得
        // (Service Behavior) core から送られてきたURLがヘッダに指定されていたらそれを利用する。
        baseUrl = req.getHeader(KEY_HEADER_BASEURL);

        // Set to the attributes of ServletRequest object
        // so that it can be used to configure upon JSGI object.
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
     * Getter for SourceManager.
     * @return ISourceManager
     * @throws PersoniumEngineException Exception about Engine
     */
    public abstract ISourceManager getServiceCollectionManager() throws PersoniumEngineException;
}
