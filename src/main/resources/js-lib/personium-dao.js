/*
 * Personium
 * Copyright 2014 - 2018 FUJITSU LIMITED
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
 * @fileOverview
 * @name personium-dao.js
 * @version 1.4.2
 */

/**
 * Personium Core APIを操作するためのDAOライブラリ.
 * 互換性のためdcでも動くようにする.
 * @class _p
 */
var _p = {};

var dc = _p;
_p.extension = {};

/**
 * Return localbox.<br>
 * @returns {_p.Box} Box object where the engine script is running
 * @exception {_p.PersoniumException} DAO exception
 */
_p.localbox = function() {
    try {
        return _p.as('client').cell().box();
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    };
};

/**
 * アクセス主体を指定.
 * @param {Object} param アクセス主体を指定するパラメタ
 * @returns {_p.Accessor} 生成したAccessorオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.as = function(param) {
    try {
        if (typeof param == 'string') {
            if ( param == 'serviceSubject' ) {
                var as = pjvm.asServiceSubject();
                return new _p.Accessor(as);
            } else if ( param == 'client' ) {
                return new _p.Accessor(pjvm.withClientToken());
            }
        } else {
            if ((typeof param.cellUrl == "string") && (typeof param.userId == "string") &&
                    (typeof param.password == "string") && (typeof param.schemaUrl == "string") &&
                    (typeof param.schemaUserId == "string") && (typeof param.schemaPassword == "string")) {
                // スキーマ付きパスワード認証
                return new _p.Accessor(pjvm.asAccountWithSchemaAuthn(param.cellUrl, param.userId, param.password,
                        param.schemaUrl, param.schemaUserId, param.schemaPassword));
            } else if ((typeof param.cellUrl == "string") && (typeof param.accessToken == "string") &&
                        (typeof param.schemaUrl == "string") && (typeof param.schemaUserId == "string") &&
                        (typeof param.schemaPassword == "string")) {
                // スキーマ付きトークン認証
                return new _p.Accessor(pjvm.getAccessorWithTransCellTokenAndSchemaAuthn(param.cellUrl, param.accessToken,
                        param.schemaUrl, param.schemaUserId, param.schemaPassword));
            } else if ((typeof param.cellUrl == "string") && (typeof param.userId == "string") &&
                    (typeof param.password == "string")) {
                // パスワード認証
                return new _p.Accessor(pjvm.asAccount(param.cellUrl, param.userId, param.password));
            } else if ((typeof param.cellUrl == "string") && (typeof param.accessToken == "string")) {
                // トークン認証
                return new _p.Accessor(pjvm.getAccessorWithTransCellToken(param.cellUrl, param.accessToken));
            } else if ((typeof param.cellUrl == "string") && (typeof param.refreshToken == "string")) {
                // フレッシュトークン認証
                return new _p.Accessor(pjvm.getAccessorWithRefreshToken(param.cellUrl, param.refreshToken));
            } else if (typeof param.accessToken == "string") {
                // トークン指定
                return new _p.Accessor(pjvm.withToken(param.accessToken));
            }
        }
        throw new _p.PersoniumException("Parameter Invalid");
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Personiumのバージョン指定.<br>
 * 例：<br>
 *   _p.setPersoniumVersion("1.0");
 * @param {string} version バージョン番号. サーバーに対し、X-Tritium-Versionヘッダに指定するバージョン文字列.
 */
_p.setPersoniumVersion = function(version) {
    pjvm.setPersoniumVersion(version);
};

/**
 * Personiumのバージョン指定.<br>
 * 例：<br>
 *   _p.setPersoniumVersion("1.0");
 * @param {string} version バージョン番号. サーバーに対し、X-Personium-Versionヘッダに指定するバージョン文字列.
 */
_p.getServerVersion = function() {
    return pjvm.getServerVersion();
};

/**
 * サーバー通信を非同期にするかどうか(V0のログ機能のみ対応)<br>
 * 例：<br>
 *   _p.setThreadable(true);
 * @param {Boolean} value true:非同期、false:同期
 */
_p.setThreadable = function(value) {
    pjvm.setThreadable(value);
};

/**
 * 新しいAccessorオブジェクトを作成する.
 * @class アクセス主体クラス
 */
_p.Accessor = function(obj) {
    this.core = obj;
};

/**
 * Cell指定.<br>
 * 例：<br>
 * 省略：<br>
 *   as("client").cell();</blockquote>
 * Cell名指定：<br>
 *   as("client").cell("cellName");
 * URL指定：<br>
 *   as("client").cell("http://xxx.com/cellName");
 * @param {string} url CellのID。省略した場合はデフォルトのCellを利用.<br>
 * @returns {_p.Cell} 新しく作成したCellオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Accessor.prototype.cell = function(url) {
    try {
        if (typeof url == 'string') {
            return new _p.Cell(this.core.cell(url));
        } else {
            return new _p.Cell(this.core.cell());
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Unitユーザへの昇格.<br>
 * @returns {_p.Cell} 新しく作成したOwnerAccessorオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Accessor.prototype.asCellOwner = function() {
    try {
        return new _p.OwnerAccessor(this.core.asCellOwner());
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * パスワード変更.<br>
 * 例：<br>
 * 省略：<br>
 *   as("client").changePassword(newPassword);</blockquote>
 * 更新するパスワード：<br>
 *   as("client").changePassword(newPassword);
 * @param {string} newPassword 新しいパスワード.<br>
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Accessor.prototype.changePassword = function(newPassword) {
    try {
        this.core.changePassword(newPassword);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいOwnerAccessorオブジェクトを作成する.
 * @class オーナーアクセス主体クラス
 * @property {_p.UnitManager} unit Unit操作のためのプロパティ
 */
_p.OwnerAccessor = function(obj) {
    this.core = obj;
    this.unit = new _p.UnitManager(this.core.unit);
};

_p.OwnerAccessor.prototype = new _p.Accessor();

/**
 * ODataのCRUDを行う抽象クラス.
 * @class OData操作クラス
 */
_p.OData = function() {};

// ODataデータを登録する(内部関数).
_p.OData.prototype.internalCreate = function(json) {
//    try {
        return this.core.create(_p.util.obj2javaJson(json));
//    } catch (e) {
//        throw new _p.PersoniumException(e.message);
//    }
};

// ODataデータを更新する(内部関数).
_p.OData.prototype.internalUpdate = function(id, json, etag) {
    if (!etag) {
        etag = "*";
    }
//    try {
        this.core.update(id, _p.util.obj2javaJson(json), etag);
//    } catch (e) {
//        throw new _p.PersoniumException(e.message);
//    }
};

// ODataデータを取得する(内部関数).
_p.OData.prototype.internalRetrieve = function(id) {
//    try {
        return this.core.retrieve(id);
//    } catch (e) {
//        throw new _p.PersoniumException(e.message);
//    }
};

/**
 * ODataデータを削除する.
 * @param {string} id 削除するデータのID
 * @param {string} etag 削除するデータのEtag
 * @exception {_p.PersoniumException} DAO例外
 */
_p.OData.prototype.del = function(id, etag) {
    if (!etag) {
        etag = "*";
    }
    try {
        this.core.del(id, etag);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ODataデータの一覧取得のためのQueryオブジェクトを生成する.<br>
 * 例：<br>
 *   odate("odata").entitiSet("ent").query().run();
 * @returns {_p.Query} Queryオブジェクト
 */
_p.OData.prototype.query = function() {
    return new _p.Query( this.core.query() );
};

/**
 * ACLの操作を行う抽象クラス.
 * @class ACL操作クラス
 */
_p.AclManager = function() {
    this.core = null;
};

/**
 * ACLを設定する.<br>
 * @param {json} param 設定するJSON
 * 設定するJSONの例）
 * {
 *   "requireSchemaAuthz": "public",
 *   "ace": [
 *     {
 *       "role": {Roleのobject},
 *       "privilege": [
 *         "read",
 *         "write"
 *       ]
 *     },
 *     {
 *       "role": {Roleのobject},
 *       "privilege": [
 *         "read",
 *         "read-acl"
 *       ]
 *     }
 *   ]
 * }
 *
 * @memberOf Acl#
 */
_p.AclManager.prototype.set = function(param) {
    try {
        var acl = new Packages.io.personium.client.Acl();

        if (param["requireSchemaAuthz"] !== null
        && typeof param["requireSchemaAuthz"] !== "undefined"
        && (param["requireSchemaAuthz"] !== "")) {
            acl.setRequireSchemaAuthz(param["requireSchemaAuthz"]);
        }
        var aces = param["ace"]

        if (aces != null) {
            for (var i = 0; i < aces.length; i++) {
                aceObj = aces[i];
                if (aceObj != null) {
                    var ace = new Packages.io.personium.client.Ace();
                    if ((aceObj["role"] != null) && (aceObj["role"] != "")) {
                        ace.setRole(aceObj["role"].core);
                    }
                    if ((aceObj["privilege"] != null) && (aceObj["privilege"] instanceof Array) && (aceObj["privilege"] != "")) {
                        for (var n = 0; n < aceObj["privilege"].length; n++) {
                            ace.addPrivilege(aceObj["privilege"][n]);
                        }
                    }
                    acl.addAce(ace);
                }
            }
        }
        this.core.set(acl);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ACLを取得する.
 * @returns {string} 取得したACLのjsonオブジェクト.
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AclManager.prototype.get = function() {

    try {
        var obj = this.core.get();
        var acl = {};
        acl["base"] = obj.base + "";
        acl["requireSchemaAuthz"] = obj.getRequireSchemaAuthz() + "";

        var aces = obj.aceList;
        for (var i = 0; i < aces.length; i++) {
            var principalObj = aces[i].getPrincipal();
            var roleName;
            if (principalObj instanceof Packages.io.personium.client.Role) {
                // Only Role class have getName method
                roleName = principalObj.getName();
            } else {
                switch(principalObj) {
                case Packages.io.personium.client.Principal.ALL:
                    roleName = '_ALL';
                    break;
                case Packages.io.personium.client.Principal.AUTHENTICATED:
                    roleName = '_AUTHENTICATED';
                    break;
                case Packages.io.personium.client.Principal.UNAUTHENTICATED:
                    roleName = '_UNAUTHENTICATED';
                    break;
                default:
                    throw new _p.PersoniumException("Parameter Invalid");
                }
            }
            var ace = {};
            ace["role"] = roleName + "";
            var privilegeList = aces[i].privilegeList;
            var privilege = new Array(privilegeList.length);
            for (j = 0; j < privilegeList.length; j++) {
                privilege[j] = privilegeList[j] + "";
            }
            ace["privilege"] = privilege;
            aces[i] = ace;
        }
        acl["ace"] = aces;
        return acl;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * WebDAVの操作を行う抽象クラス.
 * @class WebDAV操作クラス
 */
_p.Webdav = function() {
};

_p.Webdav.prototype.acl = new _p.AclManager();

/**
 * WebDAVコレクションを作成する.<br>
 * 例：<br>
 *   box().mkCol("col");
 * @param {string} name 作成するコレクション名
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.mkCol = function(name) {
    try {
        this.core.mkCol(name);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ODataコレクションを作成する.<br>
 * 例：<br>
 *   box().mkOData("col");
 * @param {string} name 作成するコレクション名
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.mkOData = function(name) {
    try {
        this.core.mkOData(name);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Serviceコレクションを作成する.<br>
 * 例：<br>
 *   box().mkService("col");
 * @param {string} name 作成するコレクション名
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.mkService = function(name) {
    try {
        this.core.mkService(name);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * コレクション内のリソースの一覧を取得する.
 * @returns {string[]} リソースへのWebDAVパス一覧
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getFileList = function() {
    try {
        return this.core.getFileList();
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * コレクション内のサブコレクションの一覧を取得する.
 * @returns {string[]} サブコレクションへのWebDAVパス一覧
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getColList = function(name) {
    try {
        return this.core.getColList(name);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * コレクションのプロパティを設定する.
 * @param {string} key 設定項目名
 * @param {string} value 設定項目値
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.setProp = function(key, value) {
    try {
        this.core.setProp(key, value);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * コレクションのプロパティを取得する.
 * @param {string} key 設定項目名
 * @returns {string} 指定したプロパティ値
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getProp = function(key) {
    try {
        return this.core.getProp(key);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * サブコレクションを指定する.<br>
 * 例：<br>
 * <dd>box().col("col");
 * <dd>box().col("col1").col("col2").col("col3");
 * <dd>box().col("col1/col2/col3");
 * @param {string} name コレクション名
 * @returns {_p.DavCollection} _p.DavCollectionオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.col = function(name) {
    try {
        var dav = new _p.DavCollection(this.core.col(name));
        dav.acl.core = dav.core.acl;
        return dav;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ODataコレクションを取得する.<br>
 * 例：<br>
 *   box().odata("odata");
 * @param {string} name ODataコレクション名
 * @returns {_p.ODataCollection} _p.ODataCollectionオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.odata = function(name) {
    try {
        return new _p.ODataCollection(this.core.odata(name));
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Service Collection.
 * Example:<br>
 *   box().service("col");
 * @name {string} name Service Collection name
 * @returns {_p.ServiceCollection} _p.ServiceCollection object
 * @exception {_p.PersoniumException} DAO exception
 */
_p.Webdav.prototype.service = function(name) {
    try {
        return new _p.ServiceCollection(this.core.service(name));
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 現在のコレクションのパスを取得する.
 * @returns {string} 現在のコレクションのパス
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getPath = function() {
    try {
        return this.core.getPath() + "";
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * DAVリソースを文字列型で取得する.<br>
 * 例：<br>
 *   box().col("col").getString("index.html", "utf-8");
 * @param {string} path 取得するパス
 * @param {string} charset 文字コード
 * @returns {string} DAVリソース
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getString = function(path, charset) {
    if (!charset) {
        charset = "utf-8";
    }
    try {
        return this.core.getString(path, charset);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * DAVリソースをストリームで取得する.
 * @param {string} path 取得するパス
 * @returns {stream} DAVリソース
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.getStream = function(path) {
    try {
        return this.core.getStream(path);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Davへファイルを登録する.<br>
 * 例：<br>
 * 1. <br>
 *   .put("test.txt", "text/plain", "test-data", "*");<br>
 * 2.<br>
 *   .put({<br>
 *       path: "test.txt",<br>
 *       contentType: "text/plain",<br>
 *       data: "test-data",<br>
 *       charset: "UTF-8",<br>
 *       etag: "*"<br>
 *    });<br>
 * @param {string または Object} param 対象のDavのパス または、すべてのパラメタを含んだJSONオブジェクト
 * @param {string} contentType 登録するファイルのメディアタイプ
 * @param {string} data 登録するデータ(文字列形式)
 * @param {string} etag 対象のEtag
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.put = function(param, contentType, data, etag) {
    if (typeof param == 'string') {
        try {
            this.core.put(param, contentType, "UTF-8", data, etag?etag:"*");
        } catch (e) {
            throw new _p.PersoniumException(e.message);
        }
    } else {
        if ((param.path) && (param.contentType) && (param.data)) {
            param.charset = param.charset?param.charset:"UTF-8";
            param.etag = param.etag?param.etag:"*";
            try {
                this.core.put(param.path, param.contentType, param.charset, param.data, param.etag);
            } catch (e) {
                throw new _p.PersoniumException(e.message);
            }
        } else {
            throw new _p.PersoniumException("Parameter Invalid");
        }
    }
};

/**
 * 指定Pathのデータを削除.<br>
 * 例：<br>
 * ETag指定：<br>
 *   box().col("col").del("index.html", "1234567890");<br>
 * ETag省略：<br>
 *   box().col("col").del("index.html");
 * @param {string} path 削除するパス
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.del = function(path, etag) {
    if (!etag) {
        etag = "*";
    }
    try {
        this.core.del(path, etag);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * URL文字列を取得する.
 * @returns {string} path 自身のURL文字列
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Webdav.prototype.makeUrlString = function() {
    try {
        return this.core.makeUrlString();
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * $linkの作成／削除を行うクラス.
 * @class $link操作クラス
 */
_p.LinkManager = function(obj) {
    this.core = obj;
};

/**
 * linkを作成.
 * @param {Object} param Link先のオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.LinkManager.prototype.link = function(param) {
    try {
        this.core.link(param.core);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * linkを削除.
 * @param {Object} param Link先のオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.LinkManager.prototype.unLink = function(param) {
    try {
        this.core.unLink(param.core);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * $linkの作成／削除する.
 * @class $link操作クラス
 */
_p.MetadataLinkManager = function(obj) {
    this.core = obj;
};

/**
 * linkを作成.
 * @param {Object} param Link先のオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.MetadataLinkManager.prototype.link = function(param) {
    try {
        this.core.link(param.core);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * linkを削除.
 * @param {Object} param Link先のオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.MetadataLinkManager.prototype.unLink = function(param) {
    try {
        this.core.unLink(param.core);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいCellオブジェクトを作成する.
 * @class Cell操作クラス
 * @property {string} name Cellを識別するCell名
 * @property {_p.CellCtl} ctl Cell内のCtl操作のためのプロパティ
 * @property {_p.EventManager} event CellレベルのEvent登録操作のためのプロパティ
 * @property {_p.EventManager} log CellレベルのEvent取得操作のためのプロパティ
 */
_p.Cell = function(obj) {
    this.core = obj;
    this.name = "";
    this.ctl = new _p.CellCtl(obj);
    this.event = new _p.EventManager(this.core.event);
    this.currentLog = new _p.CurrentLogManager(this.core.currentLog);
    this.archiveLog = new _p.ArchiveLogManager(this.core.archiveLog);
    this.acl.core = this.core.acl;
};

_p.Cell.prototype.acl = new _p.AclManager();

/**
 * Boxを指定.
 * @param {string} name Box名
 * @returns {_p.Box} Box操作クラス
 */
_p.Cell.prototype.box = function(name) {
    var dav;
    if (name) {
        dav = new _p.Box(this.core.box(name));
    } else {
        dav = new _p.Box(this.core.box());
    }
    // WebDAVから継承された際、ACLオブジェクトのcoreがセットされていないので、
    // このタイミングでセットする
    dav.acl.core = dav.core.acl;
    return dav;
};

/**
 * CellのUrlを取得.
 * @returns {string} CellのURL
 */
_p.Cell.prototype.getUrl = function() {
    return this.core.getUrl() + "";
}

/**
 * CellのUrlを取得.
 * @returns {string} CellのURL
 */
_p.Cell.prototype.setOwnerRepresentativeAccounts = function(user) {
    this.core.setOwnerRepresentativeAccounts(user);
}

/**
 * Cell.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class CellCtrl操作クラス
 * @property {_p.BoxManager} box Box操作のためのプロパティ
 * @property {_p.AccountManager} account Account操作のためのプロパティ
 * @property {_p.RelationManager} relation Relation操作のためのプロパティ
 * @property {_p.RoleManager} role Role操作のためのプロパティ
 * @property {_p.ExtRoleManager} extRole ExtRole操作のためのプロパティ
 * @property {_p.ExtCellManager} extCell ExtCell操作のためのプロパティ
 */
_p.CellCtl = function(obj) {
    this.core = obj;
    this.box = new _p.BoxManager(this.core.boxManager);
    this.account = new _p.AccountManager(this.core.account);
    this.relation = new _p.RelationManager(this.core.relation);
    this.role = new _p.RoleManager(this.core.role);
    this.extRole = new _p.ExtRoleManager(this.core.extRole);
    this.extCell = new _p.ExtCellManager(this.core.extCell);
};

/**
 * 新しいBoxオブジェクトを作成する.
 * @class Box操作クラス
 * @augments _p.Webdav
 * @property {string} name Box名
 * @property {string} schema スキーマ
 * @property {_p.BoxCtl} ctl BoxCtlオブジェクト
 */
_p.Box = function(obj) {
    this.core = obj;
    this.name = "";
    this.schema = "";
    this.ctl = new _p.BoxCtl(obj);
};
_p.Box.prototype = new _p.Webdav();

/**
 * Box.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class BoxCtl操作クラス
 * @property {_p.RoleManager} role Role操作のためのプロパティ
 */
_p.BoxCtl = function(obj) {
    this.core = obj;
    this.role = new _p.RoleManager(this.core.role);
};

/**
 * 新しいAccountオブジェクトを作成する.
 * @class Accountクラス
 * @property {string} name ユーザー名
 * @property {_p.LinkManager} role RoleへのLink操作を行うためのプロパティ
 */
_p.Account = function(obj) {
    this.core = obj;
    this.name = "";
    this.role = new _p.LinkManager(this.core.role);
};

/**
 * 新しいRoleオブジェクトを作成する.
 * @class Roleクラス
 * @property {string} name Role名
 * @property {string} id Role ID値
 * @property {_p.LinkManager} account AccountへのLink操作を行うためのプロパティ
 * @property {_p.LinkManager} relation RelationへのLink操作を行うためのプロパティ
 * @property {_p.LinkManager} extCell ExtCellへのLink操作を行うためのプロパティ
 */
_p.Role = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.account = new _p.LinkManager(this.core.account);
    this.relation = new _p.LinkManager(this.core.relation);
    this.extCell = new _p.LinkManager(this.core.extCell);
    this.extRole = new _p.LinkManager(this.core.extRole);
};

/**
 * 新しいRelationオブジェクトを作成する.
 * @class Relationクラス
 * @property {string} name Relation名
 * @property {string} id Relation ID値
 * @property {_p.LinkManager} role RoleへのLink操作を行うためのプロパティ
 * @property {_p.LinkManager} extCell ExtCellへのLink操作を行うためのプロパティ
 */
_p.Relation = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.role = new _p.LinkManager(this.core.role);
    this.extCell = new _p.LinkManager(this.core.extCell);
};

/**
 * 新しいExtRoleオブジェクトを作成する.
 * @class ExtRoleクラス
 * @property {string} name ExtRole名
 * @property {string} id ExtRole ID値
 */
_p.ExtRole = function(obj) {
    this.core = obj;
    this.name = "";
    this.relationName = "";
    this.relationBoxName = "";
    this.id = "";
    this.role = new _p.LinkManager(this.core.role);
};

/**
 * 新しいExtCellオブジェクトを作成する.
 * @class ExtCellクラス
 * @property {string} name ExtCell名
 * @property {string} id ExtCell ID値
 * @property {_p.LinkManager} role RoleへのLink操作を行うためのプロパティ
 * @property {_p.LinkManager} relation RelationへのLink操作を行うためのプロパティ
 */
_p.ExtCell = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.role = new _p.LinkManager(this.core.role);
    this.relation = new _p.LinkManager(this.core.relation);
};

/**
 * 新しいDavCollectionオブジェクトを作成する.
 * @class DavCollectionクラス
 * @augments _p.Webdav
 * @property {string} name コレクション名
 */
_p.DavCollection = function(obj) {
    this.core = obj;
    this.name = "";
};
_p.DavCollection.prototype = new _p.Webdav();

/**
 * 新しいODataCollectionオブジェクトを作成する.
 * @class ODataCollectionクラス
 * @property {string} name ODataCollection名
 * @property {_p.ODataCollectionCtl} schema $metadata操作を行うためのプロパティ
 */
_p.ODataCollection = function(obj) {
    this.core = obj;
    this.name = "";
    this.schema = new _p.ODataCollectionCtl(obj);
};

/**
 * ユーザーデータ(OData)のEntitySetを指定.
 * 例：<br>
 * box().odata("odata").schema.entityType.create("entity");<br>
 * box().odata("odata").entitySet("entity");
 * @param {string} name EntitySet名(事前にEntityTypeとして作成されたEntity名を指定)
 * @returns {_p.EntitySet} EntitySetオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ODataCollection.prototype.entitySet = function(name) {
    try {
        return new _p.EntitySet(this.core.entitySet(name));
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Create new ServiceCollection object.
 * @class ServiceCollection class
 * @property {string} name ServiceCollection name
 */
_p.ServiceCollection = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * Execute Service.
 * @param {string} name Service name
 * @param {string} body Http request body
 * @param {string} contentType Content-Type value
 * @returns {PersoniumResponse} Http response
 * @exception {_p.PersoniumException} DAO exception
 */
_p.ServiceCollection.prototype.call = function(name, body, contentType) {
    try {
        var ret = this.core.call("POST", name, body, contentType);
        return ret;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいEntitySetオブジェクトを作成.
 * @class EntitySet操作クラス
 * @augments _p.OData
 */
_p.EntitySet = function(obj) {
    this.core = obj;
};
_p.EntitySet.prototype = new _p.OData();

/**
 * ユーザーデータを登録.<br>
 * 例：box().odata("odata").entitySet("entity").create({"name":"user","age":18});
 * @param {Object} json 登録するJSONデータ
 * @returns {Object} 登録されたJSONデータ
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.create = function(json) {
    try {
        var ret = this.core.createAsJson(_p.util.obj2javaJson(json));
        return JSON.parse(ret);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ユーザーデータを取得する.
 * @param {string} id 取得対象のID値
 * @returns {Object} 取得したJSONデータ
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.retrieve = function(id) {
    try {
        var ret = this.core.retrieveAsJson(id);
        return JSON.parse(ret);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ユーザーデータを更新する.
 * @param {string} id 更新対象のID
 * @param {Object} json 更新するJSONデータ
 * @param {string} etag Etag
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.update = function(id, json, etag) {
    try {
        this.core.update(id, _p.util.obj2javaJson(json), etag);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ユーザーデータを部分更新する.
 * @param {string} id 部分更新対象のID
 * @param {Object} json 部分更新するJSONデータ
 * @param {string} etag Etag
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.merge = function(id, json, etag) {
    try {
        this.core.merge(id, _p.util.obj2javaJson(json), etag);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};


/**
 * NavigationProperty経由登録のためのkey値を指定する.<br>
 * 例：box().odata("odata").entitySet("entity").key("key").nav("linkEt").create({"name":"user"});
 * @param {string} id keyPredicate
 * @returns {_p.EntitySet} EntitySetオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.key = function(id) {
    try {
        return new _p.EntitySet(this.core.key(id));
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * navigationPropertyを指定する.<br>
 * 例：box().odata("odata").entitySet("entity").key("key").nav("linkEt").create({"name":"user"});
 * @param {string} id keyPredicate
 * @returns {_p.EntitySet} EntitySetオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntitySet.prototype.nav = function(navProp) {
    try {
        return new _p.EntitySet(this.core.nav(navProp));
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ODataCollection.schema でアクセスされ、関連APIの呼び出しを行う.
 * @class ODataCollectionCtlクラス
 * @property {_p.EntityTypeManager} entityType EntityType操作のためのプロパティ
 * @property {_p.AssociationEndManager} associationEnd AssociationEnd操作のためのプロパティ
 */
_p.ODataCollectionCtl = function(obj) {
    this.core = obj;
    this.entityType = new _p.EntityTypeManager(this.core.entityType);
    this.associationEnd = new _p.AssociationEndManager(this.core.associationEnd);
    this.complexType = new _p.ComplexTypeManager(this.core.complexType);
    this.property = new _p.PropertyManager(this.core.property);
    this.complexTypeProperty = new _p.ComplexTypePropertyManager(this.core.complexTypeProperty);
};

/**
 * 新しいEntityTypeオブジェクトを作成する.
 * @class EntityTypeクラス
 * @property {string} name EntityType名
 */
_p.EntityType = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * 新しいAssociationEndオブジェクトを作成する.
 * @class AssociationEndクラス
 * @property {string} name Association名
 * @property {string} entityTypeName EntityType名
 * @property {string} multiplicity 多重度
 * @property {_p.MetadataLinkManaer} associationEnd AssociationEndへのリンク操作を行うプロパティ
 */
_p.AssociationEnd = function(obj) {
    this.core = obj;
    this.name = "";
    this.entityTypeName = "";
    this.multiplicity = "";
    this.associationEnd = new _p.MetadataLinkManager(this.core.associationEnd)
};

/**
 * 新しいComplexTypeオブジェクトを作成する.
 * @class ComplexTypeクラス
 * @property {string} name ComplexType名
 */
_p.ComplexType = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * 新しいPropertyオブジェクトを作成する.
 * @class Propertyクラス
 * @property {string} name Property名
 * @property {string} entityTypeName 紐付くEntityType名
 * @property {string} type 型定義
 * @property {boolean} nullable Null値許可
 * @property {object} defaultValue デフォルト値
 * @property {string} CollectionKind 配列種別
 * @property {boolean} isKey 主キー設定
 * @property {string} uniqueKey ユニークキー設定
 */
_p.Property = function(obj) {
    this.core = obj;
    this.name = "";
    this.entityTypeName = "";
    this.type = "";
    this.nullable = true;
    this.defaultValue = null;
    this.collectionKind = "None";
    this.isKey = false;
    this.uniqueKey = null;
};

/**
 * 新しいComplexTypePropertyオブジェクトを作成する.
 * @class ComplexTypePropertyクラス
 * @property {string} name ComplexTypeProperty名
 * @property {string} complexTypeName 紐付くComplexType名
 * @property {string} type 型定義
 * @property {boolean} nullable Null値許可
 * @property {object} defaultValue デフォルト値
 * @property {string} CollectionKind 配列種別
 */
_p.ComplexTypeProperty = function(obj) {
    this.core = obj;
    this.name = "";
    this.complexTypeName = "";
    this.type = "";
    this.nullable = true;
    this.defaultValue = null;
    this.collectionKind = "None";
};

/**
 * 新しいBoxManagerオブジェクトを作成する.
 * @class Box操作クラス
 * @augments _p.OData
 */
_p.BoxManager = function(obj) {
    this.core = obj;
};
_p.BoxManager.prototype = new _p.OData();

/**
 * Boxを登録する.
 * @param {Object} param Box作成に必要なJSON型オブジェクト
 * 例：cell().ctl.box.create({"name":"boxname", "schema":"box-schema"});
 * @returns {_p.Box} 作成したBoxオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.BoxManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var box = new _p.Box(obj);
        box.name = obj.getName() + "";
        box.schema = obj.getSchema() + "";
        box.acl.core = box.core.acl;
        return box;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Boxを取得する.
 * @param {string} name 取得するBoxの名前<br>
 * 例：cell().ctl.box.retrieve("boxname");
 * @returns {_p.Box} 取得したBoxオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.BoxManager.prototype.retrieve = function(name) {
    try {
        var obj = this.internalRetrieve(name);
        var box = new _p.Box(obj);
        box.name = obj.getName() + "";
        box.schema = obj.getSchema() + "";
        box.acl.core = box.core.acl;
        return box;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいAccountManagerオブジェクトを作成する.
 * @class Account操作クラス
 * @augments _p.OData
 */
_p.AccountManager = function(obj) {
    this.core = obj;
};
_p.AccountManager.prototype = new _p.OData();

/**
 * Accountを登録する。<br>
 * 例：account.create({"name":"user01"}, "password");
 * @param {Object} user ユーザー名のJSONオブジェクト
 * @param {string} pass パスワード
 * @returns {_p.Account} 作成したAccountオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AccountManager.prototype.create = function(user, pass) {
    var obj;
    try {
        obj = this.core.create(_p.util.obj2javaJson(user), pass);
        var account = new _p.Account(obj);
        account.name = obj.getName() + "";
        return account;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Accountを取得する.<br>
 * 例：account.retrieve("user01");
 * @param {string} user ユーザー名<br>
 * @returns {_p.Account} 作成したAccountオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AccountManager.prototype.retrieve = function(user) {
    var obj;
    try {
        obj  = this.core.retrieve(user);
        var account = new _p.Account(obj);
        account.name = obj.getName() + "";
        return account;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Passwordを変更する.<br>
 * 例：account.changePassword("user01", "newPassword");
 * @param {string} user ユーザー名
 * @param {string} pass パスワード
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AccountManager.prototype.changePassword = function(user, pass) {
    var obj;
    try {
        this.core.changePassword(user, pass);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいEventManagerオブジェクトを作成する.
 * @class Event操作クラス
 */
_p.EventManager = function(obj) {
    this.core = obj;
};

/**
 * Eventを登録する.
 * @param {Object} param イベントオブジェクト<br>
 * @param {String} requestKey X-Personium-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   event.post({
 *               "Type":"typeData",
 *               "Object":"objectData",
 *               "Info":"infoData"},
 *               "RequestKey");
 * @returns {PersoniumResponse} Http response
 * @exception {_p.PersoniumException} DAO exception
 */
_p.EventManager.prototype.post = function(param, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.post(_p.util.obj2javaJson(param));
        }else{
            return this.core.post(_p.util.obj2javaJson(param), requestKey);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいCurrentLogManagerオブジェクトを作成する.
 * @class CurrentLog操作クラス
 */
_p.CurrentLogManager = function(obj){
    this.core = obj;
};

/**
 * ログをString形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Personium-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   currentLog.getString("default.log", "RequestKey");
 * @exception {_p.PersoniumException} DAO例外
 */
_p.CurrentLogManager.prototype.getString = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getString(filename);
        }else{
            return this.core.getString(filename, requestKey);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ログをStream形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Personium-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   currentLog.getStream("default.log", "RequestKey");
 * @exception {_p.PersoniumException} DAO例外
 */
_p.CurrentLogManager.prototype.getStream = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getStream(filename);
        }else{
            return this.core.getStream(filename, requestKey);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};
/**
 * 新しいArchiveLogManagerオブジェクトを作成する.
 * @class ArchiveLog操作クラス
 */
_p.ArchiveLogManager = function(obj){
    this.core = obj;
};

/**
 * ローテートされたログをString形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Personium-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   archiveLog.getString("default.log", "RequestKey");
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ArchiveLogManager.prototype.getString = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getString(filename);
        }else{
            return this.core.getString(filename, requestKey);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ローテートされたログをStream形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Personium-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   archiveLog.getStream("default.log", "RequestKey");
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ArchiveLogManager.prototype.getStream = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getStream(filename);
        }else{
            return this.core.getStream(filename, requestKey);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいRelationManagerオブジェクトを作成する.
 * @class Relation操作クラス
 * @augments _p.OData
 */
_p.RelationManager = function(obj) {
    this.core = obj;
};
_p.RelationManager.prototype = new _p.OData();

/**
 * Relationを登録<br>
 * 例：cell().ctl.relation.create({"name":"relation","_box.name":"boxName"});
 * @param {Object} param Relation作成に必要なJSON型オブジェクト
 * @returns {_p.Relation} 作成したRelationオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RelationManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var relation = new _p.Relation(obj);
        relation.name = obj.getName() + "";
        relation.boxName = obj.getBoxName() + "";
        return relation;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};


/**
 * Relationを取得<br>
 * 例：cell().ctl.relation.retrieve({"name":"relation","_box.name":"boxName"});
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @returns {_p.Relation} 作成したRelationオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RelationManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName === null || typeof boxName === "undefined") {
            obj  = this.core.retrieve(name);
        } else {
            obj  = this.core.retrieve(name, boxName);
        }
        var relation = new _p.Relation(obj);
        relation.name = obj.getName() + "";
        relation.boxName = obj.getBoxName() + "";
        return relation;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Relationデータを削除<br>
 * 例：cell().ctl.relation.retrieve({"name":"relation","_box.name":"boxName"});
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RelationManager.prototype.del = function(param) {
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName == null || typeof boxName === "undefined") {
            this.core.del(name);
        } else {
            this.core.del(name, boxName);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいRoleManagerオブジェクトを作成する.
 * @class Role操作クラス
 * @augments _p.OData
 */
_p.RoleManager = function(obj) {
    this.core = obj;
};
_p.RoleManager.prototype = new _p.OData();

/**
 * Roleを登録<br>
 * 例：cell().ctl.relation.create({"name":"role","_box.name":"boxName"});
 * @param {Object} param Role作成に必要なJSON型オブジェクト
 * @returns {_p.Role} 作成したRoleオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RoleManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var role = new _p.Role(obj);
        role.name = obj.getName() + "";
        role.boxName = obj.getBoxName() + "";
        return role;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Roleを取得<br>
 * 例：<br>
 * cell().ctl.relation.retrieve({"name":"role","_box.name":"boxName"});<br>
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @returns {_p.Role} 作成したRoleオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RoleManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName === null || typeof boxName === "undefined") {
            obj  = this.core.retrieve(name);
        } else {
            obj  = this.core.retrieve(name, boxName);
        }
        var role = new _p.Role(obj);
        role.name = obj.getName() + "";
        role.boxName = obj.getBoxName() + "";
        return role;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Roleデータを削除<br>
 * 例：<br>
 * cell().ctl.relation.del({"name":"role","_box.name":"boxName"});<br>
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @exception {_p.PersoniumException} DAO例外
 */
_p.RoleManager.prototype.del = function(param) {
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName == null || typeof boxName === "undefined") {
            this.core.del(name);
        } else {
            this.core.del(name, boxName);
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいExtRoleManagerオブジェクトを作成する.
 * @class ExtRole操作クラス
 * @augments _p.OData
 */
_p.ExtRoleManager = function(obj) {
    this.core = obj;
};
_p.ExtRoleManager.prototype = new _p.OData();

/**
 * ExtRoleを登録する.
 * @param {Object} param ExtRole作成に必要なJSON型オブジェクト
 * @returns {_p.ExtRole} 作成したExtRoleオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ExtRoleManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var extRole = new _p.ExtRole(obj);
        extRole.name = obj.getName() + "";
        extRole.relationName = obj.getRelationName() + "";
        extRole.relationBoxName = obj.getRelationBoxName() + "";
        return extRole;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ExtRoleを取得<br>
 * 例：<br>
 * cell().ctl.extRole.retrieve({"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"});<br>
 * @param {string} param {"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"} というJSONを指定
 * @returns {_p.ExtRole} 作成したExtRoleオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ExtRoleManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["ExtRole"];
    var relationName = param["_Relation.Name"];
    var relationBoxName = param["_Relation._Box.Name"];

    try {
        if (relationName === null || typeof relationName === "undefined") {
            obj  = this.core.retrieve(name, null, null);
        } else {
            obj  = this.core.retrieve(name, relationName, relationBoxName);
        }
        var extRole = new _p.ExtRole(obj);
        extRole.name = obj.getName() + "";
        extRole.relationName = obj.getRelationName() + "";
        extRole.relationBoxName = obj.getRelationBoxName() + "";
        return extRole;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ExtRoleデータを削除<br>
 * 例：<br>
 * cell().ctl.extRole.del({"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"});<br>
 * @param {string} param {"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"} というJSONを指定
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ExtRoleManager.prototype.del = function(param) {
    var name = param["ExtRole"];
    var relationName = param["_Relation.Name"];
    var relationBoxName = param["_Relation._Box.Name"];

    try {
        if (relationName === null || typeof relationName === "undefined") {
            obj  = this.core.del(name, null, null);
        } else {
            if (relationBoxName === null || typeof relationBoxName === "undefined") {
                obj  = this.core.del(name, relationName, null);
            } else {
                obj  = this.core.del(name, relationName, relationBoxName);
            }
        }
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいExtCellManagerオブジェクトを作成する.
 * @class ExtCellManager操作クラス
 * @augments _p.OData
 */
_p.ExtCellManager = function(obj) {
    this.core = obj;
};
_p.ExtCellManager.prototype = new _p.OData();

/**
 * ExtCellを登録する.
 * @param {Object} param ExtCell作成に必要なJSON型オブジェクト
 * @returns {_p.ExtCell} 作成したExtCellオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ExtCellManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var extcell = new _p.ExtCell(obj);
        extcell.url = obj.getUrl() + "";
        return extcell;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ExtCellを取得する.
 * @param {string} url ExtCell取得に必要なurl
 * @returns {_p.ExtCell} 取得したExtCellオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ExtCellManager.prototype.retrieve = function(url) {
    try {
        var obj = this.internalRetrieve(url);
        var extCell = new _p.ExtCell(obj);
        extCell.url = obj.getUrl() + "";
        return extCell;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいUnitManagerオブジェクトを作成する.
 * @class Unit操作クラス
 * @property {_p.UnitManagerCtl} ctl Unit内のCtl操作を行うためのプロパティ
 */
_p.UnitManager = function(obj) {
    this.core = obj;
    this.ctl = new _p.UnitManagerCtl(obj);
};

/**
 * UnitManager.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class UnitManagerCtl操作クラス
 * @property {_p.CellManager} CellのCRUDを行うためのプロパティ
 */
_p.UnitManagerCtl = function(obj) {
    this.core = obj;
    this.cell = new _p.CellManager(this.core.cell);
};

/**
 * 新しいCellManagerオブジェクトを作成する.
 * @class CellManager操作クラス
 * @augments _p.OData
 */
_p.CellManager = function(obj) {
    this.core = obj;
};
_p.CellManager.prototype = new _p.OData();

/**
 * Cellを登録する.
 * @param {Object} param Cell作成に必要なJSON型オブジェクト
 * @returns {_p.Cell} 作成したCellオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.CellManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var cell = new _p.Cell(obj);
        cell.name = obj.getName() + "";
        return cell;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Cellを更新する.
 * @param {String} id 更新対象のCell ID
 * @param {Object} json Cell更新に必要なJSON型オブジェクト
 * @param {String} etag Etag情報
 * @exception {_p.PersoniumException} DAO例外
 */
_p.CellManager.prototype.update = function(id, json, etag) {
    try {
        this.internalUpdate(id, json, etag);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Cellを取得する.
 * @param {string} id Cell取得に必要なid
 * @returns {_p.Cell} 取得したCellオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.CellManager.prototype.retrieve = function(id) {
    try {
        var obj = this.internalRetrieve(id);
        var cell = new _p.Cell(obj);
        cell.name = obj.getName() + "";
        return cell;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいEntityTypeManagerオブジェクトを作成する.
 * @class EntityType操作クラス
 * @augments _p.OData
 */
_p.EntityTypeManager = function(obj) {
    this.core = obj;
};
_p.EntityTypeManager.prototype = new _p.OData();

/**
 * EntityTypeManagerを登録する.
 * @param {Object} param EntityType作成に必要なJSON型オブジェクト
 * @returns {_p.EntityType} 作成したEntityTypeオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntityTypeManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var entityType = new _p.EntityType(obj);
        entityType.name = obj.getName() + "";
        return entityType;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * EntityTypeManagerを取得する.
 * @param {string} name 取得するEntityTypeの名前
 * @returns {_p.EntityType} 取得したEntityTypeオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.EntityTypeManager.prototype.retrieve = function(param) {
    try {
        var obj = this.internalRetrieve(param);
        var entityType = new _p.EntityType(obj);
        entityType.name = obj.getName() + "";
        return entityType;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};


/**
 * 新しいAssociationEndManagerオブジェクトを作成する.
 * @class AssociationEnd操作クラス
 * @augments _p.OData
 */
_p.AssociationEndManager = function(obj) {
    this.core = obj;
}
_p.AssociationEndManager.prototype = new _p.OData();

/**
 * AssociationEndを登録する.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.associationEnd.create({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} param AssociationEnd作成に必要なJSON型オブジェクト
 * @returns {_p.AssociationEnd} 作成したAssociationEndオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AssociationEndManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var AssociationEnd = new _p.AssociationEnd(obj);
        AssociationEnd.name = obj.getName() + "";
        AssociationEnd.entityTypeName = obj.getEntityTypeName() + "";
        AssociationEnd.multiplicity = obj.getMultiplicity() + "";
        return AssociationEnd;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * AssociationEndを取得.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.associationEnd.retrieve({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} 取得対象のキー
 * @param {string} entity 取得対象のEntityType.Name値
 * @returns {_p.AssociationEnd} 取得したAssociationEndオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AssociationEndManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        var obj = this.core.retrieve(name, entityTypeName);
        var AssociationEnd = new _p.AssociationEnd(obj);
        AssociationEnd.name = obj.getName() + "";
        AssociationEnd.entityTypeName = obj.getEntityTypeName() + "";
        AssociationEnd.multiplicity = obj.getMultiplicity() + "";
        return AssociationEnd;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * AssociationEndを削除.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.associationEnd.del({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} key 削除対象のキー
 * @param {string} entity 削除対象のEntityType.Name値
 * @returns {_p.AssociationEnd} 取得したAssociationEndオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.AssociationEndManager.prototype.del = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        this.core.del(name, entityTypeName);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいComplexTypeManagerオブジェクトを作成する.
 * @class ComplexType操作クラス
 * @augments _p.OData
 */
_p.ComplexTypeManager = function(obj) {
    this.core = obj;
}
_p.ComplexTypeManager.prototype = new _p.OData();

/**
 * ComplexTypeを登録する.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexType.create({"Name":"name"});
 * @param {Object} param ComplexType作成に必要なJSON型オブジェクト
 * @returns {_p.ComplexType} 作成したComplexTypeオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypeManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var ComplexType = new _p.ComplexType(obj);
        ComplexType.name = obj.getName() + "";
        return ComplexType;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ComplexTypeを取得.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexType.retrieve({"Name":"name"});
 * @param {Object} 取得対象のキー
 * @param {string} entity 取得対象のEntityType.Name値
 * @returns {_p.ComplexType} 取得したComplexTypeオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypeManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    try {
        var obj = this.core.retrieve(name);
        var ComplexType = new _p.ComplexType(obj);
        ComplexType.name = obj.getName() + "";
        return ComplexType;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ComplexTypeを削除.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexType.del({"Name":"name"});
 * @param {Object} key 削除対象のキー
 * @param {string} entity 削除対象のComplexType.Name値
 * @returns {_p.ComplexType} 取得したComplexTypeオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypeManager.prototype.del = function(key) {
    var name = key["Name"];
    try {
        this.core.del(name);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいPropertyManagerオブジェクトを作成する.
 * @class Property操作クラス
 * @augments _p.OData
 */
_p.PropertyManager = function(obj) {
    this.core = obj;
}
_p.PropertyManager.prototype = new _p.OData();

/**
 * Propertyを登録する.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.property.create({"Name": "PetName",
 * "_EntityType.Name": "Profile","Type": "Edm.String"});
 * @param {Object} param Property作成に必要なJSON型オブジェクト
 * @returns {_p.Property} 作成したPropertyオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.PropertyManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var Property = new _p.Property(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getEntityTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        Property.isKey = obj.getIsKey();
        Property.uniqueKey = obj.getUniqueKey() + "";
        return Property;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Propertyを取得.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.property.retrieve({"Name":"name","_EntityType.Name": "Profile"});
 * @param {Object} 取得対象のキー
 * @returns {_p.Property} 取得したPropertyオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.PropertyManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        var obj = this.core.retrieve(name, entityTypeName);
        var Property = new _p.Property(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getEntityTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        Property.isKey = obj.getIsKey();
        Property.uniqueKey = obj.getUniqueKey() + "";
        return Property;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * Propertyを削除.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.property.del({"Name":"name","_EntityType.Name": "Profile"});
 * @param {Object} key 削除対象のキー
 * @exception {_p.PersoniumException} DAO例外
 */
_p.PropertyManager.prototype.del = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        this.core.del(name, entityTypeName);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * 新しいComplexTypePropertyManagerオブジェクトを作成する.
 * @class ComplexTypeProperty操作クラス
 * @augments _p.OData
 */
_p.ComplexTypePropertyManager = function(obj) {
    this.core = obj;
}
_p.ComplexTypePropertyManager.prototype = new _p.OData();

/**
 * ComplexTypePropertyを登録する.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexTypeProperty.create({"Name": "PetName",
 * "_ComplexType.Name": "Profile","Type": "Edm.String"});
 * @param {Object} param ComplexTypeProperty作成に必要なJSON型オブジェクト
 * @returns {_p.ComplexTypeProperty} 作成したComplexTypePropertyオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypePropertyManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var Property = new _p.ComplexTypeProperty(obj);
        Property.name = obj.getName() + "";
        Property.complexTypeName = obj.getComplexTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        return Property;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ComplexTypePropertyを取得.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexTypeProperty.retrieve({"Name":"name","_ComplexType.Name": "Profile"});
 * @param {Object} 取得対象のキー
 * @returns {_p.ComplexTypeProperty} 取得したComplexTypePropertyオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypePropertyManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var complexTypeName = key["_ComplexType.Name"];
    try {
        var obj = this.core.retrieve(name, complexTypeName);
        var Property = new _p.ComplexTypeProperty(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getComplexTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        return Property;
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * ComplexTypePropertyを削除.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").schema.complexTypeProperty.del({"Name":"name","_ComplexType.Name": "Profile"});
 * @param {Object} key 削除対象のキー
 * @exception {_p.PersoniumException} DAO例外
 */
_p.ComplexTypePropertyManager.prototype.del = function(key) {
    var name = key["Name"];
    var complexTypeName = key["_ComplexType.Name"];
    try {
        this.core.del(name, complexTypeName);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * トークンの取得.<br>
 * @returns {Object} 取得したトークン情報<br>
 * 以下のJSON形式<br>
 * <dd>access_token
 * <dd>expires_in
 * <dd>refresh_token
 * <dd>refresh_expires_in
 * <dd>token_type
 */
_p.Cell.prototype.getToken = function() {
    var ret = {};
    ret.access_token = this.core.getAccessToken() + "";
    ret.expires_in = this.core.getExpiresIn() + "";
    ret.refresh_token = this.core.getRefreshToken() + "";
    ret.refresh_token_expires_in = this.core.getRefreshExpiresIn() + "";
    ret.token_type = this.core.getTokenType() + "";
    return ret;
};

/**
 * 新しいQueryオブジェクトを生成する.
 * @class Query操作クラス
 */
_p.Query = function(obj) {
    this.core = obj;
}

/**
 * $filterクエリを指定.<br>
 * 例：<br>
 * <dl>
 * <dt>完全一致：
 * <dd>odata("odata").entitySet("entity").query().filter("name eq 'user'").run();
 * <dt>前方一致：
 * <dd>odata("odata").entitySet("entity").query().filter("startswith(itemKey,'searchValue')").run();
 * <dt>部分一致：
 * <dd>odata("odata").entitySet("entity").query().filter("substringof('searchValue1',itemKey1)").run();
 * <dt>より大きい：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey gt 1000").run();
 * <dt>以上：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey ge 1000").run();
 * <dt>より小さい：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey lt 1000").run();
 * <dt>以下：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey le 1000").run();
 * <dt>論理積：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey1 eq 'searchValue1' and itemKey2 eq 'searchValue2'").run();
 * <dt>論理和：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey1 eq 'searchValue1' or itemKey2 eq 'searchValue2'").run();
 * <dt>優先グループ：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey eq 'searchValue' or (itemKey gt 500 and itemKey lt 1500)").run();
 * @param {string} filter $filterクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.filter = function(filter) {
    this.core.filter(filter);
    return this;
};

/**
 * $selectクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().select("name,age,type").run();
 * @param {string} select $selectクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.select = function(select) {
    this.core.select(select);
    return this;
};

/**
 * $topクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().top(100).run();
 * @param {number} top $topクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.top = function(value) {
    this.core.top(value);
    return this;
};

/**
 * $skipクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().skip(100).top(100).run();
 * @param {number} skip $skipクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.skip = function(value) {
    this.core.skip(value);
    return this;
};

/**
 * $expandクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().expand("entityname").run();
 * @param {string} expand $expandクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.expand = function(expand) {
    this.core.expand(expand);
    return this;
};

/**
 * $orderbyクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().orderby("fieldName asc").run();<br>
 *   odata("odata").entitySet("entity").query().orderby("fieldName desc").run();<br>
 * @param {string} type $orderbyクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.orderby = function(type) {
    this.core.orderby(type);
    return this;
};

/**
 * inlinecountクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().inlinecount("allpages").run();<br>
 *   odata("odata").entitySet("entity").query().inlinecount("none ").run();<br>
 * @param {string} type $inlinecountクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.inlinecount = function(type) {
    this.core.inlinecount(type);
    return this;
};

/**
 * qクエリを指定.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().q("key").run();
 * @param {string} value qクエリ
 * @returns {_p.Query} 自分自身(Query)
 */
_p.Query.prototype.q = function(value) {
     this.core.q(value);
     return this;
};

/**
 * ODataの検索を実行.<br>
 * 例：<br>
 *   odata("odata").entitySet("entity").query().run();
 * @returns {Object} 検索結果JSONオブジェクト
 * @exception {_p.PersoniumException} DAO例外
 */
_p.Query.prototype.run = function() {
    try {
        var ret = this.core.run();
        return JSON.parse(ret);
    } catch (e) {
        throw new _p.PersoniumException(e.message);
    }
};

/**
 * DAO例外
 * @class DAOの例外クラス
 * @param {string} msg メッセージ
 * @property {string} message メッセージ
 * @property {string} code コード
 * @property {string} name 名前
 * @returns {_p.PersoniumException} 例外オブジェクト
 */
_p.PersoniumException = function(msg) {
    // JavaDAOからは、ステータスコードとレスポンスボディがカンマ区切りで通知される
    // よって、最初のカンマまでをステータスコードと判断し、それ以降をExceptionメッセージとする
    // また、以下のように、かならず、Javaのパッケージ名が先頭に含まれる
    // io.personium.client.DaoException: 409,{"code":"PR409-OD-0003","message":{"lang":"en","value":"The entity already exists."}}
    msg = msg.substring(msg.indexOf(" ")+1);
    this.message = msg;
    this.code = 0;
    this.name = "_p.PersoniumException";
    var ar = msg.split(",");
    if (ar.length > 1) {
        this.code = parseInt(ar[0]);
        if (this.code == null) {
          this.code = 0;
        }
        this.message = ar.slice(1).join();
    }
};
_p.PersoniumException.prototype = new Error();
