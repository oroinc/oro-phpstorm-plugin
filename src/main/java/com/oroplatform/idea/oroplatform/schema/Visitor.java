package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitSequence(Sequence sequence);
    void visitContainer(Container container);
    void visitLiteralAnyValue(Scalar.Any any);
    void visitLiteralChoicesValue(Scalar.Choices choices);
    void visitLiteralPhpClassValue(Scalar.PhpClass phpClass);
    void visitLiteralPhpMethodValue(Scalar.PhpMethod phpMethod);
}
