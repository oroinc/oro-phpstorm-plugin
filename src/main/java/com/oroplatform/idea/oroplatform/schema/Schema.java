package com.oroplatform.idea.oroplatform.schema;

public class Schema {
    public final String fileName;
    public final Element rootElement;

    Schema(String fileName, Element rootElement) {
        this.fileName = fileName;
        this.rootElement = rootElement;
    }
}
