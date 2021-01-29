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

import io.personium.engine.source.FilenameResolverByName;
import io.personium.engine.source.FilenameResolverByRoute;
import io.personium.jersey.engine.test.categories.Integration;

import org.junit.experimental.categories.Category;
import org.junit.Test;

/**
 * FilenameResolver Unit Test.
 */
@Category({Integration.class})
public class FilenameResolverTest {
    /**
     * Test for static path resolver
     */
    @Test
    public void 静的パスを正しく解決できるかのテスト() {
        FilenameResolverByName resolver = new FilenameResolverByName();
        try {
            resolver.registerRoute("aaa", "test1.js");
            resolver.registerRoute("aaaBBB", "test2.js");
            resolver.registerRoute("aaa123", "test3.js");
            resolver.registerRoute("aaaBBB123", "test4.js");
            resolver.registerRoute("BBBaaa", "test5.js");

            assertEquals("test1.js", resolver.resolve("aaa"));
            assertEquals("test2.js", resolver.resolve("aaaBBB"));
            assertEquals("test3.js", resolver.resolve("aaa123"));
            assertEquals("test4.js", resolver.resolve("aaaBBB123"));
            assertEquals("test5.js", resolver.resolve("BBBaaa"));
            assertEquals(null, resolver.resolve("/aaa"));
            assertEquals(null, resolver.resolve(null));
            assertEquals(null, resolver.resolve("aaa/bbb"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test for dynamic path resolver
     */
    @Test
    public void 動的パスを正しく解決できるかのテスト() {
        FilenameResolverByRoute resolver = new FilenameResolverByRoute();
        try {
            resolver.registerRoute("aaa", "test1.js");
            resolver.registerRoute("{id}/BBB", "test2.js");
            resolver.registerRoute("aaa/{id}/view", "test3.js");
            resolver.registerRoute("{aaa}BBB123", "test4.js");
            resolver.registerRoute("aaa{id}", "test5.js");

            assertEquals("test1.js", resolver.resolve("aaa"));
            assertEquals("test2.js", resolver.resolve("aaa/BBB"));
            assertEquals("test3.js", resolver.resolve("aaa/1234/view"));
            assertEquals("test4.js", resolver.resolve("{aaaBBB123"));
            assertEquals("test5.js", resolver.resolve("aaaBBB"));
            assertEquals(null, resolver.resolve("/aaa"));
            assertEquals(null, resolver.resolve(null));
            assertEquals(null, resolver.resolve("aaa/1234/edit"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test for illegal format of route
     */
    @Test
    public void 動的パスに不適当なパターンを入れたときに例外を出すテスト() {
        FilenameResolverByRoute resolver = new FilenameResolverByRoute();
        try {
            resolver.registerRoute("{id/bbb", "src.js");
            fail();
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            assertEquals(e.getClass().getName(), IllegalArgumentException.class.getName());
        }
    
    }
}