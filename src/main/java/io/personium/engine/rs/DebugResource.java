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
 * Service Class for Debugging.
 */
@Path("{id}")
public class DebugResource extends AbstractService {
    /** Logger Object. */
    private static Log log = LogFactory.getLog(DebugResource.class);
    /** Default Cell Name. */
    private static final String CELL = "engine_debug_cell";
    /** Default Box Name. */
    private static final String SCHEME = "engine_debug_box";

    /**
     * Flag for whether JavaScript Remote Debugging feature should be enabled or not. 
     *  enabled when <code>true</code> is set. 
     */
    private Boolean useScriptDebug;

    /**
     * Constructor.
     * @param useDebug Flag for whether JavaScript remote debug feature should be enabled or not
     * @throws PersoniumEngineException PersoniumEngine Exception
     */
    public DebugResource(@QueryParam("useScriptDebug") final String useDebug) throws PersoniumEngineException {
        // Calling http://xxx/yy?useScriptDebug=true, then Remote ScriptDebugging will be enabled
        super();
        this.useScriptDebug = Boolean.valueOf(useDebug);
        log.info("Create DebugResource. useScriptDebug=" + this.useScriptDebug);
    }

    @Override
    public final String getCell() {
        return CELL;
    }

    @Override
    public final String getSchemaURI() {
        return SCHEME;
    }

    @Override
    public ISourceManager getServiceCollectionManager() throws PersoniumEngineException {
        // Test SourceManager will be returned 
        TestResourceSourceManager sourcemanager = new TestResourceSourceManager();
        return sourcemanager;
    }
}
