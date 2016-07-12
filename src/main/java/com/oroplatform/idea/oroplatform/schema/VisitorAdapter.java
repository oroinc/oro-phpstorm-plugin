package com.oroplatform.idea.oroplatform.schema;

public abstract class VisitorAdapter implements Visitor {
    @Override
    public void visitScalarAnyValue(Scalar.Any any) {
    }

    @Override
    public void visitScalarChoicesValue(Scalar.Choices choices) {
    }

    @Override
    public void visitScalarRegexpValue(Scalar.Regexp regexp) {
    }

    @Override
    public void visitScalarPropertiesFromPathValue(Scalar.PropertiesFromPath propertiesFromPath) {
    }

    @Override
    public void visitScalarConditionValue(Scalar.Condition condition) {
    }

    @Override
    public void visitScalarActionValue(Scalar.Action action) {
    }

    @Override
    public void visitScalarDatagridValue(Scalar.Datagrid datagrid) {
    }

    @Override
    public void visitScalarFormTypeValue(Scalar.FormType formType) {
    }

    @Override
    public void visitScalarReferenceValue(Scalar.Reference reference) {
    }
}
