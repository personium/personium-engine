/**
 * Personium
 * Copyright 2014-2021 - Personium Project Authors
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

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import io.personium.client.DaoException;
import io.personium.client.http.PersoniumRequestBuilder;
import io.personium.client.http.PersoniumResponse;
import io.personium.test.categories.Integration;

/**
 * User script test.
 */
@RunWith(PersoniumEngineRunner.class)
@Category({Integration.class })
public class ScriptTest extends ScriptTestBase {
    /**
     * constructor.
     */
    public ScriptTest() {
        super();
    }

    /**
     * Cell's CURD test.
     */
    @Test
    public final void cellTest() {
        callService("cell.js");
    }

    /**
     * Box's CURD test.
     */
    @Test
    public final void boxTest() {
        callService("box.js");
    }

    /**
     * Account CURD test.
     */
    @Test
    public final void accountTest() {
        callService("account.js");
    }

    /**
     * Account validate CURD test.
     */
    @Test
    public final void accountValidateTest() {
        callService("accountValidate.js");
    }

    /**
     * ServiceSubject test.
     */
    @Test
    public final void serviceSubjectTest() {
        callService("serviceSubject.js");
    }

    /**
     * ChangePassword's CURD test.
     */
    @Test
    public final void changePasswordTest() {
        callService("changePassword.js");
    }

    /**
     * ChangeMyPassword's CURD test.
     */
    @Test
    public final void changeMyPasswordTest() {
        callService("changeMyPassword.js");
    }

    /**
     * Event registration test.
     */
    @Test
    public final void eventTest() {
        callService("event.js");
    }

    /**
     * CellLevelEvent registration test.
     */
    @Test
    public final void cellLevelEventTest() {
        callService("cellLevelEvent.js");
    }

    /**
     * Get log file test.
     */
    @Test
    public final void cellLevelEventLogTest() {
        callService("cellLevelEventLog.js");
    }

    /**
     * Role CRUD test.
     */
    @Test
    public final void roleTest() {
        callService("role.js");
    }

    /**
     * Role (composite key) CRUD test.
     */
    @Test
    public final void roleComplexTest() {
        callService("roleComplex.js");
    }

    /**
     * Relation CRUD test.
     */
    @Test
    public final void relationTest() {
        callService("relation.js");
    }

    /**
     * Relation (composite key) CRUD test.
     */
    @Test
    public final void relationComplexTest() {
        callService("relationComplex.js");
    }

    /**
     * ExtRole CRUD test.
     */
    @Test
    public final void extRoleTest() {
        callService("extrole.js");
    }

    /**
     * ExtCell CRUD test.
     */
    @Test
    public final void extCellTest() {
        callService("extcell.js");
    }

    /**
     * MKCOL test.
     */
    @Test
    public final void mkColTest() {
        callService("mkcol.js");
    }

    /**
     * MKODATA test.
     */
    @Test
    public final void mkOdataTest() {
        callService("mkodata.js");
    }

    /**
     * MKSERVICE test.
     */
    @Test
    public final void mkServiceTest() {
        callService("mkservice.js");
    }

    /**
     * asExtCell test.
     */
    @Test
    public final void asExtCellTest() {
        callService("asExtCell.js");
    }

    /**
     * asTransCellAccessToken test.
     */
    @Test
    public final void asTransCellAccessTokenTest() {
        callService("asTransCellAccessToken.js");
    }

    /**
     * asRefreshToken test.
     */
    @Test
    public final void asRefreshTokenTest() {
        callService("asRefreshToken.js");
    }

    /**
     * asRefreshTokenError test.
     */
    @Test
    public final void asRefreshTokenErrorTest() {
        callService("asRefreshTokenError.js");
    }

    /**
     * asSchema test.
     */
    @Test
    public final void asSchemaTest() {
        callService("asSchema.js");
    }

    /**
     * asSchemaByToken test.
     */
    @Test
    public final void asSchemaByTokenTest() {
        callService("asSchemaByToken.js");
    }

    /**
     * asSelf & asClient test.
     */
    @Test
    public final void asSelfClientTest() {
        callService("asSelfClient.js");
    }

    /**
     * linkRelationRole test.
     */
    @Test
    public final void linkRelationRoleTest() {
        callService("linkRelationRole.js");
    }

    /**
     * linkRelationExtCell test.
     */
    @Test
    public final void linkRelationExtCellTest() {
        callService("linkRelationExtCell.js");
    }

    /**
     * linkExtCellRelation test.
     */
    @Test
    public final void linkExtCellRelationTest() {
        callService("linkExtCellRelation.js");
    }

    /**
     * linkExtCellRole test.
     */
    @Test
    public final void linkExtCellRoleTest() {
        callService("linkExtCellRole.js");
    }

    /**
     * linkRoleAccount test.
     */
    @Test
    public final void linkRoleAccountTest() {
        callService("linkRoleAccount.js");
    }

    /**
     * linkRoleExtCell test.
     */
    @Test
    public final void linkRoleExtCellTest() {
        callService("linkRoleExtCell.js");
    }

    /**
     * linkRoleRelation test.
     */
    @Test
    public final void linkRoleRelationTest() {
        callService("linkRoleRelation.js");
    }

    /**
     * linkExtRoleRole test.
     */
    @Test
    public final void linkExtRoleRoleTest() {
        callService("linkExtRoleRole.js");
    }

    /**
     * AssociationEnd test.
     */
    @Test
    public final void associationEndTest() {
        callService("associationEnd.js");
    }

    /**
     * ComplexType test.
     */
    @Test
    public final void complexTypeTest() {
        callService("complexType.js");
    }

    /**
     * Property test.
     */
    @Test
    public final void propertyTest() {
        callService("property.js");
    }

    /**
     * ComplexTypeProperty test.
     */
    @Test
    public final void complexTypePropertyTest() {
        callService("complexTypeProperty.js");
    }

    /**
     * linkAssociationEnd test.
     */
    @Test
    public final void linkAssociationEndTest() {
        callService("linkAssociationEnd.js");
    }

    /**
     * UserData test.
     */
    @Test
    public final void userDataTest() {
        callService("userDataTest.js");
    }

    /**
     * ACL test.
     */
    @Test
    public final void aclTest() {
        callService("acl.js");
    }

    /**
     * Normal system variation test of ACL setting.
     */
    @Test
    public final void aclNormalVariationTest() {
        callService("aclNormalVariation.js");
    }

    /**
     * Abnormal system variation test of ACL setting
     */
    @Test
    public final void aclErrorVariationTest() {
        callService("aclErrorVariation.js");
    }

    /**
     * NavigationPropertyPost test.
     */
    @Test
    public final void navigationPropertyPostTest() {
        callService("navigationPropertyPost.js");
    }

    /**
     * Upgrade unit test.
     */
    // TODO : Since the promotion is not currently supported on the Core side, the test is invalidated.
    @Ignore
    @Test
    public final void upgradeUnitTest() {
        callService("upgradeUnit.js");
    }

    /**
     * UserData query test.
     */
    @Test
    public final void userDetaQueryTest() {
        callService("userDataQuery.js");
    }

    /**
     * UserData query expand test.
     */
    @Test
    public final void userDetaQueryExpandTest() {
        callService("userDataQueryExpand.js");
    }

    /**
     * Java library can not be called.
     */
    @Test
    public final void accessDenyJavaAPITest() {
        callService("accessDenyJavaAPI.js");
    }

    /**
     * 呼び出しを許しているwrapperパッケージのクラスのコンストラクタが呼び出せないこと.
     */
    @Test
    public final void cantCallConstructorTest() {
        callService("cantCallConstructor.js");
    }

    /**
     * An error occurs if the required file is abnormal.
     */
    @Test
    @Ignore // Requiring erroneous script throw exception (and return 500)
    public final void requireFileErrorTest() {
        if (isServiceTest) {
            // スクリプトの登録 （Davのput）
            putScript("requireEvalErrorSub.js", "requireEvalErrorSub.js");
            callService("requireEvalError.js");
        }
        if (isServiceTest) {
            // スクリプトの削除（Davのdel）
            try {
                testSvcCol.del("requireEvalErrorSub.js");
            } catch (DaoException e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * An error occurs when the required file does not exist.
     */
    @Test
    public final void requireNoFileTest() {
        callService("requireNoFile.js");
    }

    /**
     * box installation test
     */
    @Test
    public final void boxInstallationTest() {
        if (!isServiceTest) return;

        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("testBar.bar");

            putResource("testBar.bar", "application/zip", is);

            try {
                callService("boxInstallation.js");
            } finally {
                try {
                    delResource("testBar.bar");
                } catch(DaoException e){
                    fail(e.getMessage());
                }
            }
        } catch(DaoException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Binary upload and download of WebDav files..
     */
    @Test
    public final void WebDavファイルのバイナリでのアップロード及びダウンロード() {

        String url;
        String testSrc = "DAOBinaryIO.js";
        HttpUriRequest req = null;
        String reqBody = "reqbodydata------\naaa\nbbb\nあいうえお\n";
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("POST").body(reqBody).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals(reqBody, dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * ファイルのバイナリからの読み込み.
     */
    @Test
    public final void ファイルのバイナリからの読み込み() {

        String url;
        String testSrc = "DAOBinaryRead.js";
        HttpUriRequest req = null;
        String reqBody = "reqbodydata------\naaa\nbbb\nあいうえお\n";
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("POST").body(reqBody).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals(reqBody, dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * 存在しないService test.
     */
    @Test
    public final void serviceNotFound() {
        if (isServiceTest) {
            String url = String.format("%s/%s/%s/%s/test?cell=%s", baseUrl, cellName, boxName, "notfoundsvccol",
                    cellName);
            try {
                HttpUriRequest req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Personium-Version", version);
                }
                request(req);
                fail();
            } catch (DaoException e) {
                assertEquals("404", e.getCode());
            }
        }
    }

    /**
     * スクリプトが空の場合のService実行テスト.
     */
    @Test
    public final void serviceEmpty() {
        String url;
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript("empty.js", "test.js");
                url = requestUrl();
            } else {
                url = requestUrl("empty.js");
            }
            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            // ステータスコードが５００＆Content-Lengh 返却されていることを確認
            int statusCode = objResponse.getStatusLine().getStatusCode();
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
            String contentLength = dcRes.getHeader(HttpHeaders.CONTENT_LENGTH);
            if (contentLength == null || contentLength.length() <= 0) {
                fail("Content-Lengh header value does not exist");
            }
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトに日本語が含まれる場合に正常にサービス実行できること.
     */
    @Test
    public final void serviceJapanese() {
        String url;
        String testSrc = "japanese.js";
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals("テストです。", dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトから未サポートのレスポンスコードが返却された場合不正なHTTPレスポンスが指定されたエラーとなること.
     */
    @Test
    public final void nonSupportedResponseCode() {
        String url;
        HttpUriRequest req = null;
        try {
            String[][] jsList = {
                    {"nonSupportedResponseCode105.js", "105"},
                    {"nonSupportedResponseCode2000.js", "2000"},
            };
            for (String[] testSrc : jsList) {
                // スクリプトの登録 （Davのput）
                if (isServiceTest) {
                    putScript(testSrc[0], "test.js");
                    url = requestUrl();
                } else {
                    url = requestUrl(testSrc[0]);
                }
                // サービスの実行
                req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Personium-Version", version);
                }
                // レスポンスのチェック
                HttpResponse objResponse = httpClient.execute(req);
                PersoniumResponse dcRes = new PersoniumResponse(objResponse);
                assertEquals(500, dcRes.getStatusCode());
                String expectedMessage = String.format("Server Error : response status illegal type. status: %s",
                        testSrc[1]);
                assertEquals(expectedMessage, dcRes.bodyAsString());
                EntityUtils.consume(objResponse.getEntity());
            }
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトから未サポートのレスポンスコードが返却された場合不正なHTTPレスポンスが指定されたエラーとなること.
     * (レスポンスコードはクエリで指定).
     */
    @Test
    public final void nonSupportedResponseCodeWithQuery() {
        String url;
        HttpUriRequest req = null;
        try {
            String[][] jsList = {
                    {"returnResponseCodewithQuery.js", "105"},
                    {"returnResponseCodewithQuery.js", "2000"}
            };
            for (String[] testSrc : jsList) {
                // スクリプトの登録 （Davのput）
                if (isServiceTest) {
                    putScript(testSrc[0], "test.js");
                    url = requestUrl() + "&status=" + testSrc[1];
                } else {
                    url = requestUrl(testSrc[0]) + "&status=" + testSrc[1];
                }
                // サービスの実行
                req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Personium-Version", version);
                }
                // レスポンスのチェック
                HttpResponse objResponse = httpClient.execute(req);
                PersoniumResponse dcRes = new PersoniumResponse(objResponse);
                assertEquals(500, dcRes.getStatusCode());
                String expectedMessage = String.format("Server Error : response status illegal type. status: %s",
                        testSrc[1]);
                assertEquals(expectedMessage, dcRes.bodyAsString());
                EntityUtils.consume(objResponse.getEntity());
            }
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトから未サポートのレスポンスヘッダが返却された場合指定したヘッダが取得できること.
     */
    @Test
    public final void nonSupportedResponseHeader() {
        String url;
        String testSrc = "nonSupportedResponseHeader.js";

        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            assertEquals(200, dcRes.getStatusCode());
            assertEquals("header value", dcRes.getHeader("Invalid-custom-header"));
            assertEquals("テストです。", dcRes.bodyAsString());

        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトからTransfer-Encodingヘッダが返却された場合無視されること.
     */
    @Test
    public final void nonSupportedResponseHeaderTransferEncoding() {
        String url;
        String testSrc = "nonSupportedResponseHeaderTransferEncoding.js";

        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            assertEquals(200, dcRes.getStatusCode());
            assertEquals("", dcRes.getHeader("Transfer-Encoding"));
            assertEquals("テストです。", dcRes.bodyAsString());

        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトからContent-typeとレスポンスボディが不一致の場合でもレスポンスが取得できること.
     */
    @Test
    public final void unmatchedBodyAndContentType() {
        String url;
        String testSrc = "unmatchedBodyAndContentType.js";

        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new PersoniumRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Personium-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            PersoniumResponse dcRes = new PersoniumResponse(objResponse);

            String bodyAsString = dcRes.bodyAsString();
            assertEquals(200, dcRes.getStatusCode());
            assertEquals("テストです。", bodyAsString);

        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * レスポンスにIllegalな値を設定した場合エラーになること.
     */
    @Test
    public final void レスポンスにIllegalな値を設定した場合エラーになること() {

        // 異常なレスポンスパターンとレスポンスボディのテストセット
        IllegalResponseFixture[] datas = {
                // レスポンスがJSGIの形式になっていない。
                new IllegalResponseFixture("Server Error : not NativeObject", "[]"),
                // ステータスコードが数値以外。
                new IllegalResponseFixture("Server Error : response status illegal type.",
                        "{status:\"hoge\",headers:{},body:[]}"),
                // ステータスコードが4桁。
                new IllegalResponseFixture("Server Error : response status illegal type. status: 2000",
                        "{status:2000,headers:{},body:[]}"),
                // ヘッダーが未定義。
                new IllegalResponseFixture("Server Error : not headers", "{status:200,body:[]}"),
                // ヘッダーのキーが文字列ではない。
                new IllegalResponseFixture("Server Error : header key format error",
                        "{status:200,headers:{100:\"hoge\"},body:[]}"),
                // ヘッダーの値が文字列ではない。
                new IllegalResponseFixture("Server Error : header value format error",
                        "{status:200,headers:{head:1000},body:[]}"),
                // ヘッダーのContent-Typeのメディア・タイプが存在しない値。
                new IllegalResponseFixture("Server Error : Response header parsing media type.",
                        "{status:200,headers:{\"Content-Type\":\"hoge\"},body:[]}"),
                // ヘッダーのContent-Typeのcharsetが存在しない値。
                new IllegalResponseFixture("Server Error : response charset illegal type.",
                        "{status:200,headers:{\"Content-Type\":\"plain/text;charset=hoge\"},body:[]}"),
                // レスポンスデータが空。
                new IllegalResponseFixture("Server Error : response body undefined forEach.",
                        "{status:200,headers:{}}"),
                // レスポンスデータのforEachが未実装。
                new IllegalResponseFixture("Server Error : response body undefined forEach.",
                        "{status:200,headers:{},body:{}}"),
                // レスポンスデータで返す値が文字列ではない。
                new IllegalResponseFixture("Server Error : response body illegal type.",
                        "{status:200,headers:{},body:[1]}") };

        String url;
        String testSrc = "responseIllegal.js";
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            for (int i = 0; i < datas.length; i++) {
                // サービスの実行
                req = new PersoniumRequestBuilder().url(url).method("POST").body(
                        datas[i].requestJson).token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Personium-Version", version);
                }

                HttpResponse objResponse;
                objResponse = httpClient.execute(req);
                PersoniumResponse dcRes = new PersoniumResponse(objResponse);

                assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, dcRes.getStatusCode());
                assertEquals(datas[i].responseMessage, dcRes.bodyAsString());
            }

        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * キャッシュ機構の確認のために2回呼び出しするテスト.
     */
    @Test
    public final void キャッシュ機構の確認のために2回呼び出しするテスト() {
        callService("cell.js");
        callService("cell.js");
    }

}
