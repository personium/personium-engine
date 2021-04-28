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
package io.personium.engine.source;

/**
 * Exception class for route registration
 */
@SuppressWarnings("serial")
public class RouteRegistrationException extends Exception {

    /* path name of route */
    private String name = null;

    /* source filename of route */
    private String src = null;

    /**
     * Constructor of RouteRegistrationException
     * @param cause cause of exception
     * @param name name of route
     * @param src source filename of route 
     */
    public RouteRegistrationException (Throwable cause, final String name, final String src) {
        super(cause);
        this.name = name;
        this.src = src;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Route registration failed. (name=%s, src=%s)", this.name, this.src);
    }
}
