package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.List;

public class Property {

    private final Pattern name;
    private final Element valueElement;
    private final boolean required;
    private final boolean deprecated;
    private final Scalar keyElement;

    private Property(Pattern name, Element valueElement, boolean required, Scalar keyElement, boolean deprecated) {
        this.name = name;
        this.valueElement = valueElement;
        this.required = required;
        this.keyElement = keyElement;
        this.deprecated = deprecated;
    }

    private Property(Pattern name, Element valueElement, boolean required) {
        this(name, valueElement, required, Scalars.any, false);
    }

    Property(String name, Element valueElement) {
        this(name, valueElement, false);
    }

    private Property(String name, Element valueElement, boolean required) {
        this(new ExactlyPattern(name), valueElement, required);
    }

    private Property(java.util.regex.Pattern name, Element valueElement) {
        this(new RegexpPattern(name), valueElement, false);
    }

    static Property named(String name, Element valueElement) {
        return new Property(name, valueElement);
    }

    static Property any(Element valueElement) {
        return new Property(java.util.regex.Pattern.compile(".*"), valueElement);
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

    Property required() {
        return new Property(name, valueElement, true);
    }

    Property withKeyElement(Scalar key) {
        return new Property(name, valueElement, required, key, deprecated);
    }

    Property deprecated() {
        return new Property(name, valueElement, required, keyElement, true);
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public Scalar getKeyElement() {
        return keyElement;
    }

    private static class RegexpPattern implements Pattern {
        private final java.util.regex.Pattern value;

        RegexpPattern(java.util.regex.Pattern value) {
            this.value = value;
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
