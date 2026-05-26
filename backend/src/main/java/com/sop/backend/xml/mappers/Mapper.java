package com.sop.backend.xml.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.sop.backend.exceptions.XmlTransformerException;
import com.sop.backend.models.BaseEntity;
import com.sop.backend.xml.XmlElements;
import com.sop.backend.xml.XmlToJsonParser;
import org.springframework.stereotype.Component;

@Component
public abstract class Mapper <T extends BaseEntity> {

    protected final XmlToJsonParser xmlToJsonParser;

    public Mapper(XmlToJsonParser xmlToJsonParser) {
        this.xmlToJsonParser = xmlToJsonParser;
    }

    /**
     * In the XML data there is a lot of nested elements.
     * To get an element you have to get it by going through all elements where the element you want is nested in.
     * @param jsonNode    The already parsed XML data.
     * @param xmlElements Array of {@link XmlElements} where the last element should be the element that you want to get.
     *                    The other elements should be the elements where the last one is nested in the correct order (from parent to child).
     * @return The JsonNode of the last element in the array. Remember that it can give back an array node.
     */
    public JsonNode getJsonNode(JsonNode jsonNode, XmlElements[] xmlElements) throws XmlTransformerException {
        JsonNode currentNode = jsonNode;

        for (int i = 0; i < xmlElements.length; i++) {
            String elementValue = xmlElements[i].getValue();
            currentNode = currentNode.path(elementValue);

            if (i < xmlElements.length - 1 && currentNode.isArray()) {
                throw new XmlTransformerException("Element " + elementValue + " is an array node. Only the last element can be an array node.");
            }

            if (currentNode.isMissingNode()) {
                throw new XmlTransformerException("Element " + elementValue + " not found");
            }
        }

        return currentNode;
    }

    public String getNodeValue(JsonNode jsonNode) throws XmlTransformerException {

        if (jsonNode.isMissingNode()) {
            throw new XmlTransformerException("Json node (value) not found");
        }

        if (jsonNode.isValueNode()) {
            return jsonNode.asText();
        }

        JsonNode textNode = jsonNode.get("");
        if (textNode != null && textNode.isValueNode()) {
            return textNode.asText();
        }

        throw new XmlTransformerException("Json node has no text value. Node was:\n" + jsonNode.toPrettyString());
    }

    public String getNodeId (JsonNode jsonNode) throws XmlTransformerException {
        if (jsonNode.isMissingNode()) {
            throw new XmlTransformerException("Json node (id) not found");
        }

        if (jsonNode.isArray()) {
            throw new XmlTransformerException("Json node is an array");
        }

        if (jsonNode.get("Id").asText().isEmpty()) {
            throw new XmlTransformerException("Json node id not found");
        }

        return jsonNode.get("Id").asText();
    }

    public int getNodeIdAsInt (JsonNode jsonNode) throws XmlTransformerException {
        if (jsonNode.isMissingNode()) {
            throw new XmlTransformerException("Json node (id) not found");
        }

        if (jsonNode.isArray()) {
            throw new XmlTransformerException("Json node is an array");
        }

        if (jsonNode.get("Id").asText().isEmpty()) {
            throw new XmlTransformerException("Json node id not found");
        }

        return jsonNode.get("Id").asInt();
    }

    protected abstract T populateModel(JsonNode jsonNode);
}