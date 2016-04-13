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

    public static class Regexp implements Value {
        private final Pattern pattern;

        public Regexp(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralRegexpValue(this);
        }

        public Pattern getPattern() {
            return pattern;
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
        private final String namespacePart;
        private final boolean allowDoctrineShortcutNotation;

        static PhpClass controller() {
            return new PhpClass("Controller", false);
        }

        static PhpClass entity() {
            return entity(true);
        }

        static PhpClass entity(boolean allowDoctrineShortcutNotation) {
            return new PhpClass("Entity", allowDoctrineShortcutNotation);
        }

        private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation) {
            this.namespacePart = namespacePart;
            this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralPhpClassValue(this);
        }

        public boolean allowDoctrineShortcutNotation() {
            return allowDoctrineShortcutNotation;
        }

        public String getNamespacePart() {
            return namespacePart;
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

    public static class PhpField implements Value {

        @Override
        public void accept(Visitor visitor) {
            visitor.visitLiteralPhpFieldValue(this);
        }
    }
}
