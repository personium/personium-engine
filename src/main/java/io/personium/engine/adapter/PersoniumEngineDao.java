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
package io.personium.engine.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.personium.client.Accessor;
import io.personium.client.DaoException;
import io.personium.client.PersoniumContext;
import io.personium.common.auth.token.AccountAccessToken;
import io.personium.engine.wrapper.PersoniumJSONObject;

/**
 * Personium-Engine用DAO.
 */
public class PersoniumEngineDao extends PersoniumContext {

    private String serviceSubject;

    /**
     * サービスサブジェクトのsetter.
     * @param serviceSubject サービスサブジェクト
     */
    public void setServiceSubject(String serviceSubject) {
        this.serviceSubject = serviceSubject;
    }

    private String schemaUrl;

    /**
     * ボックスのスキーマURLのsetter.
     * @param schemaUrl ボックススキーマURL
     */
    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    /**
     * コンストラクタ.
     * @param url 基底URL
     * @param name Cell Name
     * @param boxSchema Box DataSchemaURI
     * @param bName Box-Name
     */
    public PersoniumEngineDao(final String url, final String name, final String boxSchema, final String bName) {
        super(url, name, boxSchema, bName);
    }

    /**
     * アクセッサを生成します. マスタトークンを利用し、アクセッサを生成します。（正式実装は セルフトークンを利用する）
     * @return 生成したAccessorインスタンス
     * @throws DaoException DAO例外
     */
    public final Accessor asServiceSubject() throws DaoException {
        // サービスサブジェクト設定が未設定
        if ("".equals(this.serviceSubject)) {
            throw DaoException.create("ServiceSubject undefined.", 0);
        }

        // 設定されたアカウントが、存在することをチェックする。

        // トークン生成
        long issuedAt = new Date().getTime();
        AccountAccessToken localToken = new AccountAccessToken(
                issuedAt,
                AccountAccessToken.ACCESS_TOKEN_EXPIRES_HOUR * AccountAccessToken.MILLISECS_IN_AN_HOUR,
                this.getCellUrl(),
                this.serviceSubject,
                this.schemaUrl);

        Accessor as = this.withToken(localToken.toTokenString());
        as.setAccessType(Accessor.KEY_SELF);
        return as;
    }

    /**
     * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
     * @return 生成したAccessorインスタンス
     * @throws DaoException DAO例外
     */
    public final Accessor withClientToken() throws DaoException {
        return this.withToken(this.getClientToken());
    }

    /**
     * クライアントライブラリで、JavaのJSONObjectを直接扱えないためラップして返す.
     * @param jsonStr JSON文字列
     * @return JSONObjectのラッパー
     * @throws ParseException ParseException
     */
    public final PersoniumJSONObject newJSONObject(final String jsonStr) throws ParseException {
        PersoniumJSONObject json = (PersoniumJSONObject) (new JSONParser().parse(jsonStr, new ContainerFactory() {

            @SuppressWarnings("rawtypes")
            @Override
            public Map createObjectContainer() {
                return new PersoniumJSONObject();
            }

            @SuppressWarnings("rawtypes")
            @Override
            public List creatArrayContainer() {
                return null;
            }
        }));
        return json;
    }
}
