/*
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
/**
 * 呼び出しを許しているwrapperパッケージのクラスのコンストラクタが呼び出せないこと.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("./testCommon");

    var code = 500;
    var message = "NG";
    try {
        new Packages.io.personium.engine.extension.wrapper.PersoniumInputStream(request['input'].stream());
    } catch (e) {
        if (e.message !== "not found") {
            code = e.code;
            message = e.message;
            return util.response().statusCode(code).responseBody(message).build();
        }
    }
    try {
        new Packages.io.personium.engine.wrapper.PersoniumJSONObject();
    } catch (e) {
        if (e.message !== "not found") {
            code = e.code;
            message = e.message;
            return util.response().statusCode(code).responseBody(message).build();
        }
    }
    try {
        new Packages.io.personium.engine.adapter.PersoniumRequestBodyStream(request['input'].stream());
    } catch (e) {
        if (e.message !== "not found") {
            code = e.code;
            message = e.message;
            return util.response().statusCode(code).responseBody(message).build();
        }
    }
    // レスポンスを返却
    return util.response().responseBody("OK").build();
}


