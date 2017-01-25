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
package io.personium.unit.engine.test.extension.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import io.personium.engine.extension.support.ExtensionJarLoader;
import io.personium.engine.extension.support.JavaClassRevealFilter;
import io.personium.unit.Unit;

/**
 * JavaClassRevealFilterのユニットテスト.
 */
@Category({ Unit.class })
public class ExtensionJarLoaderTest {

    /**
     * 各テストの実効前に実行する処理.
     * @throws Exception テスト実行前の前提条件設定に失敗した場合
     */
    @Before
    public void before() throws Exception {
        // クラスローダが singleton化されたことにより、一部のテスト実施でエラーがでるため、
        //  テスト中は毎回 singletonを消して、新たにクラスローダが作成されるように修正。
        Field field = ExtensionJarLoader.class.getDeclaredField("singleton");
        field.setAccessible(true);
        field.set(null, null);
    }

    /**
     * 各テストの最後に実行する処理.
     */
    @After
    public void after() {
        // Extention用の jarファイルの置場所をデフォルト設定に戻す
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, ExtensionJarLoader.DEFAULT_EXTENSION_DIR);
    }

    /**
     * デフォルトディレクトリのjarファイルをロードしその中のクラスがロードされていること.
     * @throws Exception エラー
     */
    @Test
    public void 指定ディレクトリのjarファイルをロードしその中のクラスがロードされていること() throws Exception {

        String fileName = this.getClass().getClassLoader().getResource("extension").getFile();
        System.out.println("#####>>>> " + fileName);
        // Extension用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);
        // fileName.substring(0, fileName.lastIndexOf("/")));

        // テスト前に本テスト対象のクラスがデフォルトクラスローダにロードされていないことを確認
        try {
            this.getClass().getClassLoader().loadClass("io.personium.engine.extension.test.NonRevealingClass");
            fail("Target class is already loaded.");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        try {
            this.getClass().getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
            fail("Target class is already loaded.");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }

        ExtensionJarLoader loader = ExtensionJarLoader.getInstance(this.getClass().getClassLoader(),
                new JavaClassRevealFilter());

        Class<?> clazz1 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.NonRevealingClass");
        assertNotNull(clazz1);
        assertTrue(clazz1 instanceof Class);
        assertFalse(ScriptableObject.class.isAssignableFrom(clazz1));

        Class<?> clazz2 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
        assertNotNull(clazz2);
        assertTrue(clazz2 instanceof Class);
        assertTrue(ScriptableObject.class.isAssignableFrom(clazz2));

        // ScriptableObjectを継承した Revealingクラスが ExtensionJarLoaderに設定されていること。
        Set<Class<? extends Scriptable>> set = loader.getPrototypeClassSet();
        assertFalse(set.contains(clazz1));
        assertTrue(set.contains(clazz2));
    }


    /**
     * Extension用クラスのプロパティファイルがロードできること.
     * @throws Exception エラー
     */
    @Test
    public void Extension用クラスのプロパティファイルがロードできること() throws Exception {

        String fileName = this.getClass().getClassLoader().getResource("extension").getFile();
        System.out.println("#####>>>> " + fileName);
        // Extension用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);

        // テスト前に本テスト対象のクラスがデフォルトクラスローダにロードされていないことを確認
        try {
            this.getClass().getClassLoader().loadClass("io.personium.engine.extension.test.NonRevealingClass");
            fail("Target class is already loaded.");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        try {
            this.getClass().getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
            fail("Target class is already loaded.");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }

        ExtensionJarLoader loader = ExtensionJarLoader.getInstance(this.getClass().getClassLoader(),
                new JavaClassRevealFilter());

        Class<?> clazz1 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.NonRevealingClass");
        assertNotNull(clazz1);
        assertTrue(clazz1 instanceof Class);
        assertFalse(ScriptableObject.class.isAssignableFrom(clazz1));

        Class<?> clazz2 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
        assertNotNull(clazz2);
        assertTrue(clazz2 instanceof Class);
        assertTrue(ScriptableObject.class.isAssignableFrom(clazz2));

        // ScriptableObjectを継承した Revealingクラスが ExtensionJarLoaderに設定されていること。
        Set<Class<? extends Scriptable>> set = loader.getPrototypeClassSet();
        assertFalse(set.contains(clazz1));
        assertTrue(set.contains(clazz2));

        // Ext_RevealingClass.properties の内容が読み取れることを確認
        Object instance = clazz2.newInstance();
        Method getPropMethod = instance.getClass().getMethod("getProperties", new Class[] {});
        Object prop = getPropMethod.invoke(instance, new Object[] {});
        assertTrue(prop instanceof Properties);
        assertEquals(((Properties) prop).get("propKey"), "propValue");
    }

    /**
     * 存在しないディレクトリをextension_jarフォルダとして指定した場合でも例外が発生しないこと.
     * @throws Exception エラー
     */
    @Test
    public void 存在しないディレクトリをextension_jarフォルダとして指定した場合でも例外が発生しないこと() throws Exception {

        // 存在しないディレクトリを指定
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, "nonExistent");

        ExtensionJarLoader loader = ExtensionJarLoader.getInstance(this.getClass().getClassLoader(),
                new JavaClassRevealFilter());

        try {
            Class<?> clazz2 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
            fail();
        } catch (ClassNotFoundException e) {
            assertTrue(true);
        }
    }

    /**
     * 空のディレクトリをextension_jarフォルダとして指定した場合でも例外が発生しないこと.
     * @throws Exception エラー
     */
    @Test
    @Ignore
    // Eclipseが、target/test-classesに空のディレクトリを作ってくれないので skip
    public void 空のディレクトリをextension_jarフォルダとして指定した場合でも例外が発生しないこと() throws Exception {

        String fileName = this.getClass().getClassLoader().getResource("emptyExtension").getFile();
        System.out.println("#####>>>> " + fileName);
        // Extension用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);

        ExtensionJarLoader loader = ExtensionJarLoader.getInstance(this.getClass().getClassLoader(),
                new JavaClassRevealFilter());

        try {
            Class<?> clazz2 = loader.getClassLoader().loadClass("io.personium.engine.extension.test.Ext_RevealingClass");
            fail();
        } catch (ClassNotFoundException e) {
            assertTrue(true);
        }
    }

}
