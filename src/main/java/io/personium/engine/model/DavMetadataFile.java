/**
 * Personium
 * Copyright 2019 FUJITSU LIMITED
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
package io.personium.engine.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

import org.apache.commons.io.Charsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.personium.engine.PersoniumEngineException;

/**
 * a class for handling internal fs file storing Dav metadata.
 */
public class DavMetadataFile {
    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(DavMetadataFile.class);

    /** Metadata file name. */
    //TODO filename can be used on Unix, Windows, but it is nice to use on DAV.
    public static final String DAV_META_FILE_NAME = ".pmeta";

    /** Milliseconds to wait of metafile reading retries. */
    private static final long META_LOAD_RETRY_WAIT = 100L;
    /** Maximum number of metafile reading retries. */
    private static final int META_LOAD_RETRY_MAX = 5;

    File file;

    JSONObject json = new JSONObject();

    /** JSON Key for ID. */
    private static final String KEY_ID = "i";

    /** JSON Key for Node Type. */
    private static final String KEY_NODE_TYPE = "t";

    /** JSON Key for ACL. */
    private static final String KEY_ACL = "a";

    /** JSON Key for PROPS JSON key to save.*/
    private static final String KEY_PROPS = "d";

    /** JSON Key for published. */
    private static final String KEY_PUBLISHED = "p";

    /** JSON Key for updated. */
    private static final String KEY_UPDATED = "u";

    /** JSON Key for ContentType. */
    private static final String KEY_CONTENT_TYPE = "ct";

    /** JSON Key for Content Length. */
    private static final String KEY_CONTENT_LENGTH = "cl";

    /** JSON Key for Encryption Type. */
    private static final String KEY_ENCRYPTION_TYPE = "et";

    /** JSON Key for Version. */
    private static final String KEY_VERSION = "v";

    /** JSON Key for Cell Status. */
    private static final String KEY_CELL_STATUS = "cs";

    /**
     * constructor.
     */
    private DavMetadataFile(File file) {
        this.file = file;
    }

    /**
     * Factory method.
     * @param file File
     * @return DavMetadataFile
     */
    public static DavMetadataFile newInstance(File file) {
        DavMetadataFile meta = new DavMetadataFile(file);
        return meta;
    }

    /**
     * factory method.
     * @param fsPath dav file path
     * @return DavMetadataFile
     */
    public static DavMetadataFile newInstance(String fsPath) {
        String path = fsPath + File.separator + DAV_META_FILE_NAME;
        return newInstance(new File(path));
    }

    /**
     * @return true if the file exists.
     */
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * load from the file.
     * @throws PersoniumEngineException exception
     */
    public void load() throws PersoniumEngineException {
        // Coping with core issue #28.
        // When an Exception occurs Retry several times.
        int retryCount = 0;
        while (true) {
            try {
                doLoad();
                break;
            } catch (Exception e) {
                if (retryCount < META_LOAD_RETRY_MAX) {
                    try {
                        Thread.sleep(META_LOAD_RETRY_WAIT);
                    } catch (InterruptedException ie) {
                        // If sleep fails, Error
                        throw new RuntimeException(ie);
                    }
                    retryCount++;
                    log.info("Meta file load retry. RetryCount:" + retryCount);
                } else {
                    // IO failure or JSON is broken
                    throw e;
                }
            }
        }
    }

    /**
     * load from the file.
     */
    private void doLoad() throws PersoniumEngineException {
        try (Reader reader = Files.newBufferedReader(file.toPath(), Charsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            this.json = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            throw new PersoniumEngineException("Error occured while loading metafile.",
                    PersoniumEngineException.STATUSCODE_SERVER_ERROR, e);
        }
    }

    /**
     * returns JSONObject representation of the file content.
     * @return JSONObject
     */
    public JSONObject getJSON() {
        return this.json;
    }

    /**
     * Returns the content of metadata as JSONString.
     * @return JSONString
     */
    public String toJSONString() {
        return json.toJSONString();
    }

    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return (String) this.json.get(KEY_ID);
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return (String) this.json.get(KEY_NODE_TYPE);
    }

    /**
     * @return the acl
     */
    public JSONObject getAcl() {
        return (JSONObject) this.json.get(KEY_ACL);
    }

    /**
     * Get property.
     * @param key Property key
     * @return property value
     */
    public String getProperty(String key) {
        JSONObject props = (JSONObject) this.json.get(KEY_PROPS);
        return (String) props.get(key);
    }

    /**
     * @return the properties
     */
    public JSONObject getProperties() {
        return (JSONObject) this.json.get(KEY_PROPS);
    }

    /**
     * @return the published
     */
    public Long getPublished() {
        return (Long) this.json.get(KEY_PUBLISHED);
    }

    /**
     * @return the updated
     */
    public Long getUpdated() {
        return (Long) this.json.get(KEY_UPDATED);
    }

    /**
     * @return content type string
     */
    public String getContentType() {
        return (String) this.json.get(KEY_CONTENT_TYPE);
    }

    /**
     * @return long value of content length.
     */
    public long getContentLength() {
        return (Long) this.json.get(KEY_CONTENT_LENGTH);
    }

    /**
     * @return encryption type string.
     */
    public String getEncryptionType() {
        return (String) this.json.get(KEY_ENCRYPTION_TYPE);
    }

    /**
     * @return cell status string.
     */
    public String getCellStatus() {
        return (String) this.json.get(KEY_CELL_STATUS);
    }

    /**
     * @return long value of the resource version
     */
    public Long getVersion() {
        return (Long) this.json.get(KEY_VERSION);
    }

}
