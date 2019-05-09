package io.personium.engine.model;

import org.mozilla.javascript.Script;

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
