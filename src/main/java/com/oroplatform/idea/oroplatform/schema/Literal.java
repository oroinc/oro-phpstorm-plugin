package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Literal implements Element {

    private final List<String> choices = new LinkedList<String>();

    public Literal() {
        this(Collections.<String>emptyList());
    }

    public Literal(List<String> choices) {
        this.choices.addAll(choices);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitLiteral(this);
    }

    public List<String> getChoices() {
        return Collections.unmodifiableList(choices);
    }

}
