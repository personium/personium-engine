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

import io.personium.common.file.DataCryptor;
import io.personium.engine.PersoniumEngineException;
import io.personium.engine.model.DavMetadataFile;
import io.personium.engine.model.ScriptCache;

/**
 * Service resource source management using file system.
 */
public class FsServiceResourceSourceManager implements ISourceManager {
    /** Logger Object . */
    private static Logger log = LoggerFactory.getLogger(FsServiceResourceSourceManager.class);

    private String fsPath;
    /** RoutingId(CellID). */
    private String fsRoutingId;

    /** Collection's PROPPATCH Info. */
    private String serviceCollectionInfo;

    /** Filename Resolver from path to source file. */
    private IFilenameResolver srcResolver = null;

    private String serviceSubject;

    /**
     * Constructor.
     * @param filePath File System Path of the target ESC (Engine Service Collection).
     * @param fsRoutingId Cell ID of target service collection.
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
     * load the ServiceCollection Info.
     * @throws PersoniumEngineException DcEngineException
     */
    private void loadServiceCollectionInfo() throws PersoniumEngineException {
        // filePath null check.
        if (this.fsPath == null) {
            log.info("File path is empty.");
            throw new PersoniumEngineException("404 Not Found (Request Header invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        // Get the Service Collection Metadata
        JSONObject json = getMetaData(this.fsPath);

        // Get the Engine Service Configuration information
        this.serviceCollectionInfo = (String) ((Map<?, ?>) json.get("d")).get("service@urn:x-personium:xmlns");
        if (null == this.serviceCollectionInfo) {
            log.info("Service property Invalid ");
            throw new PersoniumEngineException("404 Not Found (Service property invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        log.debug("scriptPath: [" + this.serviceCollectionInfo + "] ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScriptNameForServicePath(String servicePath) {
        if (this.srcResolver != null) {
            return this.srcResolver.resolve(servicePath);
        }
        throw new RuntimeException("Route is not registered");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createCachedScript(Script script, String sourceName, Map<String, ScriptCache> userScriptCache)
            throws PersoniumEngineException {
        String sourceDir = this.fsPath + File.separator + "__src" + File.separator + sourceName;
        DavMetadataFile metaFile = DavMetadataFile.newInstance(sourceDir);
        metaFile.load();
        ScriptCache cache = new ScriptCache(script, metaFile.getUpdated());
        userScriptCache.put(sourceDir, cache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script getCachedScript(String sourceName, Map<String, ScriptCache> userScriptCache)
            throws PersoniumEngineException {
        String sourceDir = this.fsPath + File.separator + "__src" + File.separator + sourceName;
        DavMetadataFile metaFile = DavMetadataFile.newInstance(sourceDir);
        metaFile.load();
        if (!userScriptCache.containsKey(sourceDir)) {
            return null;
        }
        ScriptCache cache = userScriptCache.get(sourceDir);
        if (cache.isScriptFileUpdated(metaFile.getUpdated())) {
            return null;
        }
        return cache.getScript();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSource(String sourceName) throws PersoniumEngineException {
        // Get Target script information 
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
     * {@inheritDoc}
     */
    @Override
    public String getServiceSubject() {
      return this.serviceSubject;
    }

    /*
     * Parse
     *    this.serviceCollectionInfo 
     * and store it to 
     *    this.pathMap
     *    this.serviceSubject.
     */
    private void parseServiceTag() throws PersoniumEngineException {
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
            IFilenameResolver resolver = new FilenameResolverByRoute();
            NodeList nl = doc.getElementsByTagNameNS("*", "path");
            try {
                for (int i = 0; i < nl.getLength(); i++) {
                    NamedNodeMap nnm = nl.item(i).getAttributes();
                    resolver.registerRoute(nnm.getNamedItem("name").getNodeValue(), nnm.getNamedItem("src").getNodeValue());
                }
                this.srcResolver = resolver;
            } catch (RouteRegistrationException e) {
                throw new PersoniumEngineException(e.getMessage(), 503, e);
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
