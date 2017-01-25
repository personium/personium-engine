/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.personium.engine.PersoniumEngineException;
import io.personium.engine.source.ISourceManager;
import io.personium.engine.source.EsServiceResourceSourceManager;
import io.personium.engine.source.FsServiceResourceSourceManager;

/**
 * DC-Engineサーブレットクラス.
 */
@Path("/{cell}/{scheme}/service/{id : .+}")
public class ServiceResource extends AbstractService {
    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(AbstractService.class);

    static {
        log.getClass();
    }

    /**
     * コンストラクタ.
     * @throws PersoniumEngineException DcEngine例外
     */
    public ServiceResource() throws PersoniumEngineException {
        super();
    }

    /**
     * Cell名取得.
     * @return Cell名
     */
    @Override
    public final String getCell() {
        return null;
    }

    /**
     * データスキーマURI取得.
     * @return データスキーマURI
     */
    @Override
    public final String getScheme() {
        return null;
    }

    @Override
    public ISourceManager getServiceCollectionManager() throws PersoniumEngineException {
        ISourceManager svcRsSourceManager = null;
        // ソースの管理情報を取得
        if (this.fsPath != null) {
          svcRsSourceManager = new FsServiceResourceSourceManager(this.fsPath);
        } else {
          svcRsSourceManager = new EsServiceResourceSourceManager(
              getIndex(), getType(), getId(), getRoutingId());
        }
        return svcRsSourceManager;
    }
}
