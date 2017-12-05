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
package io.personium.engine.source;

import io.personium.engine.PersoniumEngineException;

/**
 * ユーザースクリプトのソースを管理するインターフェース.
 */
public interface ISourceManager {

    /**
     * サービスサブジェクトを返却.
     * @return サービスサブジェクト
     * @throws PersoniumEngineException
     */
    String getServiceSubject() throws PersoniumEngineException;
    /**
     * サービス名に対応したスクリプトを返却.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     * @throws PersoniumEngineException
     */
    String getScriptNameForServicePath(String servicePath) throws PersoniumEngineException;

    /**
     * スクリプトファイルの中身を返却.
     * @param scriptFileName スクリプトファイル名
     * @return スクリプトファイルの中身
     * @throws PersoniumEngineException
     */
    String getSource(String scriptFileName) throws PersoniumEngineException;
}
