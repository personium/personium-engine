/*
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
_p.pjvm = pjvm;
_p.dcjvm = pjvm;

_p.util = {};

_p.util.obj2javaJson = function(obj) {
    return _p.pjvm.newPersoniumJSONObject(JSON.stringify(obj));
};


_p.util.escape4xss = function(obj) {
    if (typeof obj === 'string') {
        return obj.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&apos;"); 
    } else if (typeof obj === 'Object') {
        var ret = {};
        for (var k in obj) {
            ret[k] = _p.util.escape4xss(obj[k]);
        }
    } else if (typeof obj === 'Array') {
        var ret = {};
        for (var k in obj) {
            ret[k] = _p.util.escape4xss(obj[k]);
        }
    } else{
        return obj;
    }
};
    
_p.util.require =  function(path) {
    _require.load(path);
};

_p.util.queryParse = function(queryString, charset){
    
    charset = (charset == undefined)?"utf-8":charset;
    
    var query = {};
    var params = queryString.split("&");
    for ( var param in params){
        var kv = params[param].split("=");
        if (kv < 2) {
            continue;
        }

        var key = _p.util.decodeURI(kv[0],charset);
        var value = _p.util.decodeURI(kv[1],charset);
        if (query[key]){
            if ( typeof(query[key]) !== "object"){
                query[key] = [query[key]];
            }
            query[key].push(value);
        } else {
            query[key] = value;
        }

    }
    return query;
};
    
_p.util.decodeURI = function(str, charset) {
     // 以下の処理で、.toString() + "" をしている理由
     // 　そのままハッシュデータを作成し、JSON.stringify を行うと、
     // 　「Java class "[B" has no public instance field or method named "toJSON"」
     // 　というようなExceptionが発生する。
     // まったく同じ形式のＪＳＯＮを手作業で作成した場合は、JSON.stringifyが成功する。
     //  値の形式が文字列になっていないようなので、「.toString() + ""」をしてみた。
     // そしたら、うまく動いた。
    return pjvm.decodeURI(str, charset).toString() + "";
};

/**
 * Require機能.
 * @param path パス
 * @returns 読み込まれた拡張オブジェクト
 */
function require(path) {
	return _require.doRequire(path);
}
