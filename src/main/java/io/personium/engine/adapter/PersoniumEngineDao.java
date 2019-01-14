/**
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
package io.personium.engine.adapter;

import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.personium.client.Accessor;
import io.personium.client.DaoException;
import io.personium.client.PersoniumContext;
import io.personium.engine.wrapper.PersoniumJSONObject;

/**
 * Dao for Personium-Engine.
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

    /**
     * コンストラクタ.
     * @param url 基底URL
     * @param name Cell Name
     * @param boxSchema Box DataSchemaURI
     * @param bName Box-Name
     * @param pathBaseEnabled pathBaseEnabled
     * @throws DaoException DaoException
     */
    public PersoniumEngineDao(final String url, final String name, final String boxSchema,
            final String bName, boolean pathBaseEnabled)
            throws DaoException {
        super(url, name, boxSchema, bName, pathBaseEnabled);
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

        // TODO 設定されたアカウントが、存在することをチェックする。

        Accessor as = new PersoniumEngineAccessor(this, this.serviceSubject);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    /**
     * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
     * @return 生成したAccessorインスタンス
     * @throws DaoException DAO例外
     */
    public final Accessor withClientToken() throws DaoException {
        Accessor as = new PersoniumEngineAccessor(this);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
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
