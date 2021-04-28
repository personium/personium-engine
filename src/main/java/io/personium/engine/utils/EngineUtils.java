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
package io.personium.engine.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utils class for Engine.
  */
public final class EngineUtils {
    /**
     * Consturctor.
     */
    private EngineUtils() {
    }

    /**
     * Read a local text file.
     * @param path target file path
     * @return text file content in String object
     * @throws IOException
     */
    public static String readFile(final String path) throws IOException {
        StringBuffer js = new StringBuffer();
        BufferedReader reader = null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = new FileInputStream(path);
            isr = new InputStreamReader(fis, "UTF-8");
            reader = new BufferedReader(isr);
            int chr;
            while ((chr = reader.read()) != -1) {
                js.append((char) chr);
            }
            return js.toString();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } finally {
                try {
                    if (isr != null) {
                        isr.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        }
    }
}
