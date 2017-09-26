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
import java.util.ArrayList;
import java.util.UUID;

import io.personium.client.Accessor;
import io.personium.client.Cell;
import io.personium.client.DaoException;
import io.personium.client.PersoniumContext;
import io.personium.common.auth.token.AccountAccessToken;
import io.personium.common.auth.token.TransCellAccessToken;
import io.personium.common.auth.token.Role;


public class PersoniumEngineAccessor extends Accessor {

    // AccessType for serviceSubject
    private static final String KEY_SELF = "self";
    // AccessType for client
    private static final String KEY_CLIENT = "client";

    private String serviceSubject;
    private String schemaUrl;

    // Constructor for serviceSubject
    public PersoniumEngineAccessor(PersoniumContext personiumContext, String subject, String schema) {
        super(personiumContext);
        this.serviceSubject = subject;
        this.schemaUrl = schema;
        setAccessType(KEY_SELF);
    }

    // Constructor for client
    public PersoniumEngineAccessor(PersoniumContext personiumContext, String token) {
        super(personiumContext);
        setToken(token, 0, "", 0);
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

        if (this.targetCellName != null) {
            // create TransCellToken
            TransCellAccessToken token = new TransCellAccessToken(
                UUID.randomUUID().toString(),
                issuedAt,
                TransCellAccessToken.LIFESPAN,
                getContext().getCellUrl(),
                getContext().getCellUrl() + "#" + this.serviceSubject,
                this.targetCellName,
                new ArrayList<Role>(),
                this.schemaUrl
            );
            accessToken = token.toTokenString();
            expiresIn = token.expiresIn();
        }
        else {
            // create AccountAccessToken
            AccountAccessToken localToken = new AccountAccessToken(
                issuedAt,
                AccountAccessToken.ACCESS_TOKEN_EXPIRES_HOUR * AccountAccessToken.MILLISECS_IN_AN_HOUR,
                getContext().getCellUrl(),
                this.serviceSubject,
                this.schemaUrl
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
