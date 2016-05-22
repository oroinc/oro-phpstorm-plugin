package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.List;

public class Property {

    private final Pattern name;
    private final Element valueElement;
    private final boolean required;
    private final Scalar keyElement;

    private Property(Pattern name, Element valueElement, boolean required, Scalar keyElement) {
        this.name = name;
        this.valueElement = valueElement;
        this.required = required;
        this.keyElement = keyElement;
    }

    private Property(Pattern name, Element valueElement, boolean required) {
        this(name, valueElement, required, Scalar.any);
    }

    public Property(String name, Element valueElement) {
        this(name, valueElement, false);
    }

    Property(String name, Element valueElement, boolean required) {
        this(new ExactlyPattern(name), valueElement, required);
    }

    Property(java.util.regex.Pattern name, Element valueElement) {
        this(new RegexpPattern(name), valueElement, false);
    }

    public Property(java.util.regex.Pattern name, Element valueElement, boolean required) {
        this(new RegexpPattern(name), valueElement, required);
    }

    public boolean nameMatches(String name) {
        return this.name.matches(name);
    }

    public List<String> nameExamples() {
        return this.name.examples();
    }

    public Element getValueElement() {
        return valueElement;
    }

    public String getName() {
        return this.name.getName();
    }

    @Override
    public String toString() {
        return "Property{" +
                "name=" + name +
                '}';
    }

    public boolean isRequired() {
        return required;
    }

    public Property required() {
        return new Property(name, valueElement, true);
    }

    public Property withKeyElement(Scalar key) {
        return new Property(name, valueElement, required, key);
    }

    public Scalar getKeyElement() {
        return keyElement;
    }

    private static class RegexpPattern implements Pattern {
        private final java.util.regex.Pattern value;

        RegexpPattern(java.util.regex.Pattern value) {
            this.value = value;
        }

        RegexpPattern(String name) {
            this(java.util.regex.Pattern.compile(name));
        }

        public boolean matches(String value) {
            return this.value.matcher(value).matches();
        }

        @Override
        public List<String> examples() {
            return Collections.emptyList();
        }

        @Override
        public String getName() {
            return value.toString();
        }

        @Override
        public String toString() {
            return "RegexpPattern{" +
                    "value=" + value +
                    '}';
        }
    }

    private static class ExactlyPattern implements Pattern {
        private final String value;

        private ExactlyPattern(String value) {
            this.value = value;
        }

        @Override
        public boolean matches(String value) {
            return this.value.equals(value);
        }

        @Override
        public List<String> examples() {
            return Collections.singletonList(value);
        }

        @Override
        public String getName() {
            return value;
        }

        @Override
        public String toString() {
            return "ExactlyPattern{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    private interface Pattern {
        boolean matches(String value);
        List<String> examples();
        String getName();
    }
}
