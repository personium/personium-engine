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
package io.personium.engine.source;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.mozilla.javascript.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.model.ScriptCache;
import io.personium.engine.utils.PersoniumUtils;

/**
 * サービスコレクションの情報を管理する.
 */
public class TestResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(TestResourceSourceManager.class);

    /**
     * コンストラクタ.
     */
    public TestResourceSourceManager() {
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     * @throws PersoniumEngineException exception
     */
    public String getServiceSubject() throws PersoniumEngineException {
        return "engine";
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     * @throws PersoniumEngineException exception
     */
    public String getScriptNameForServicePath(String servicePath) throws PersoniumEngineException {
        // テスト用リソース動作時はURLで呼び出されたフィル名と実行スクリプト名が同一
        return servicePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createCachedScript(Script script, String sourceName, Map<String, ScriptCache> userScriptCache)
            throws PersoniumEngineException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script getCachedScript(String sourceName, Map<String, ScriptCache> userScriptCache)
            throws PersoniumEngineException {
        return null;
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws PersoniumEngineException exception
     */
    public String getSource(String sourceName) throws PersoniumEngineException {
        try {
            URL path = getClass().getResource("/service/" + sourceName);

            return PersoniumUtils.readFile(URLDecoder.decode(path.getFile(), CharEncoding.UTF_8));
        } catch (Exception e) {
            log.info("CouchClientException msg:" + e.getMessage() + ",svcName:" + sourceName);
            log.info("UserScript read error ", e);
            throw new PersoniumEngineException("404 Not Found", PersoniumEngineException.STATUSCODE_NOTFOUND, e);
        }
    }
}

