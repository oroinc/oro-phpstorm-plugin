package com.oroplatform.idea.oroplatform.schema;

public abstract class VisitorAdapter implements Visitor {
    @Override
    public void visitScalarAnyValue(Scalar.Any any) {
    }

    @Override
    public void visitScalarChoicesValue(Scalar.Choices choices) {
    }

    @Override
    public void visitScalarPhpClassValue(Scalar.PhpClass phpClass) {
    }

    @Override
    public void visitScalarPhpMethodValue(Scalar.PhpMethod phpMethod) {
    }

    @Override
    public void visitScalarPhpFieldValue(Scalar.PhpField phpField) {
    }

    @Override
    public void visitScalarRegexpValue(Scalar.Regexp regexp) {
    }

    @Override
    public void visitScalarPropertiesFromPathValue(Scalar.PropertiesFromPath propertiesFromPath) {
    }

    @Override
    public void visitScalarFileValue(Scalar.File file) {
    }

    @Override
    public void visitScalarConditionValue(Scalar.Condition condition) {
    }

    @Override
    public void visitScalarActionValue(Scalar.Action action) {
    }

    @Override
    public void visitScalarPhpCallbackValue(Scalar.PhpCallback phpCallback) {
    }
}
