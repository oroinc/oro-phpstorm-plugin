package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Scalar implements Element {

    private final Value value;

    public Scalar() {
        this(new Any());
    }

    public Scalar(Value value) {
        this.value = value;
    }

    public Scalar(List<String> choices) {
        this(new Choices(choices));
    }

    @Override
    public void accept(Visitor visitor) {
        value.accept(visitor);
    }

    public Value getValue() {
        return value;
    }

    public interface Value {
        public void accept(Visitor visitor);
    }

    public static class Any implements Value {
        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralAnyValue(this);
        }
    }

    public static class Choices implements Value {
        private final List<String> choices = new LinkedList<String>();

        Choices(List<String> choices) {
            this.choices.addAll(choices);
        }

        public List<String> getChoices() {
            return Collections.unmodifiableList(choices);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralChoicesValue(this);
        }
    }

    public static class PhpClass implements Value {
        private final Type type;

        PhpClass(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralPhpClassValue(this);
        }

        public enum Type {
            Entity, Controller
        }
    }

    public static class PhpMethod implements Value {
        private final Pattern pattern;

        /**
         * @param pattern Simple pattern, use * for any string
         */
        PhpMethod(String pattern) {
            this.pattern = Pattern.compile("^"+pattern.replace("*", ".*")+"$");
        }

        PhpMethod() {
            this("*");
        }

        public boolean matches(String name) {
            return pattern.matcher(name).matches();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralPhpMethodValue(this);
        }
    }
}
