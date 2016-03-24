package com.oroplatform.idea.oroplatform.schema;

public class Array implements Element {
    private final Element type;

    public Array(Element type) {
        this.type = type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitArray(this);
    }

    public Element getType() {
        return type;
    }
}
