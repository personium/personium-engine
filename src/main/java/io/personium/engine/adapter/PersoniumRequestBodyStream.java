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
package io.personium.engine.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.personium.engine.extension.wrapper.PersoniumInputStream;

/**
 * Wrapper for InputStream of Request Body.
 */
public final class PersoniumRequestBodyStream {
    InputStream input;
    BufferedReader bufferReader;

    /**
     * Constructor.
     * @param input InputStream
     */
    public PersoniumRequestBodyStream(InputStream input) {
        this.input = input;
    }

    /**
     * returns wrapped InputStream, which is not shut by the ClassShutter.
     * @return PersoniumInputStream (wrapper)
     * @throws Exception
     */
    public PersoniumInputStream stream() throws Exception  {
        return new PersoniumInputStream(this.input);
    }

    /**
     * read one line.
     * @param encoding CharEncoding for reading
     * @return the line read in a String format.
     * @throws IOException IOException
     */
    public String readLine(String encoding) throws IOException {
        if (this.bufferReader == null) {
            this.bufferReader = new BufferedReader(new InputStreamReader(this.input, encoding));
        }
        return bufferReader.readLine();
    }

    /**
     * read one line in utf-8.
     * @return the line read in a String format.
     * @throws IOException IOException
     */
    public String readLine() throws IOException {
        return this.readLine("utf-8");
    }

    /**
     * Read the whole InputStream and return as String.
     * @param encoding CharEncoding for reading
     * @return read result String
     * @throws IOException IOException
     */
    public String readAll(String encoding) throws IOException {
        if (this.bufferReader == null) {
            this.bufferReader = new BufferedReader(new InputStreamReader(this.input, encoding));
        }
        StringBuilder sb = new StringBuilder();
        int chr;
        while ((chr = this.bufferReader.read()) != -1) {
            sb.append((char) chr);
        }
        return sb.toString();
    }
    /**
     * Read the whole InputStream and return as String.
     * @return String read result
     * @throws IOException
     */
    public String readAll() throws IOException {
        return this.readAll("utf-8");
    }
}
