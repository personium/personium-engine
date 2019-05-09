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
package io.personium.engine.source;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mozilla.javascript.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.personium.core.model.file.DataCryptor;
import io.personium.engine.PersoniumEngineException;
import io.personium.engine.model.DavMetadataFile;
import io.personium.engine.model.ScriptCache;

/**
 * Service resource source management using file system.
 */
public class FsServiceResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(FsServiceResourceSourceManager.class);

    private String fsPath;
    /** RoutingId(CellID). */
    private String fsRoutingId;

    /** コレクションのPROPPATCH情報. */
    private String serviceCollectionInfo;

    /** Mapping from path to source file. */
    private Map<String, String> pathMap = new HashMap<>();

    private String serviceSubject;

    /**
     * コンストラクタ.
     * @param filePath 対象サービスコレクションのFile System Path.
     * @param fsRoutingId CellID having target service collection.
     * @throws PersoniumEngineException DcEngineException
     */
    public FsServiceResourceSourceManager(String filePath, String fsRoutingId) throws PersoniumEngineException {
        this.fsPath = filePath;
        this.fsRoutingId = fsRoutingId;
        log.info("Source File Path: [" + this.fsPath + "]");
        this.loadServiceCollectionInfo();
        this.parseServiceTag();
    }

    /**
     * サービスコレクションの情報を取得.
     * @throws PersoniumEngineException DcEngineException
     */
    private void loadServiceCollectionInfo() throws PersoniumEngineException {
        // filePath null check.
        if (this.fsPath == null) {
            log.info("File path is empty.");
            throw new PersoniumEngineException("404 Not Found (Request Header invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        // サービスコレクションを取得
        JSONObject json = getMetaData(this.fsPath);

        // スクリプトの情報を取得する
        this.serviceCollectionInfo = (String) ((Map<?, ?>) json.get("d")).get("service@urn:x-personium:xmlns");
        if (null == this.serviceCollectionInfo) {
            log.info("Service property Invalid ");
            throw new PersoniumEngineException("404 Not Found (Service property invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        log.debug("scriptPath: [" + this.serviceCollectionInfo + "] ");
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     */
    public String getScriptNameForServicePath(String servicePath) {
        return this.pathMap.get(servicePath);
    }

    public void createCachedScript(Script script, String sourceName, Map<String, ScriptCache> engineLibCache)
            throws PersoniumEngineException {
        String sourceDir = this.fsPath + File.separator + "__src" + File.separator + sourceName;
        DavMetadataFile metaFile = DavMetadataFile.newInstance(sourceDir);
        metaFile.load();
        ScriptCache cache = new ScriptCache(script, metaFile.getUpdated());
        engineLibCache.put(sourceDir, cache);
    }

    public Script getCachedScript(String sourceName, Map<String, ScriptCache> engineLibCache) throws PersoniumEngineException {
        String sourceDir = this.fsPath + File.separator + "__src" + File.separator + sourceName;
        DavMetadataFile metaFile = DavMetadataFile.newInstance(sourceDir);
        metaFile.load();
        if (!engineLibCache.containsKey(sourceDir)) {
            return null;
        }
        ScriptCache cache = engineLibCache.get(sourceDir);
        if (cache.isScriptFileUpdated(metaFile.getUpdated())) {
            return null;
        }
        return cache.getScript();
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws PersoniumEngineException DcEngineException
     */
    public String getSource(String sourceName) throws PersoniumEngineException {
        // 対象のスクリプトの情報を取得する
        String sourceDir = this.fsPath + File.separator + "__src" + File.separator + sourceName;
        String sourcePath = sourceDir + File.separator + "content";
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new PersoniumEngineException("404 Not Found", PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        // Determine whether the file is encrypted
        JSONObject json = getMetaData(sourceDir);
        String encryptionType = (String) json.get("et");

        try {
            String source = "";
            // Perform decryption.
            DataCryptor cryptor = new DataCryptor(this.fsRoutingId);
            try (InputStream in = cryptor.decode(new FileInputStream(sourceFile), encryptionType)) {
                source = IOUtils.toString(in, Charsets.UTF_8);
            }
            return source;
        } catch (IOException e) {
          log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
          throw new PersoniumEngineException("404 UserScript Encoding error",
                  PersoniumEngineException.STATUSCODE_NOTFOUND, e);
        }
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     */
    public String getServiceSubject() {
      return this.serviceSubject;
    }

    /**
     * サービス名からスクリプトファイルのパスを取得する.
     * @param xml XML文字列
     * @param svcName サービス名
     * @return スクリプトファイルパス
     */
    private void parseServiceTag() {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(this.serviceCollectionInfo.getBytes());
        try {
            doc = builder.parse(is);
            Element el = doc.getDocumentElement();
            this.serviceSubject = el.getAttribute("subject");
            NodeList nl = doc.getElementsByTagNameNS("*", "path");
            for (int i = 0; i < nl.getLength(); i++) {
                NamedNodeMap nnm = nl.item(i).getAttributes();
                pathMap.put(nnm.getNamedItem("name").getNodeValue(), nnm.getNamedItem("src").getNodeValue());
            }
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get .pmeta in JSON format.
     * @param metaDirPath Directory path in which meta file to be acquired is stored
     * @return .pmeta in JSON format
     * @throws PersoniumEngineException Meta file not found.
     */
    private JSONObject getMetaData(String metaDirPath) throws PersoniumEngineException {
        String separator = "";
        if (!metaDirPath.endsWith(File.separator)) {
            separator = File.separator;
        }
        File metaFile = new File(metaDirPath + separator + ".pmeta");
        JSONObject json = null;
        try (Reader reader = Files.newBufferedReader(metaFile.toPath(), Charsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            // IO failure or JSON is broken
            log.info("Meta file not found or invalid (" + this.fsPath + ")");
            throw new PersoniumEngineException("500 Server Error",
            PersoniumEngineException.STATUSCODE_SERVER_ERROR);
        }
        return json;
    }
}
