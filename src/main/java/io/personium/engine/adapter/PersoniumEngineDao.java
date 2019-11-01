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
 * Customized subclass of PersoniumContext for use in PersoniumEngine.
 * TODO Class name should be changed to EngineClientContext
 */
public class PersoniumEngineDao extends PersoniumContext {

    private String serviceSubject;

    /**
     * Setter for Service Subject.
     * @param serviceSubject Service Subject
     */
    public void setServiceSubject(String serviceSubject) {
        this.serviceSubject = serviceSubject;
    }

    /**
     * Constructor.
     * @param url Base URL
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
     * create Accessor. 
     * @return created Accessor instance
     * @throws DaoException
     */
    public final Accessor asServiceSubject() throws DaoException {
        // Service Subject is not configured 
        if ("".equals(this.serviceSubject)) {
            throw DaoException.create("ServiceSubject undefined.", 0);
        }

        // TODO 設定されたアカウントが、存在することをチェックする。
        Accessor as = new PersoniumEngineAccessor(this, this.serviceSubject);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    /**
     * Accessor を生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
     * @return 生成したAccessorインスタンス
     * @throws DaoException DAO例外
     */
    public final Accessor withClientToken() throws DaoException {
        Accessor as = new PersoniumEngineAccessor(this);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    /**
     * returns wrapped JSONObject since direct JSONObject returned from java-client module is shut by class shutter.
     * @param jsonStr JSON String
     * @return JSONObject Wrapper
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
