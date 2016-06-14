package com.oroplatform.idea.oroplatform.schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

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

    static Scalar strictChoices(String... choices) {
        return new Scalar(new Scalar.Choices(Arrays.asList(choices)));
    }

    static Scalar choices(String... choices) {
        return new Scalar(new Scalar.Choices(Arrays.asList(choices)).allowExtraChoices());
    }

    static Scalar phpMethod(String pattern) {
        return new Scalar(new Scalar.PhpMethod(pattern));
    }

    static Scalar regexp(String pattern) {
        return new Scalar(new Scalar.Regexp(Pattern.compile(pattern)));
    }

    final static Scalar condition = new Scalar(new Condition());

    final static Scalar action = new Scalar(new Action());

    final static Scalar file = new Scalar(new Scalar.File());

    final static Scalar any = new Scalar();

    final static Scalar fullEntity = new Scalar(Scalar.PhpClass.entity(false));

    final static Scalar entity = new Scalar(Scalar.PhpClass.entity(true));

    final static Scalar controller = new Scalar(Scalar.PhpClass.controller());

    final static Scalar phpClass = new Scalar(Scalar.PhpClass.any());

    final static Scalar field = new Scalar(new Scalar.PhpField());

    final static Scalar bool = new Scalar(new Choices(asList("true", "false")));

    final static Scalar integer = new Scalar(new Regexp(Pattern.compile("^\\d+$")));

    interface Value {
        void accept(Visitor visitor);
    }

    static class Any implements Value {
        private Any(){}

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarAnyValue(this);
        }
    }

    public static class Regexp implements Value {
        private final Pattern pattern;

        private Regexp(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarRegexpValue(this);
        }

        public Pattern getPattern() {
            return pattern;
        }
    }

    public static class Choices implements Value {
        private final List<String> choices = new LinkedList<String>();
        private final boolean allowExtraChoices;

        private Choices(List<String> choices) {
            this(choices, false);
        }

        private Choices(List<String> choices, boolean allowExtraChoices) {
            this.choices.addAll(choices);
            this.allowExtraChoices = allowExtraChoices;
        }

        public List<String> getChoices() {
            return Collections.unmodifiableList(choices);
        }

        Choices allowExtraChoices() {
            return new Choices(choices, true);
        }

        public boolean doesAllowExtraChoices() {
            return allowExtraChoices;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarChoicesValue(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Choices choices1 = (Choices) o;

            if (allowExtraChoices != choices1.allowExtraChoices) return false;
            return choices.equals(choices1.choices);
        }

        @Override
        public int hashCode() {
            int result = choices.hashCode();
            result = 31 * result + (allowExtraChoices ? 1 : 0);
            return result;
        }
    }

    public static class PhpClass implements Value {
        private final String namespacePart;
        private final boolean allowDoctrineShortcutNotation;

        private static PhpClass controller() {
            return new PhpClass("Controller", false);
        }

        private static PhpClass entity(boolean allowDoctrineShortcutNotation) {
            return new PhpClass("Entity", allowDoctrineShortcutNotation);
        }

        private static PhpClass any() {
            return new PhpClass(null, false);
        }

        private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation) {
            this.namespacePart = namespacePart;
            this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarPhpClassValue(this);
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
        private PhpMethod(String pattern) {
            this.pattern = Pattern.compile("^"+pattern.replace("*", ".*")+"$");
        }

        public boolean matches(String name) {
            return pattern.matcher(name).matches();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarPhpMethodValue(this);
        }
    }

    public static class PhpField implements Value {

        private PhpField(){}

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarPhpFieldValue(this);
        }
    }

    public static class Condition implements Value {
        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarConditionValue(this);
        }
    }

    public static class Action implements Value {

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarActionValue(this);
        }
    }

    public static class File implements Value {
        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarFileValue(this);
        }
    }
}
