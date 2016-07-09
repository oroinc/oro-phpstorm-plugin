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
    void visitScalarPropertiesFromPathValue(Scalar.PropertiesFromPath propertiesFromPath);
    void visitScalarFileValue(Scalar.File file);
    void visitScalarConditionValue(Scalar.Condition condition);
    void visitScalarActionValue(Scalar.Action action);
    void visitRepeatAtAnyLevel(Repeated repeated);
    void visitScalarPhpCallbackValue(Scalar.PhpCallback phpCallback);
}
