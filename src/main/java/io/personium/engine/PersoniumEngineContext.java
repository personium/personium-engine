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
package io.personium.engine;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.client.DaoException;
import io.personium.client.utils.PersoniumLoggerFactory;
import io.personium.common.utils.CommonUtils;
import io.personium.engine.adapter.PersoniumEngineDao;
import io.personium.engine.adapter.PersoniumRequestBodyStream;
import io.personium.engine.adapter.Require;
import io.personium.engine.extension.support.AbstractExtensionScriptableObject;
import io.personium.engine.extension.support.ExtensionJarLoader;
import io.personium.engine.extension.support.ExtensionLogger;
import io.personium.engine.extension.support.IExtensionLogger;
import io.personium.engine.extension.support.JavaClassRevealFilter;
import io.personium.engine.jsgi.JSGIRequest;
import io.personium.engine.jsgi.PersoniumResponse;
import io.personium.engine.model.ScriptCache;
import io.personium.engine.source.ISourceManager;
import io.personium.engine.utils.PersoniumEngineConfig;
import io.personium.engine.utils.PersoniumEngineLoggerFactory;

/**
 * Personium-Engineのメインクラス.
 */
public class PersoniumEngineContext implements Closeable {
    /**    Logger Object.    */
            private        static   Logger log = LoggerFactory.getLogger(PersoniumEngineContext.class);

        private static final String PERSONIUM_SCOPE = "_p";
    private static final String EXTENSION_SCOPE = "extension";
             private static Map<String, Script> engineLibCache = new ConcurrentHashMap<String, Script>();

    private static final int CACHE_MAX_NUM = PersoniumEngineConfig.getScriptCacheMaxNum();
    @SuppressWarnings("serial")
	private static Map<String, ScriptCache> userScriptCache = Collections.synchronizedMap(
            new LinkedHashMap<String, ScriptCache>(16, 0.75f, true) { //CHECKSTYLE IGNORE 16, 0.75 is default.
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ScriptCache> eldest) {
                    return size() > CACHE_MAX_NUM;
                }
            });

    /** Cell Name. */
    private String currentCellName;
    /** Box Name. */
    private String currentBoxName;
    /** Data Schema URI. */
    private String currentSchemeUri;

    /** Rhino Context. */
    private org.mozilla.javascript.Context cx;

    /** Rhino ContextFactory. */
    private PersoniumJsContextFactory factory;
    /** Rhino Scope. */
    private Scriptable scope;

    /** Base URL. */
    private String baseUrl;

    /** Script SourceManager. */
    private ISourceManager sourceManager;


    //XXX Debug
    private StringBuilder timeBuilder;


    static {
        ContextFactory.initGlobal(new PersoniumJsContextFactory());
    }

    /**
     * Constructor.
     * @throws PersoniumEngineException 
     */
    public PersoniumEngineContext() throws PersoniumEngineException {
        // Rhinoの実行環境を作成する
        this.factory = new PersoniumJsContextFactory();
        this.cx = factory.enterContext();
        this.cx.setLanguageVersion(org.mozilla.javascript.Context.VERSION_ES6);

        this.scope = cx.initStandardObjects();

        cx.setOptimizationLevel(-1);
    }

    /**
     * Constructor.
     * @param timeBuilder time string builder
     * @throws PersoniumEngineException exception
     */
    public PersoniumEngineContext(StringBuilder timeBuilder) throws PersoniumEngineException {
        this();
        this.timeBuilder = timeBuilder;
    }

    /**
     * Publish Extension class to JavaScript.
     * この際、ロガークラス実体を Extensionクラス側に設定する。
     * @throws PersoniumEngineException When failing to publish
     */
    private void prepareExtensionClass() throws PersoniumEngineException {
        // Extension用 jarのロード
        ExtensionJarLoader extLoader = null;
        try {
            extLoader = ExtensionJarLoader.getInstance(this.cx.getApplicationClassLoader(),
                    new JavaClassRevealFilter());
            factory.initApplicationClassLoader(extLoader.getClassLoader());
        } catch (IOException e) {
            throw new PersoniumEngineException("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR, e);
        } catch (PersoniumEngineException e) {
            throw e;
        }

        // Javascript内でプロトタイプとして使用可能な Javaクラスを定義する。
        // Scope の設定
        NativeObject pScope = (NativeObject) this.scope.get(PERSONIUM_SCOPE, this.scope);
        NativeObject declaringClass = (NativeObject) pScope.get(EXTENSION_SCOPE, pScope);

        for (Class<? extends Scriptable> clazz : extLoader.getPrototypeClassSet()) {
            try {
                if (AbstractExtensionScriptableObject.class.isAssignableFrom(clazz)) {
                    // AbstractExtensionScriptableObjectであれば、ロガー設定を行う。
                    @SuppressWarnings("unchecked")
                    Class<? extends AbstractExtensionScriptableObject> extensionClazz
                        = (Class<? extends AbstractExtensionScriptableObject>) clazz;
                    // Extensionクラス内で利用可能なロガーを渡す。
                    // この処理の間に例外が発生しても、何も行わず無視する。（ロガー設定はしないまま正常に動作させる。)
                    try {
                        Method setLoggerMethod = extensionClazz.getMethod(
                                "setLogger",
                                new Class[] {
                                        Class.class,
                                        IExtensionLogger.class });
                        setLoggerMethod.setAccessible(true);
                        setLoggerMethod.invoke(null,
                                new Object[] {extensionClazz, new ExtensionLogger(extensionClazz)});
                    } catch (Exception e) {
                        log.info("setLogger method cannot be called.", e);
                    }
                }

                // ############################################################################3
                // 以下のメソッドから例外が出力された場合、スクリプト実行の障害となるが、複数の extensionが導入されている場合、
                // 問題となる extensionを利用していない UserScriptまで実行できなくなるのを防ぐため、ここからは例外は投げない。
                // 問題のプロトタイプにアクセスした場合、Script実行時のエラーとなる。
                // ############################################################################3
                ScriptableObject.defineClass(declaringClass, clazz);
            } catch (RuntimeException e) {
                log.warn(String.format("Warn: Extension class(%s) could not be revealed to javascript.: %s",
                        clazz.getCanonicalName(), e.getMessage()));
            } catch (Exception e) {
                log.warn(String.format("Warn: Extension class(%s) could not be revealed to javascript.: %s",
                        clazz.getCanonicalName(), e.getMessage()));
            }
        }
    }

    /**
     * Setter for ISourceManager.
     * @param value the ISourceManager
     */
    public final void setSourceManager(final ISourceManager value) {
        this.sourceManager = value;
    }

    /**
     * Load Global Object. 
     * TODO method name change 
     * 過去はグローバルオブジェクトを作成する関数だったが、現状は単なるsetterになっている。
     * @param url 基底URL
     * @param cell Cell名
     * @param schema データスキーマURI
     * @param box Box名
     * @param service サービス名
     */
    public final void loadGlobalObject(final String url,
            final String cell,
            final String schema,
            final String box,
            final String service) {
        this.baseUrl = url;
        this.currentCellName = cell;
        this.currentBoxName = box;
        this.currentSchemeUri = schema;
    }

    /**
     * Run JSGI function.
     * @param source 実行するユーザースクリプト
     * @param req Requestオブジェクト
     * @param res Responseオブジェクト
     * @param is リクエストストリームオブジェクト
     * @param serviceSubject サービスサブジェクト
     * @param previousPhaseTime previous phase time
     * @param sourceName source name
     * @return Response
     * @throws PersoniumEngineException PersoniumEngine例外
     */
    public final Response runJsgi(final String source,
            final HttpServletRequest req,
            final HttpServletResponse res,
            final InputStream is,
            final String serviceSubject,
            long previousPhaseTime,
            String sourceName) throws PersoniumEngineException {
        // JSGI実行準備
        // create java-client Contenxt Object
        PersoniumEngineDao ed = createDao(req, serviceSubject);

        // Set the Java-client Context Object accessible from "pjvm" in JavaScript
        javaToJs(ed, "pjvm");

        // Set the Require Object acessible with global object "_require" in JavaScript
        javaToJs(createRequireObject(), "_require");

        // Load JS files
        //  "personium-dao.js" 
        try {
            loadJs("personium-dao");
        } catch (IOException e1) {
            log.info("runJsgi error (DAO load io error) ", e1);
            throw new PersoniumEngineException("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        //  "personium-lib.js"
        try {
            loadJs("personium-lib");
        } catch (IOException e1) {
            log.info("runJsgi error (personium-lib load io error) ", e1);
            throw new PersoniumEngineException("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        //  jsgi-lib.js
        try {
            loadJs("jsgi-lib");
        } catch (IOException e1) {
            log.info("runJsgi error (jsgi-lib load io error) ", e1);
            throw new PersoniumEngineException("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        // p名前空間に、Extensionのクラス群を定義する。
        prepareExtensionClass();

        // RequestオブジェクトをJavaScriptプロパティへ設定
        JSGIRequest jsReq = new JSGIRequest(req, new PersoniumRequestBodyStream(is));

        // JSGI Execution
        //  User Script を実行(eval)する
        try {
            Object ret;
            log.info("eval user script : script size = " + source.length());
            ret = evalUserScript(source, jsReq, previousPhaseTime, sourceName);
            log.info("[" + PersoniumEngineConfig.getVersion() + "] " + "<<< Request Ended ");

            PersoniumResponse pRes = PersoniumResponse.parseJsgiResponse(ret);

            return pRes.build();
        } catch (Error e) {
        	// In case of the user script timeout
            // log output at INFO level 
            log.info("UserScript TimeOut", e);
            throw new PersoniumEngineException("Script TimeOut", HttpStatus.SC_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            if (e instanceof WrappedException) {
                e = (Exception) ((WrappedException) e).getWrappedException();
            }

            // In case of exceptions inside user scripts
            // Log them with INFO level
            log.info("User Script Evalucation Error : " + e.getMessage(), e);
            throw new PersoniumEngineException("Server Error : " + e.getMessage(),
                    PersoniumEngineException.STATUSCODE_SERVER_ERROR,
                    e);
        }
    }

    /**
     * UserScript Evaluation.
     * @param source User Script Source
     * @throws IOException
     * @throws PersoniumEngineException
     */
    private Object evalUserScript(final String source, JSGIRequest jsReq, long previousPhaseTime, String sourceName)
            throws PersoniumEngineException, ClassNotFoundException, IOException {

        long nowTime = System.currentTimeMillis();
        previousPhaseTime = nowTime;

        Script script;
        script = sourceManager.getCachedScript(sourceName, userScriptCache);
        if (script == null) {
            script = cx.compileString("fn_jsgi = " + source, null, 1, null);
            sourceManager.createCachedScript(script, sourceName, userScriptCache);

            nowTime = System.currentTimeMillis();
            timeBuilder.append("Phase-compile,");
            timeBuilder.append(nowTime - previousPhaseTime);
            timeBuilder.append(",");
            previousPhaseTime = nowTime;
        } else {
            nowTime = System.currentTimeMillis();
            timeBuilder.append("Phase-cache,");
            timeBuilder.append(nowTime - previousPhaseTime);
            timeBuilder.append(",");
            previousPhaseTime = nowTime;
        }


        if (script != null) {
            script.exec(cx, scope);
        }
        nowTime = System.currentTimeMillis();
        timeBuilder.append("Phase-exec,");
        timeBuilder.append(nowTime - previousPhaseTime);
        timeBuilder.append(",");
        previousPhaseTime = nowTime;

        Object fObj = scope.get("fn_jsgi", scope);
        Object result = null;
        if (!(fObj instanceof Function)) {
            log.warn("fn_jsgi not found");
            throw new PersoniumEngineException("Server Error", PersoniumEngineException.STATUSCODE_SERVER_ERROR);
        }

        Object[] functionArgs = {jsReq.getRequestObject() };

        previousPhaseTime = System.currentTimeMillis();

        Function f = (Function) fObj;
        result = f.call(cx, scope, scope, functionArgs);

        nowTime = System.currentTimeMillis();
        timeBuilder.append("Phase-call,");
        timeBuilder.append(nowTime - previousPhaseTime);
        timeBuilder.append(",");
        previousPhaseTime = nowTime;

        return result;
    }

    /**
     * DAOオブジェクトを作成.
     * @param req Requestオブジェクト
     * @param serviceSubject サービスサブジェクト
     * @return DAOオブジェクト
     * @throws PersoniumEngineException PersoniumEngineException
     */
    private PersoniumEngineDao createDao(final HttpServletRequest req, final String serviceSubject)
            throws PersoniumEngineException {
        PersoniumEngineLoggerFactory engLogFactory = new PersoniumEngineLoggerFactory();
        PersoniumLoggerFactory.setDefaultFactory(engLogFactory);

        String pathBaseHeader = req.getHeader("X-Personium-Path-Based-Cell-Url-Enabled");
        boolean pathBase = StringUtils.isEmpty(pathBaseHeader);
        if (!pathBase) {
            pathBase = Boolean.parseBoolean(pathBaseHeader);
        }

        PersoniumEngineDao pcx;
        try {
            pcx = new PersoniumEngineDao(baseUrl, currentCellName, currentSchemeUri, currentBoxName, pathBase);
        } catch (DaoException e) {
            log.info("DAO init error) ", e);
            throw new PersoniumEngineException("Server Error : " + e.getMessage(),
                    PersoniumEngineException.STATUSCODE_SERVER_ERROR, e);
        }
        pcx.setServiceSubject(serviceSubject);
        pcx.setBoxSchema(req.getHeader("X-Personium-Box-Schema"));

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        String version = req.getHeader(PersoniumEngineDao.PERSONIUM_VERSION);
        if (version != null && !(version.equals(""))) {
            pcx.setPersoniumVersion(version);
        }
        log.debug("auth : --------------------------------------------------------------------------");
        log.debug(auth);
        if (auth != null && auth.length() > "Bearer".length()) {
            pcx.setClientToken(auth.substring("Bearer".length()).trim());
        }
        // set defaultHeaders
        String requestKey = req.getHeader(CommonUtils.HttpHeaders.X_PERSONIUM_REQUESTKEY);
        if (requestKey != null) {
            pcx.setDefaultHeader(CommonUtils.HttpHeaders.X_PERSONIUM_REQUESTKEY, requestKey);
        }
        String eventId = req.getHeader(CommonUtils.HttpHeaders.X_PERSONIUM_EVENTID);
        if (eventId != null) {
            pcx.setDefaultHeader(CommonUtils.HttpHeaders.X_PERSONIUM_EVENTID, eventId);
        }
        String ruleChain = req.getHeader(CommonUtils.HttpHeaders.X_PERSONIUM_RULECHAIN);
        if (ruleChain != null) {
            pcx.setDefaultHeader(CommonUtils.HttpHeaders.X_PERSONIUM_RULECHAIN, ruleChain);
        }
        String via = req.getHeader(CommonUtils.HttpHeaders.X_PERSONIUM_VIA);
        if (via != null) {
            pcx.setDefaultHeader(CommonUtils.HttpHeaders.X_PERSONIUM_VIA, via);
        }
        pcx.setDefaultHeader("Connection", "close");

        if (log.isDebugEnabled()) {
            log.debug("runJsgi.");
            log.debug("    X-Personium-RequestKey: " + requestKey);
            log.debug("    X-Personium-EventId: " + eventId);
            log.debug("    X-Personium-RuleChain: " + ruleChain);
            log.debug("    X-Personium-Via: " + via);
        }
        return pcx;
    }

    /**
     * Requireオブジェクトを作成.
     * @param localPath ローカル実行時のソースパス
     * @return 生成したRequireオブジェクト
     */
    private Require createRequireObject() {
        Require requireComp = new Require(this);
        requireComp.setSourceManager(this.sourceManager);
        log.debug("RequireObject created");
        return requireComp;
    }

    /**
     * JavaScriptファイルを解析し、オブジェクトに登録.
     * @param name JavaScriptソース名
     * @throws IOException IO例外
     */
    private Object loadJs(final String name) throws IOException {
        URL path = getClass().getResource("/js-lib/" + name + ".js");

        Script jsBuildObject = null;
        if (engineLibCache.containsKey(path.toString())) {
            jsBuildObject = engineLibCache.get(path.toString());
        } else {
            FileInputStream fis = new FileInputStream(URLDecoder.decode(path.getFile(), CharEncoding.UTF_8));
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(fis, "UTF-8");
                jsBuildObject = cx.compileReader(isr, path.getPath(), 1, null);
            } finally {
                IOUtils.closeQuietly(isr);
                IOUtils.closeQuietly(fis);
            }
            engineLibCache.put(path.toString(), jsBuildObject);
        }
        if (jsBuildObject == null) {
            return null;
        }
        Object ret = jsBuildObject.exec(cx, scope);
        log.debug("Load JavaScript from Local Resource : " + path);
        return ret;
    }

    /**
     * JavaのオブジェクトをJavaScriptオブジェクトに変換.
     * @param obj Javaオブジェクト
     * @param propertyName JavaScriptオブジェクトの変数名
     */
    private void javaToJs(final Object obj, final String propertyName) {
        log.debug("JavaObject to JavaScriptProperty " + propertyName);
        Object jObj = org.mozilla.javascript.Context.javaToJS(obj, scope);
        ScriptableObject.putProperty(scope, propertyName, jObj);
    }

    /**
     * JavaScriptファイルを解析し、オブジェクトに登録.
     * @param source JavaScriptソースの中身
     * @param path JavaScriptソース名
     * @return オブジェクト
     * @throws PersoniumEngineException exception
     */
    public Object requireJs(final String source, final String path) throws PersoniumEngineException {
        long previousPhaseTime = System.currentTimeMillis();

        StringBuilder builder = new StringBuilder();
        // Add because there is no extension.
        String jsName = path + ".js";
        Object ret = null;
        Script script = sourceManager.getCachedScript(jsName, userScriptCache);
        if (script == null) {
            script = cx.compileString(source, path, 1, null);
            sourceManager.createCachedScript(script, jsName, userScriptCache);
            builder.append("========== Require timestamp. ");
            builder.append("Compile,");
        } else {
            builder.append("========== Require timestamp. ");
            builder.append("Cache,");
        }
        builder.append(System.currentTimeMillis() - previousPhaseTime);
        builder.append(",");
        previousPhaseTime = System.currentTimeMillis();

        if (script != null) {
            ret = script.exec(cx, scope);
        }

        builder.append("Exec,");
        builder.append(System.currentTimeMillis() - previousPhaseTime);

        log.debug("Load JavaScript from Require Resource : " + path);
        log.debug(builder.toString());

        return ret;
    }

    @Override
    public void close() throws IOException {
        PersoniumJsContext.exit();
    }
}
