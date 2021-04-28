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

import com.sun.jersey.api.uri.UriTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class PathResolverByURITemplate implements IPathResolver {

    /** Mapping from path to source file. */
    private ArrayList<RouteEntry> pathList = new ArrayList<>(); 

    private interface IRouteEntry {
        public boolean match(String uri);
        public String getSourceString();
    }

    private class RouteEntry implements IRouteEntry{
        private UriTemplate template = null;
        private String sourceString = null;
  
        public RouteEntry (String uriTemplate, String sourceString) throws PatternSyntaxException, IllegalArgumentException {
          this.template = new UriTemplate(uriTemplate);
          this.sourceString = sourceString;
        }
  
        public String getSourceString() {
          return this.sourceString;
        }
  
        public boolean match(String uri) {
          Map<String,String> matchResult = new HashMap<>();
          return this.template.match(uri, matchResult);
        }
    }

    /**
     * Function for registering route for engine script
     * @param name path of engine script
     * @param src source filename of engine script
     * @throws RouteRegistrationException when name contains illegal uri template
     */
    public void registerRoute(String name, String src) throws RouteRegistrationException {
        try {
            pathList.add(new RouteEntry(name, src));
        } catch (Exception e) {
            throw new RouteRegistrationException(e, name, src);
        }
    }

    /** 
     * Function for getting source file name from route path
     * @param servicePath path executed
     * @return source filename.
     */
    public String resolve(String servicePath) {
        for (RouteEntry routeEntry : pathList) {
            if (routeEntry.match(servicePath)) {
              return routeEntry.sourceString;
            }
        }
        return null;
    }
}
