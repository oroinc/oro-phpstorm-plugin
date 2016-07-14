package com.oroplatform.idea.oroplatform.schema;

public abstract class VisitorAdapter implements Visitor {
    @Override
    public void visitScalarAnyValue(Scalar.Any any) {
    }

    @Override
    public void visitScalarReferenceValue(Scalar.Reference reference) {
    }

    @Override
    public void visitScalarLookupValue(Scalar.Lookup lookup) {
    }
}
