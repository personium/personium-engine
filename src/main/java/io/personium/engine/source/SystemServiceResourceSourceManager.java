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
package io.personium.engine.source;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.utils.PersoniumUtils;

/**
 * Service resource source management for system service.
 */
public class SystemServiceResourceSourceManager implements ISourceManager {
    private static Logger log = LoggerFactory.getLogger(SystemServiceResourceSourceManager.class);

    private String serviceName;

    /**
     * Constructor.
     * @param service sevice name
     */
    public SystemServiceResourceSourceManager(final String service) {
        this.serviceName = service;
        log.info("ServiceName: [" + this.serviceName + "]");
    }

    /**
     * Get service subject.
     * @return serviceSubject
     */
    public String getServiceSubject() {
      return "_engine";
    }

    /**
     * Get script name for service path.
     * @param servicePath service path
     * @return service name
     */
    public String getScriptNameForServicePath(String servicePath) {
        return this.serviceName + ".js";
    }

    /**
     * Get contents of source file.
     * @param sourceName source file naem
     * @return string of source file
     * @throws PersoniumEngineException Exception about Engine
     */
    public String getSource(String sourceName) throws PersoniumEngineException {
        try {
            URL path = getClass().getResource("/js-system/" + sourceName);

            return PersoniumUtils.readFile(path.getFile());
        } catch (Exception e) {
            log.info("System Script read error ", e);
            throw new PersoniumEngineException("404 Not Found", PersoniumEngineException.STATUSCODE_NOTFOUND, e);
        }
    }
}
