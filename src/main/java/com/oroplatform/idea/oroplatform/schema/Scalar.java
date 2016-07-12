package com.oroplatform.idea.oroplatform.schema;

import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.*;

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

    static Scalar propertiesFromPath(PropertyPath path, String prefix) {
        return new Scalar(new Scalar.PropertiesFromPath(path, prefix));
    }

    static Scalar propertiesFromPath(PropertyPath path) {
        return new Scalar(new Scalar.PropertiesFromPath(path, ""));
    }

    static Scalar phpMethod(final String pattern) {
        return new Scalar(new Reference(new PhpMethodReferenceProviders(pattern)));
    }

    static Scalar regexp(String pattern) {
        return new Scalar(new Scalar.Regexp(Pattern.compile(pattern)));
    }

    final static Scalar condition = new Scalar(new Condition());

    final static Scalar action = new Scalar(new Action());

    final static Scalar formType = new Scalar(new FormType());

    final static Scalar datagrid = new Scalar(new Datagrid());

    final static Scalar file = new Scalar(new Reference(new FilePathReferenceProviders()));

    final static Scalar any = new Scalar();

    final static Scalar fullEntity = phpClass(Scalar.PhpClass.entity(false));

    final static Scalar entity = phpClass(Scalar.PhpClass.entity(true));

    final static Scalar controller = phpClass(Scalar.PhpClass.controller());

    final static Scalar phpClass = phpClass(Scalar.PhpClass.any());

    private static Scalar phpClass(final Scalar.PhpClass phpClass) {
        return new Scalar(new Reference(new PhpClassReferenceProviders(phpClass)));
    }

    final static Scalar phpCallback = new Scalar(new Reference(new PhpCallbackReferenceProviders()));

    final static Scalar field = new Scalar(new Reference(new PhpFieldReferenceProviders()));

    final static Scalar bool = new Scalar(new Choices(asList("true", "false")));

    final static Scalar integer = new Scalar(new Regexp(Pattern.compile("^\\d+$")));

    public interface Value {
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

    public static class PhpClass {
        private final String namespacePart;
        private final boolean allowDoctrineShortcutNotation;

        private static PhpClass controller() {
            return new PhpClass("Controller", false);
        }

        private static PhpClass entity(boolean allowDoctrineShortcutNotation) {
            return new PhpClass("Entity", allowDoctrineShortcutNotation);
        }

        public static PhpClass any() {
            return new PhpClass(null, false);
        }

        private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation) {
            this.namespacePart = namespacePart;
            this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
        }

        public boolean allowDoctrineShortcutNotation() {
            return allowDoctrineShortcutNotation;
        }

        public String getNamespacePart() {
            return namespacePart;
        }
    }

    public static class PhpMethod {
        private final Pattern pattern;

        /**
         * @param pattern Simple pattern, use * for any string
         */
        public PhpMethod(String pattern) {
            this.pattern = Pattern.compile("^"+pattern.replace("*", ".*")+"$");
        }

        public boolean matches(String name) {
            return pattern.matcher(name).matches();
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

    public static class FormType implements Value {

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarFormTypeValue(this);
        }
    }

    public static class Reference implements Value {

        private final ReferenceProviders referenceProviders;

        public Reference(ReferenceProviders referenceProviders) {
            this.referenceProviders = referenceProviders;
        }

        public ReferenceProviders getReferenceProviders() {
            return referenceProviders;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarReferenceValue(this);
        }
    }

    public static class Datagrid implements Value {

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarDatagridValue(this);
        }
    }

    public static class PropertiesFromPath implements Value {
        private final PropertyPath path;
        private final String prefix;

        public PropertiesFromPath(PropertyPath path, String prefix) {
            this.path = path;
            this.prefix = prefix;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitScalarPropertiesFromPathValue(this);
        }

        public PropertyPath getPath() {
            return path;
        }

        public String getPrefix() {
            return prefix;
        }
    }

}
