/*
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
/**
 * Test for box installation
 */
function boxInstallationTest(request){
    var util = require("testCommon");

    // getting cellName from query
    var query = _p.util.queryParse(request.queryString);
    var cellName = query["cell"];

    try {
        var cell = _p.as("client").cell();
        var status = "";

        // install bar from inputstream
        var inputStream = cell.box().getStream("testBar.bar");
        var box = _p.as("client").cell(cellName).installBox("boxtest", inputStream);

        // wait
        sleep(1);

        // wait for ready
        var underInstallation = true;
        while(underInstallation) {
            status = box.getMetaData();
            parsed = JSON.parse(status);
            if (parsed.box.status === "ready") underInstallation = false;
        }

        // delete box created
        _p.as("client").cell(cellName).ctl.box.recursiveDelete("boxtest");

        // install box from URL
        var barURL = cell.getUrl() + cell.box().name + "/testBar.bar";
        box = cell.installBox("boxtest2", barURL);

        // wait
        sleep(1);

        // retrieve box created
        underInstallation = true;

        // wait for ready
        while(underInstallation) {
            status = box.getMetaData();
            parsed = JSON.parse(status);
            if (parsed.box.status === "ready") underInstallation = false;
        }
        
        // delete box created
        _p.as("client").cell(cellName).ctl.box.recursiveDelete("boxtest2");

        // create response and return
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(JSON.stringify(e)).build();
    } finally {

    }
}

function sleep( T ){
    var d1 = new Date().getTime();
    var d2 = new Date().getTime();
    while( d2 < d1+1000*T ){    // wait for T seconds
        d2=new Date().getTime();
    }
    return;
}
