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
 * ExtCellのCRUDテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("./testCommon");

    // クエリを解析し、Cell名を取得する
    var query = _p.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var data = {};
    
    try {
        // 対象Cellのオブジェクト取得
        var cell2 = _p.as("client").cell(cellName + "1");
        data["Url"] = cell2.getUrl();
        
        // ExtCell作成
        var extcell = _p.as("client").cell(cellName).ctl.extCell.create(data);

        // 同じ名前のExtCellを登録し、409になることを確認
        try {
            _p.as("client").cell(cellName).ctl.extCell.create(data);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // ExtCellManager.prototype.retrieve 01

        // 作成したExtCellを取得
        role = _p.as("client").cell(cellName).ctl.extCell.retrieve(extcell.url);

        // 作成したExtCellを削除する
        _p.as("client").cell(cellName).ctl.extCell.del(extcell.url);
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}