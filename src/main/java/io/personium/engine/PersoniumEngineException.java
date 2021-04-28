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
package io.personium.engine;

/**
 * base Exception class for PersoniumEngine.
 */
@SuppressWarnings("serial")
public class PersoniumEngineException extends Exception {
    /** 500. */
    public static final int STATUSCODE_SERVER_ERROR = 500;
    /** 404. */
    public static final int STATUSCODE_NOTFOUND = 404;
    /** 200. */
    public static final int STATUSCODE_SUCCESS = 200;

    /** Original Exception Object. */
    private Exception originalException = null;
    /** Status Code. */
    private int statusCode = PersoniumEngineException.STATUSCODE_SUCCESS;

    /**
     * Constructor.
     * @param msg Message
     * @param code Status Code
     * @param e Original Exception
     */
    public PersoniumEngineException(final String msg, final int code, final Exception e) {
        super(msg);
        this.statusCode = code;
        this.originalException = e;
    }

    /**
     * Constructor.
     * @param msg Message
     * @param code Status Code
     */
    public PersoniumEngineException(final String msg, final int code) {
        super(msg);
        this.statusCode = code;
        this.originalException = null;
    }

    /**
     * Getter for status code.
     * @return status code
     */
    public final int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Getter for original exception object.
     * @return original exception object
     */
    public final Exception getOriginalException() {
        return this.originalException;
    }
}
