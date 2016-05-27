package com.oroplatform.idea.oroplatform.schema;

public class Schema {
    public final String filePathPattern;
    public final Element rootElement;

    Schema(String filePathPattern, Element rootElement) {
        this.filePathPattern = filePathPattern;
        this.rootElement = rootElement;
    }
}
