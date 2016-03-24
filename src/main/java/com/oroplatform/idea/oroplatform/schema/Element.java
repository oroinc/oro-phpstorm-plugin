package com.oroplatform.idea.oroplatform.schema;

public interface Element {
    void accept(Visitor visitor);
}
