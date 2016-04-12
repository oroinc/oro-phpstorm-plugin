package com.oroplatform.idea.oroplatform.schema;

public abstract class VisitorAdapter implements Visitor {
    @Override
    public void visitLiteralAnyValue(Scalar.Any any) {
    }

    @Override
    public void visitLiteralChoicesValue(Scalar.Choices choices) {
    }

    @Override
    public void visitLiteralPhpClassValue(Scalar.PhpClass phpClass) {
    }

    @Override
    public void visitLiteralPhpMethodValue(Scalar.PhpMethod phpMethod) {
    }

    @Override
    public void visitLiteralPhpFieldValue(Scalar.PhpField phpField) {
    }

    @Override
    public void visitLiteralRegexpValue(Scalar.Regexp regexp) {
    }
}
