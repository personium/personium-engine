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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.personium.common.es.EsType;
import io.personium.common.es.response.PersoniumGetResponse;
import io.personium.core.model.file.BinaryDataAccessException;
import io.personium.core.model.file.BinaryDataAccessor;
import io.personium.engine.PersoniumEngineException;
import io.personium.engine.EsModel;
import io.personium.engine.utils.PersoniumEngineConfig;

/**
 * サービスコレクションの情報からソースの情報を管理する.
 */
public class EsServiceResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(EsServiceResourceSourceManager.class);

    /** ESインデックス. */
    private String index;

    /** ESタイプ. */
    private String type;

    /** ESID. */
    private String id;

    /** ESRoutingID. */
    private String routingId;

    /** ESアクセッサtype. */
    private EsType typ;

    /** ODataコレクションのPROPPATCH情報. */
    private String serviceCollectionInfo;

    /** ESから取得したODataコレクションの配下のソース情報. */
    private Map<?, ?> sourceInfo;

    /**
     * コンストラクタ.
     * @param index 対象サービスコレクションのESのインデックス
     * @param type 対象サービスコレクションのESのタイプ
     * @param id 対象サービスコレクションのESのID
     * @param routingId 対象サービスコレクションのESのルーティングID
     */
    public EsServiceResourceSourceManager(String index, String type, String id, String routingId) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.routingId = routingId;
        log.info("ElasticSearch index: [" + this.index + "] type: [" + this.type + "] "
                + "id: [" + this.id + "] routingId :[" + this.id + "]");
        this.typ = EsModel.type(this.index, this.type, this.routingId, 0, 0);
    }

    /**
     * サービスコレクションの情報を取得.
     * @throws PersoniumEngineException DcEngineException
     */
    private void loadServiceCollectionInfo() throws PersoniumEngineException {
        if (this.sourceInfo != null) {
            return;
        }
        // elasticsearchからPROPを取得する
        // Type 名に # は使えないっぽい。
        if (this.routingId == null) {
            log.info("Routing ID is empty.");
            throw new PersoniumEngineException("404 Not Found (Request Header invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        // サービスコレクションを取得
        PersoniumGetResponse getResp = this.typ.get(this.id);
        if (!getResp.isExists()) {
            log.info("Service Collection id not found to ElasticSearch (" + this.id + ")");
            throw new PersoniumEngineException("404 Not Found (Service Collection invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        // スクリプトの情報を取得する
        this.serviceCollectionInfo = (String) ((Map<?, ?>) getResp.getSource().get("d")).get("service@urn:x-personium:xmlns");
        if (null == this.serviceCollectionInfo) {
            log.info("Service property Invalid ");
            throw new PersoniumEngineException("404 Not Found (Service property invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        log.debug("scriptPath: [" + this.serviceCollectionInfo + "] ");
        // childrenを取る （__src）
        String children = (String) ((Map<?, ?>) getResp.getSource().get("o")).get("__src");
        // __src の情報を取得
        getResp = this.typ.get(children);
        if (!getResp.isExists()) {
            log.info("Service Source Colleciton(__src) not found (" + children + ")");
            throw new PersoniumEngineException("404 Not Found (Service Source Collection invalid) ",
                    PersoniumEngineException.STATUSCODE_NOTFOUND);
        }
        this.sourceInfo = (Map<?, ?>) getResp.getSource().get("o");
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     * @throws PersoniumEngineException DcEngineException
     */
    public String getServiceSubject() throws PersoniumEngineException {
        this.loadServiceCollectionInfo();
        // サービスサブジェクトの取得
        return getServiceSubject(this.serviceCollectionInfo);
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     * @throws PersoniumEngineException DcEngineException
     */
    public String getScriptNameForServicePath(String servicePath) throws PersoniumEngineException {
        this.loadServiceCollectionInfo();
        return getScriptName(this.serviceCollectionInfo, servicePath);
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws PersoniumEngineException DcEngineException
     */
    public String getSource(String sourceName) throws PersoniumEngineException {
        this.loadServiceCollectionInfo();
        // 対象のスクリプトの情報を取得する
        String sourceNodeId = (String) this.sourceInfo.get(sourceName);
        if (sourceNodeId == null) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new PersoniumEngineException("404 Not Found", PersoniumEngineException.STATUSCODE_NOTFOUND);
        }
        PersoniumGetResponse getResp = this.typ.get(sourceNodeId);
        if (!getResp.isExists()) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new PersoniumEngineException("404 Not Found", PersoniumEngineException.STATUSCODE_NOTFOUND);
        }

        BinaryDataAccessor binaryAccessor = new BinaryDataAccessor(PersoniumEngineConfig.getBlobStoreRoot(), this.index
                .substring(PersoniumEngineConfig.getUnitPrefix().length() + 1), PersoniumEngineConfig.getFsyncEnabled());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            binaryAccessor.copy(sourceNodeId, baos);
            return baos.toString("UTF-8");
        } catch (BinaryDataAccessException e) {
            log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
            throw new PersoniumEngineException("404 UserScript Encoding error", PersoniumEngineException.STATUSCODE_NOTFOUND, e);
        } catch (UnsupportedEncodingException e) {
            log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
            throw new PersoniumEngineException("404 UserScript Encoding error", PersoniumEngineException.STATUSCODE_NOTFOUND, e);
        }


    }

    /**
     * サービス名からスクリプトファイルのパスを取得する.
     * @param xml XML文字列
     * @param svcName サービス名
     * @return スクリプトファイルパス
     */
    private String getScriptName(final String xml, final String svcName) {
        String scriptName = "";
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        try {
            doc = builder.parse(is);

            NodeList nl = doc.getElementsByTagNameNS("*", "path");
            for (int i = 0; i < nl.getLength(); i++) {
                NamedNodeMap nnm = nl.item(i).getAttributes();
                if (nnm.getNamedItem("name").getNodeValue().equals(svcName)) {
                    scriptName = nnm.getNamedItem("src").getNodeValue();
                }
            }
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scriptName;
    }

    /**
     * サービス設定からサービスサブジェクトの値を取得する.
     * @param xml XML文字列
     */
    private String getServiceSubject(final String xml) {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        try {
            doc = builder.parse(is);

            Element el = doc.getDocumentElement();
            return el.getAttribute("subject");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

