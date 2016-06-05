package com.oroplatform.idea.oroplatform.schema;

public class Schema {
    public final FileMatcher fileMatcher;
    public final Element rootElement;

    Schema(FileMatcher matcher, Element rootElement) {
        this.fileMatcher = matcher;
        this.rootElement = rootElement;
    }
}
