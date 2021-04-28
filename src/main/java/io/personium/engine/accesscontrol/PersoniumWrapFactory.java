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

import java.io.InputStream;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

import io.personium.engine.adapter.PersoniumRequestBodyStream;
import io.personium.engine.wrapper.PersoniumJSONObject;
import io.personium.engine.extension.wrapper.PersoniumInputStream;

/**
 * Engine's customized WrapFactory of RHINO.
 *  This is used when JavaScript receives return values
 *  from java method. The returned value from java is 
 *  wrapped in accordance with this class and then received
 *  by JavaScript layer.
 *  Configured at PersoniumJsContextFactory#makeContext(): Context
 */
public class PersoniumWrapFactory extends WrapFactory {

    /** 
     * Perform the following conversions.
     * When Number is passed, by not wrapping, let Rhino convert it to a JavaScript number.
     * When String is passed, by not wrapping, let Rhino convert it to a JavaScript string.
     * When InputStream is passed, wrap it with PersoniumInputStream.
     * When JSONObject is passed, wrap it with PersoniumJSONObject.
     *  (client-java sometimes returns JSONObject, so we need this.）
     * When ArrayList is passed, convert it to JavaScript NativeArray.
     *   (client-java sometimes returns ArrayList, so we need this.）
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (obj instanceof Number) {
            return obj;

        } else if (obj instanceof String) {
            return obj;

        } else if (obj instanceof InputStream && !(obj instanceof PersoniumInputStream)) {
            return new PersoniumInputStream((InputStream) obj);

        } else if (obj instanceof JSONObject && !(obj instanceof PersoniumJSONObject)) {
            return ((JSONObject) obj).toJSONString();

        } else if (obj instanceof ArrayList) {
            // If client-java returns ArrayList (e.g. when handling ACL), 
            // replace it with NativeArray and not disclose Java array to JavaScript layer.
            ArrayList<Object> list = (ArrayList<Object>) obj;
            NativeArray na = new NativeArray(list.size());
            for (int i = 0; i < list.size(); i++) {
                na.put(i, na, list.get(i));
            }
            na.setPrototype(scope);
            return na;
        }
        return super.wrap(cx, scope, obj, staticType);
    }

    @Override
    public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
        // Invalidate the constructor calls
        // even when the java class is open for access from JavaScript
        if (obj.getClass() == PersoniumInputStream.class
                || obj.getClass() == PersoniumJSONObject.class
                || obj.getClass() == PersoniumRequestBodyStream.class) {
            throw new EvaluatorException("not found");
        }
        return super.wrapNewObject(cx, scope, obj);
    }
}
