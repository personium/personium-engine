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
 * Role(複合キー)のCRUDテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = _p.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var data = {Name:"role","_Box.Name":"boxName"};
    try {
        // Box作成
        _p.as("client").cell(cellName).ctl.box.create({Name:"boxName", Schema:null});

        // Role作成
        var role = _p.as("client").cell(cellName).ctl.role.create(data);

        // 同じ名前のRoleを登録し、409になることを確認
        try {
            _p.as("client").cell(cellName).ctl.role.create(data);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したRoleを取得
        role = _p.as("client").cell(cellName).ctl.role.retrieve({Name:"role","_Box.Name":"boxName"});

        // 作成したRoleを削除する
        _p.as("client").cell(cellName).ctl.role.del({Name:"role","_Box.Name":"boxName"});

        // Box削除
        _p.as("client").cell(cellName).ctl.box.del("boxName");
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}