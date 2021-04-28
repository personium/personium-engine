/**
 * Personium
 * Copyright 2014-2021 - 2019 Personium Project Authors
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

import java.util.Map;

import org.mozilla.javascript.Script;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.model.ScriptCache;

/**
 * Interface for managing Engine Script sources.
 */
public interface ISourceManager {

    /**
     * Getter for Service Subject.
     * @return Service Subject
     * @throws PersoniumEngineException exception
     */
    String getServiceSubject() throws PersoniumEngineException;
    /**
     * Corresponding script File name is returned when given a service name.
     * @param servicePath Service Name
     * @return Script File Name
     * @throws PersoniumEngineException exception
     */
    String getScriptNameForServicePath(String servicePath) throws PersoniumEngineException;

    /**
     * Create script cache.
     * @param script script
     * @param sourceName script source name
     * @param userScriptCache script cache map
     * @throws PersoniumEngineException exception
     */
    void createCachedScript(Script script, String sourceName, Map<String, ScriptCache> userScriptCache)
            throws PersoniumEngineException;

    /**
     * Get script cache.
     * @param sourceName script source name
     * @param userScriptCache script cache map
     * @return script cache
     * @throws PersoniumEngineException exception
     */
    Script getCachedScript(String sourceName, Map<String, ScriptCache> userScriptCache) throws PersoniumEngineException;

    /**
     * Getter for Script file content.
     * @param scriptFileName Script File Name
     * @return Script file content
     * @throws PersoniumEngineException exception
     */
    String getSource(String scriptFileName) throws PersoniumEngineException;
}
