package com.oroplatform.idea.oroplatform.schema;

public class Sequence implements Element {
    private final Element type;

    public Sequence(Element type) {
        this.type = type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSequence(this);
    }

    public Element getType() {
        return type;
    }
}
