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
package io.personium.engine.adapter;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.personium.engine.PersoniumEngineContext;
import io.personium.engine.PersoniumEngineException;
import io.personium.engine.source.ISourceManager;

/**
 * Require読み込み用クラス.
 */
public class Require {
    /** PersoniumEngineContext. */
    private PersoniumEngineContext context;

    /** Sourc Manager. */
    private ISourceManager sourceManager;

    /** Logger Object. */
    private static Log log = LogFactory.getLog(Require.class);

    static {
        log.getClass();
    }
    /**
     * Constructor.
     * @param context PersoniumContext
     */
    public Require(PersoniumEngineContext context) {
        this.context = context;
    }

    /**
     * Require Execution.
     * @param moduleName require対象モジュール名
     * @return Require result
     * @throws PersoniumEngineException Exception about Engine
     */
    public Object doRequire(String moduleName) throws PersoniumEngineException {
        String source = this.sourceManager.getSource(moduleName + ".js");
        Date date = new Date();
        Long time = date.getTime();
        String key = moduleName + time.toString();
        source = key + " = function() {};(function(exports) {" + source + "})(" + key + ");" + key;

        return this.context.requireJs(source, moduleName);
    }

    /**
     * Setter for source manager.
     * @param sourceManager the ISourceManager
     */
    public final void setSourceManager(final ISourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
}
