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
package io.personium.engine.rs;

import javax.ws.rs.Path;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.source.SystemServiceResourceSourceManager;
import io.personium.engine.source.ISourceManager;

/**
 * JAX-RS Resource class.
 */
@Path("/{cell}/{schema}/system/{id : .+}")
public class SystemServiceResource extends AbstractService {
    /**
     * Constructor.
     * @throws PersoniumEngineException PersoniumEngineException
     */
    public SystemServiceResource() throws PersoniumEngineException {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getCell() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getSchemaURI() {
        return null;
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public ISourceManager getServiceCollectionManager() throws PersoniumEngineException {
        ISourceManager svcRsSourceManager = null;
        // Create source manager for SystemServiceResource
        if (this.getServiceName() != null) {
          svcRsSourceManager = new SystemServiceResourceSourceManager(this.getServiceName());
        }
        return svcRsSourceManager;
    }
}
