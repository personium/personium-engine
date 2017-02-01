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
package io.personium.engine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.client.utils.PersoniumLogger;

/**
 * Personium-Engineログクラス.
 */
public class PersoniumEngineLogger implements PersoniumLogger {
    // ログオブジェクト
    private Logger log;

    /**
     * コンストラクタ.
     * @param clazz クラス
     */
    @SuppressWarnings("rawtypes")
    public PersoniumEngineLogger(Class clazz) {
        log = LoggerFactory.getLogger(clazz);
    }

    /**
     * デバッグ情報出力.
     * @param msg 出力メッセージ
     */
    public void debug(String msg) {
        log.debug(msg);
    }
}
