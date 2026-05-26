package com.sop.backend.exceptions;

public class XmlTransformerException extends RuntimeException {
    public XmlTransformerException(String message) {
        super("XmlTransformer: " + message);
    }
}
