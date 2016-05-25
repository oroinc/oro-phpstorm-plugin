package com.oroplatform.idea.oroplatform.schema;

public class Sequence implements Element {
    private final Element type;

    private Sequence(Element type) {
        this.type = type;
    }

    static Sequence of(Element type) {
        return new Sequence(type);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSequence(this);
    }

    public Element getType() {
        return type;
    }
}
