package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitSequence(Sequence sequence);
    void visitContainer(Container container);
    void visitOneOf(OneOf oneOf);
    void visitScalarAnyValue(Scalar.Any any);
    void visitScalarChoicesValue(Scalar.Choices choices);
    void visitScalarRegexpValue(Scalar.Regexp regexp);
    void visitScalarPropertiesFromPathValue(Scalar.PropertiesFromPath propertiesFromPath);
    void visitScalarConditionValue(Scalar.Condition condition);
    void visitScalarActionValue(Scalar.Action action);
    void visitRepeatAtAnyLevel(Repeated repeated);
    void visitScalarDatagridValue(Scalar.Datagrid datagrid);
    void visitScalarFormTypeValue(Scalar.FormType formType);
    void visitScalarReferenceValue(Scalar.Reference reference);
}
