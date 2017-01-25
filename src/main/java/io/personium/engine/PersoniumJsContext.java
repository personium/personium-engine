/**
 * Personium
 * Copyright 2014 - 2017 FUJITSU LIMITED
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

import org.apache.http.HttpStatus;
import org.mozilla.javascript.Context;

/**
 * javascript.Contextの派生クラス.
 */
public class PersoniumJsContext extends Context {
    /** タイムアウト値. */
    private long timeout;

    /**
     * コンストラクタ.
     */
    @SuppressWarnings("deprecation")
    public PersoniumJsContext() {
    }

    /**
     * タイムアウト値の設定.
     * @param value タイムアウト値
     */
    public final void setTimeout(final long value) {
        this.timeout = value;
    }

    /**
     * タイムアウト値のチェック.
     * @throws PersoniumEngineException DcEngine例外
     */
    public final void checkTimeout() throws PersoniumEngineException {
        if (timeout < System.currentTimeMillis()) {
            throw new PersoniumEngineException("JavaScript Timeout", HttpStatus.SC_SERVICE_UNAVAILABLE);
        }
    }
}
