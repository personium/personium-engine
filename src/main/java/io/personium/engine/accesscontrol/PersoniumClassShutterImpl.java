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
package io.personium.engine.accesscontrol;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ClassShutter;

/**
 * ClassShutter Implementation for Personium Engine. 
 * Controlling the call-able Java packages on PersoniumEngine.
 */
public final class PersoniumClassShutterImpl implements ClassShutter {
    private static final Set<String> ACCEPTED = new HashSet<String>();

    private static String[] allow = {
            // Classes / Packages that Personium Engine opens access to the 
            // JavaScript layer.
            "io.personium.client.",
            "io.personium.engine.wrapper.",
            "io.personium.engine.adapter.",
            "io.personium.engine.extension.wrapper.",

            // Cannot delete because we use this in tests (Class)
            "ch.qos.logback.classic.Logger",

            // Exceptions
            "io.personium.engine.PersoniumEngineException",
    };

    @Override
    // Visibility Decision method
    public boolean visibleToScripts(final String fullClassName) {
        if (ACCEPTED.contains(fullClassName)) {
            return true;
        }
        for (String clazz : allow) {
            if (fullClassName.startsWith(clazz)) {
                ACCEPTED.add(fullClassName);
                return true;
            }
        }
        return false;
    }
}
