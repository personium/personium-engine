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
package io.personium.engine;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.common.auth.token.LocalToken;
import io.personium.common.auth.token.TransCellAccessToken;
import io.personium.core.model.file.DataCryptor;
import io.personium.engine.rs.DebugResource;
import io.personium.engine.rs.ServiceResource;
import io.personium.engine.rs.StatusResource;
import io.personium.engine.rs.TestResource;
import io.personium.engine.utils.PersoniumEngineConfig;

/**
 * Personium-Engine.
 */
public class PersoniumEngineApplication extends Application {
    static Logger log = LoggerFactory.getLogger(PersoniumEngineApplication.class);
    /** デバッグフラグ. */
    // private static final String KEY_DCENGINE_DEBUG = "io.personium.engine.debug";
    static {
        try {
            TransCellAccessToken.configureX509(PersoniumEngineConfig.getX509PrivateKey(),
                    PersoniumEngineConfig.getX509Certificate(), PersoniumEngineConfig.getX509RootCertificate());
            LocalToken.setKeyString(PersoniumEngineConfig.getTokenSecretKey());
            DataCryptor.setKeyString(PersoniumEngineConfig.getTokenSecretKey());
        } catch (Exception e) {
            log.warn("Failed to start server.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Set.
     * @return Classリスト
     */
    @Override
    public final Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ServiceResource.class);
        // if (Boolean.valueOf(this.servletContext.getInitParameter(KEY_DCENGINE_DEBUG))) {
        classes.add(StatusResource.class);
        classes.add(DebugResource.class);
        classes.add(TestResource.class);
        // }
        return classes;
    }
}
