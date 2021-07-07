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
 * Accessorのリフレッシュトークン認証テスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("./testCommon");
    var responseBody = "";
    // クエリを解析し、Cell名を取得する
    var query = _p.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var json = {cellUrl:cellName, userId:"user001", password:"pass001"};
    var expecte_pode = new Array();
    var code = new Array();
    var message = new Array();;
    try {
        var cell = _p.as(json).cell();
        
        var token = cell.getToken();

        // refreshTokenに空文字
        json = {cellUrl:cellName, refreshToken:""};
        // リフレッシュトークン認証
        try {
            _p.as(json).cell();
        } catch (e) {
            // サーバから認証エラーエラーコード400が返る
            if (e.code != 400) {
                expecte_pode.push("400");
                code.push(e.code);
                message.push(e.message);
            }
        }

        // refreshTokenにnull
        json = {cellUrl:cellName, refreshToken:null};
        // リフレッシュトークン認証
        try {
            _p.as(json).cell();
        } catch (e) {
            // エラーコード0と"Parameter Invalid"が返る。
            if (e.code != 0) {
                expecte_pode.push("0");
                code.push(e.code);
                message.push(e.message);
            }
        }

        // refreshTokenに数字
        json = {cellUrl:cellName, refreshToken:88888};
        // リフレッシュトークン認証
        try {
            _p.as(json).cell();
        } catch (e) {
            // エラーコード0と"Parameter Invalid"が返る。
            if (e.code != 0) {
                expecte_pode.push("0");
                code.push(e.code);
                message.push(e.message);
            }
        }

        // レスポンスを返却
        if (code.length > 0){
            var response = "";
            for (var i = 0;i < code.length;i++) {
                response = response + ",expected:code=" + expecte_pode[i] + " but:code=" + code[i] + " message=" + message[i] + ".";
            }
            return util.response().responseBody(response).build();
        } else {
            return util.response().responseBody("OK").build();
        }

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}