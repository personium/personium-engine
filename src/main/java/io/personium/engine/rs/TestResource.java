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
package io.personium.engine.rs;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.source.ISourceManager;
import io.personium.engine.source.TestResourceSourceManager;

/**
 * JAX-RS Resource class for Testing.
 */
@Path("/{cell}/{schema}/test/{id : .+}")
public class TestResource extends AbstractService {
    /** Logger Object. */
    private static Log log = LogFactory.getLog(TestResource.class);

    /**
     * JavaScript リモートデバッグ機能を有効にするかどうかを指定する. <code>true</code>を指定した場合、有効となる。
     */
    private Boolean useScriptDebug;

    /**
     * Constructor.
     * @param useDebug JavaScript リモートデバッグ機能を有効にするかどうかを指定する
     * @throws PersoniumEngineException PersoniumEngine例外
     */
    public TestResource(@QueryParam("useScriptDebug") final String useDebug) throws PersoniumEngineException {
        // http://xxx/yy?useScriptDebug=true と指定した場合、useScriptDebugの値が、本パラメタのuseDebugに入る。
        super();
        this.serviceSubject = "engine"; // デフォルト値を"engine"とする
        this.useScriptDebug = Boolean.valueOf(useDebug);
        log.info("Create DebugResource. useScriptDebug=" + this.useScriptDebug);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getCell() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getSchemaURI() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISourceManager getServiceCollectionManager() throws PersoniumEngineException {
        // returning TestResourceSourceManager instance
        TestResourceSourceManager sourcemanager = new TestResourceSourceManager();
        return sourcemanager;
    }
}
