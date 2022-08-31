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
 * Role - Account リンクテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("./testCommon");

    // クエリを解析し、Cell名を取得する
    var query = _p.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var user = {Name:"user"};
    var password = "password";
    try {
        // アカウント作成
        var account = _p.as("client").cell(cellName).ctl.account.create(user, password);

        // Role作成
        data = {Name:"linkRoleAccount_role"};
        var role = _p.as("client").cell(cellName).ctl.role.create(data);
        // Role リンク
        role.account.link(account);
        // Role リンク
        try {
            role.account.link(account);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // Roleアンリンク
        role.account.unLink(account);
        // Roleアンリンク (404)
        try {
            role.account.unLink(account);
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したアカウントを取得する
        _p.as("client").cell(cellName).ctl.account.del(account.name);
        // 作成したRoleを削除する
        _p.as("client").cell(cellName).ctl.role.del({Name:"linkRoleAccount_role"});

        // レスポンスを返却
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {

    }
}