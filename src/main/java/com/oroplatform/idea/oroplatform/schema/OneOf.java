package com.oroplatform.idea.oroplatform.schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OneOf implements Element {

    private final List<Element> elements = new LinkedList<>();

    private OneOf(List<Element> elements) {
        this.elements.addAll(elements);
    }

    OneOf(Element... elements) {
        this(Arrays.asList(elements));
    }

    static OneOf from(Element... elements) {
        return new OneOf(elements);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitOneOf(this);
    }

    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
