package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitArray(Array array);
    void visitContainer(Container container);
    void visitLiteral(Literal literal);
}
