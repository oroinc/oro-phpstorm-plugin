package com.oroplatform.idea.oroplatform.schema;

public interface Visitor {
    void visitSequence(Sequence sequence);
    void visitContainer(Container container);
    void visitOneOf(OneOf oneOf);
    void visitRepeatAtAnyLevel(Repeated repeated);
    void visitScalar(Scalar scalar);
}
