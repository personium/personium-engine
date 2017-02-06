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
package io.personium.unit.engine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import io.personium.engine.utils.PersoniumEngineConfig;
import io.personium.jersey.engine.test.categories.Integration;

/**
 * PersoniumEngineConfig ユニットテストクラス.
 */
@Category({Integration.class })
public class PersoniumEngineConfigTest {

    /**
     * ユニットテスト用クラス.
     */
    public class UnitPersoniumEngineConfig extends PersoniumEngineConfig {
        /**
         * コンストラクタ.
         */
        public UnitPersoniumEngineConfig() {
            super();
        }

        /**
         * 設定ファイルを読み込む.
         * @param configFilePath 設定ファイルパス
         * @return 設定ファイルIS
         */
        public InputStream unitGetConfigFileInputStream(String configFilePath) {
            return this.getConfigFileInputStream(configFilePath);
        }
    }

    /**
     * 存在するプロパティファイルのパスを指定した場合_指定したパスのプロパティファイルを読み込むこと.
     */
    @Test
    public void 存在するプロパティファイルのパスを指定した場合_指定したパスのプロパティファイルを読み込むこと() {
        UnitPersoniumEngineConfig dcEngineConfig = new UnitPersoniumEngineConfig();
        Properties properties = new Properties();
        String configFilePath = ClassLoader.getSystemResource("personium-unit-config.properties.unit").getPath();
        try {
            properties.load(dcEngineConfig.unitGetConfigFileInputStream(configFilePath));
        } catch (IOException e) {
            fail("properties load failuer");
        }
        assertEquals("unitTest", properties.getProperty("io.personium.engine.testkey"));
    }

    /**
     * 存在しないプロパティファイルのパスを指定した場合_クラスパス上のプロパティを読み込むこと.
     */
    @Test
    @Ignore
    public void 存在しないプロパティファイルのパスを指定した場合_クラスパス上のプロパティを読み込むこと() {
        UnitPersoniumEngineConfig dcEngineConfig = new UnitPersoniumEngineConfig();
        Properties properties = new Properties();
        try {
            properties.load(dcEngineConfig.unitGetConfigFileInputStream("personium-unit-config.properties.unit"));
        } catch (IOException e) {
            fail("properties load failuer");
        }
        assertEquals("unitTestDefault", properties.getProperty("io.personium.engine.testkey"));
    }

    /**
     * io.personium.engnie系プロパティで定義された内容が、io.personium.coreプロパティとして取得できること.
     */
    @Test
    public void io_personium_engnie系プロパティで定義された内容が_io_personium_coreプロパティとして取得できること() {
        System.setProperty("io.personium.configurationFile",
                "src/test/resources/personium-unit-config.properties.unit");
        PersoniumEngineConfig.reload();
        assertEquals("unitTest", PersoniumEngineConfig.get("io.personium.engine.testkey"));
        assertEquals("unitTest", PersoniumEngineConfig.get("io.personium.core.testkey"));

        assertEquals("keyWithCorePrefix", PersoniumEngineConfig.get("io.personium.core.testKey2"));
        assertEquals("keyWithEnginePrefix", PersoniumEngineConfig.get("io.personium.engine.testKey3"));
        assertEquals("keyWithEnginePrefix", PersoniumEngineConfig.get("io.personium.core.testKey3"));
    }
}
