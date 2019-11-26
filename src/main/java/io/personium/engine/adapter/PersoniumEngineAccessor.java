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

import java.util.ArrayList;
import java.util.Date;

import io.personium.client.Accessor;
import io.personium.client.DaoException;
import io.personium.client.PersoniumContext;
import io.personium.common.auth.token.AbstractOAuth2Token;
import io.personium.common.auth.token.ResidentLocalAccessToken;
import io.personium.common.auth.token.Role;
import io.personium.common.auth.token.TransCellAccessToken;

/**
 * Customized subclass of client Accessor class for PersoniumEngine.
 * TODO Class name should be changed to EngineClientAccessor
 */
public class PersoniumEngineAccessor extends Accessor {

    // AccessType for serviceSubject
    private static final String KEY_SELF = "self";
    // AccessType for client
    private static final String KEY_CLIENT = "client";

    private String serviceSubject;

    /**
     * Constructor for serviceSubject.
     * @param personiumContext PersoniumContext object
     * @param subject string of subject
     * @throws DaoException DaoException
     */
    public PersoniumEngineAccessor(PersoniumContext personiumContext, String subject) throws DaoException {
        super(personiumContext);
        this.serviceSubject = subject;
        setAccessType(KEY_SELF);
    }

    /**
     * Constructor for client.
     * @param personiumContext PersoniumContext object
     * @throws DaoException DaoException
     */
    public PersoniumEngineAccessor(PersoniumContext personiumContext) throws DaoException {
        super(personiumContext);
        setToken(personiumContext.getClientToken(), 0, "", 0);
        setAccessType(KEY_CLIENT);
    }

    @Override
    protected void certification() throws DaoException {
        // create token only if the access type is self.
        if (!KEY_SELF.equals(getAccessType())) {
            return;
        }

        String accessToken;
        Number expiresIn;
        String refreshToken = "";
        Number refreshExpiresIn = 0;

        long issuedAt = new Date().getTime();

        if (this.targetCellUrl != null) {
            // create TransCellToken
            TransCellAccessToken token = new TransCellAccessToken(
                issuedAt,
                getContext().getCurrentCellUrl(),
                getContext().getCurrentCellUrl() + "#" + this.serviceSubject,
                this.targetCellUrl,
                new ArrayList<Role>(),
                getBoxSchema(),
                new String[] {"root"}
            );
            accessToken = token.toTokenString();
            expiresIn = token.expiresIn();
        } else {
            // create AccountAccessToken
            ResidentLocalAccessToken localToken = new ResidentLocalAccessToken(
                issuedAt,
                ResidentLocalAccessToken.ACCESS_TOKEN_EXPIRES_HOUR * ResidentLocalAccessToken.MILLISECS_IN_AN_HOUR,
                getContext().getCurrentCellUrl(),
                this.serviceSubject,
                getBoxSchema(), AbstractOAuth2Token.Scope.ENGINE
            );
            accessToken = localToken.toTokenString();
            expiresIn = localToken.expiresIn();
        }

        setToken(accessToken, expiresIn, refreshToken, refreshExpiresIn);
    }

    // set the token information to Accessor
    private void setToken(String accessToken, Number expiresIn, String refreshToken, Number refreshExpiresIn) {
        setAccessToken(accessToken);
        setExpiresIn(expiresIn);
        setRefreshToken(refreshToken);
        setRefreshExpiresIn(refreshExpiresIn);
        setTokenType("Bearer");
    }

}
