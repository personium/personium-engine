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
package io.personium.jersey.engine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import io.personium.client.DaoException;
import io.personium.client.http.PersoniumRequestBuilder;
import io.personium.client.http.PersoniumResponse;
import io.personium.jersey.engine.test.categories.Integration;

/**
 * User script test.
 */
@RunWith(PersoniumEngineRunner.class)
@Category({Integration.class })
public class ScriptTestForDynamicPath extends ScriptTestBase {
    /**
     * constructor.
     */
    public ScriptTestForDynamicPath() {
        super("io.personium.engine");
    }

    /**
     * generate request url for executing via personium-core
     * @param relativePath engine reletive path in service collection
     */
    protected String requestUrlForIntegrationTest(final String relativePath) {
        return String.format("%s/%s/%s/%s/%s?cell=%s", baseUrl, cellName, boxName, serviceCollectionName, relativePath, cellName);
    }

    /**
     * Testing whether dynamic path is available
     */
    @Test
    public final void dynamicPathAvailableTest() {
        if (!isServiceTest) return;

        putScript("cell.js", "test.js");

        try {
            configureService("{id}/test", "test.js");
            String url = requestUrlForIntegrationTest("hogehoge/test");
            callServiceTest(url);
        } catch(DaoException e) {
            fail(e.getMessage());
        } finally {
            delScript("test.js");
        }
    }

    /**
     * Testing whether dynamic path is available if routes contain illegal pattern
     */
    @Test
    public final void dynamicPathWithIllegalRoutePatternReturns503() {
        if (!isServiceTest) return;

        putScript("cell.js", "test.js");

        String url = requestUrlForIntegrationTest("hogehoge/test");
        try {
            configureService(new PersoniumServiceRoute[] {
                new PersoniumServiceRoute("test", "test.js"),
                new PersoniumServiceRoute("{id/test", "test.js"),
            });

            HttpUriRequest req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }
            HttpResponse res = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(res);
            assertEquals(HttpStatus.SC_SERVICE_UNAVAILABLE, dcRes.getStatusCode());
        } catch(Exception e) {
            fail(e.getMessage());
        } finally {
            delScript("test.js");
        }
    }
}
