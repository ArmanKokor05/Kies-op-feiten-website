package com.sop.backend.xml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlToJsonParser {

    private final XmlMapper xmlMapper = new XmlMapper();

    public List<JsonNode> parseMultipleFilesToJson(MultipartFile[] data) throws IOException {
        List<JsonNode> jsonNodes = new ArrayList<>();

        for (MultipartFile file : data) {
            jsonNodes.add(parseXmlToJson(file));
        }

        return jsonNodes;
    }

    public JsonNode parseXmlToJson(MultipartFile file) throws IOException {
        return xmlMapper.readValue(file.getBytes(), JsonNode.class);
    }
}