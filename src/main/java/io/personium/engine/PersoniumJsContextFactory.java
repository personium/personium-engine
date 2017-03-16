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
package io.personium.engine;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import io.personium.engine.accesscontrol.PersoniumClassShutterImpl;
import io.personium.engine.accesscontrol.PersoniumWrapFactory;



/**
 * javascript.ContextFactoryの派生クラス.
  */
public class PersoniumJsContextFactory extends ContextFactory {
    /** タイムアウト値. */
    private static final int TIMEOUTVALUE = 50 * 1000;
    /** setInstructionObserverThreshold で使用されていたマジックナンバー. */
    private static final int MVALUE = 10;

    @Override
    protected final Context makeContext() {
        PersoniumJsContext cx = new PersoniumJsContext();
        cx.setInstructionObserverThreshold(TIMEOUTVALUE / MVALUE);

        // ClassShutterの登録(Javaパッケージ呼び出し制御)
        cx.setClassShutter(new PersoniumClassShutterImpl());

        cx.setWrapFactory(new PersoniumWrapFactory());

        return cx;
    }

    @Override
    protected final Object doTopCall(
            final Callable callable,
            final Context cx,
            final Scriptable scope,
            final Scriptable thisObj,
            final Object[] args) {
        long curTime = System.currentTimeMillis();
        ((PersoniumJsContext) cx).setTimeout(curTime + TIMEOUTVALUE);
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }

    @Override
    protected final void observeInstructionCount(
            final Context cx,
            final int instructionCount) {
        try {
            ((PersoniumJsContext) cx).checkTimeout();
        } catch (PersoniumEngineException e) {
            throw new Error();
        }
    }
}
