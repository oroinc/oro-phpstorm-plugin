package com.oroplatform.idea.oroplatform.schema;

public class Literal implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visitLiteral(this);
    }
}
