package com.oroplatform.idea.oroplatform.schema;

public class Repeated implements Element {

    private final Element element;

    private Repeated(Element element) {
        this.element = element;
    }

    public static Repeated atAnyLevel(Element element) {
        return new Repeated(element);
    }

    public Element getElement() {
        return element;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitRepeatAtAnyLevel(this);
    }
}
