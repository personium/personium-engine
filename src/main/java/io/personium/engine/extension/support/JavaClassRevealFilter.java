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
package io.personium.engine.extension.support;


/**
 * ExtensionClassFilter class that discloses only classes whose name start with  \"Ext_\".
 * TODO class name should be changed to DefaultExtensionClassFilter.
 */
public class JavaClassRevealFilter implements ExtensionClassFilter {

    private static final String REVEAL_CLASS_PREFIX = "Ext_";

    @Override
    public boolean accept(String packageName, String className) {
        if (null == packageName || null == className) {
            return false;
        }
        // return true when class name starts with "Ext_" 
        return className.startsWith(REVEAL_CLASS_PREFIX);
    }

    @Override
    public String getDescription() {
        return "Disclose only classes whose name start with  \"Ext_\".";
    }
}
