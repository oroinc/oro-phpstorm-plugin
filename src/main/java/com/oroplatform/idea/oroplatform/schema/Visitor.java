package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitSequence(Sequence sequence);
    void visitContainer(Container container);
    void visitOneOf(OneOf oneOf);
    void visitScalarAnyValue(Scalar.Any any);
    void visitScalarRegexpValue(Scalar.Regexp regexp);
    void visitRepeatAtAnyLevel(Repeated repeated);
    void visitScalarReferenceValue(Scalar.Reference reference);
    void visitScalarLookupValue(Scalar.Lookup lookup);
}
