package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitSequence(Sequence sequence);
    void visitContainer(Container container);
    void visitOneOf(OneOf oneOf);
    void visitScalarAnyValue(Scalar.Any any);
    void visitScalarChoicesValue(Scalar.Choices choices);
    void visitScalarPhpClassValue(Scalar.PhpClass phpClass);
    void visitScalarPhpMethodValue(Scalar.PhpMethod phpMethod);
    void visitScalarPhpFieldValue(Scalar.PhpField phpField);
    void visitScalarRegexpValue(Scalar.Regexp regexp);
    void visitScalarFileValue(Scalar.File file);
}
