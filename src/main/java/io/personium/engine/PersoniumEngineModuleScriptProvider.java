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
package io.personium.engine;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

import io.personium.engine.source.ISourceManager;

public class PersoniumEngineModuleScriptProvider implements ModuleScriptProvider{

    /** File Extension for engine script */
    private final String engineScriptExt = ".js";

    /** PersoniumEngineContext. */
    private PersoniumEngineContext context;

    /** Sourc Manager. */
    private ISourceManager sourceManager;

    /** Logger Object. */
    private static Log log = LogFactory.getLog(PersoniumEngineModuleScriptProvider.class);

    static {
        log.getClass();
    }
    /**
     * Constructor.
     * @param context PersoniumContext
     */
    public PersoniumEngineModuleScriptProvider(PersoniumEngineContext context) {
        this.context = context;
    }

    /**
     * Setter for source manager.
     * @param sourceManager the ISourceManager
     */
    public final void setSourceManager(final ISourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    /**
     * @inheritDoc
     */
    @Override
    public ModuleScript getModuleScript(Context cx, String moduleId, URI moduleUri, URI baseUri, Scriptable paths) throws Exception, IllegalArgumentException {
        try {
            String scriptFileName = moduleId + engineScriptExt;
            String source = this.sourceManager.getSource(scriptFileName);
            Script script = this.context.getScript(source, scriptFileName);
            ModuleScript res =  new ModuleScript(script, moduleUri, baseUri);
            return res;
        } catch(EvaluatorException e) {
            // illegal script
            throw new IllegalArgumentException(e);
        }
    }
}
