/**
 * Personium
 * Copyright 2019 FUJITSU LIMITED
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
package io.personium.engine.model;

import org.mozilla.javascript.Script;

/**
 * Script cache object class.
 */
public class ScriptCache {

    /** Script cache. */
    private Script script;
    /** Script file update time. */
    private Long updateTime;

    /**
     * Constructor.
     * @param script Script cache
     * @param updateTime Script file update time
     */
    public ScriptCache(Script script, Long updateTime) {
        this.script = script;
        this.updateTime = updateTime;
    }

    /**
     * Get script cache.
     * @return Script cache
     */
    public Script getScript() {
        return script;
    }

    /**
     * Get script file update time.
     * @return Script file update time
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * Check script file updated.
     * @param latestUpdateTime Latest update time
     * @return true:updated, false:no update
     */
    public boolean isScriptFileUpdated(Long latestUpdateTime) {
        if (latestUpdateTime == null) {
            return false;
        }
        if (updateTime == null) {
            return true;
        }
        return updateTime < latestUpdateTime;
    }
}
