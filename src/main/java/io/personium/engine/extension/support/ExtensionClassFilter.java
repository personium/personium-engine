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
package io.personium.engine.extension.support;

/**
 * Filter interface for hiding / disclosing java classes of the Extension against JavaScript layer.
 */
public interface ExtensionClassFilter {

    /**
     * Method for defining which class in which package should be disclosed to JavaScript layer.
     * @param packageName Package Name
     * @param className Class Name
     * @return true if the class is to be revealed as Extension, false otherwise.
     */
    boolean accept(String packageName, String className);

    /**
     * Method for defining the description of the filter.
     * @return Description String
     */
    String getDescription();
}
