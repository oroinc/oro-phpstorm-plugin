package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.List;

public class Property {

    private final Pattern name;
    private final Element valueElement;

    private Property(Pattern name, Element valueElement) {
        this.name = name;
        this.valueElement = valueElement;
    }

    public Property(String name, Element valueElement) {
        this(new ExactlyPattern(name), valueElement);
    }

    public Property(java.util.regex.Pattern name, Element valueElement) {
        this(new RegexpPattern(name), valueElement);
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

    @Override
    public String toString() {
        return "Property{" +
                "name=" + name +
                '}';
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
            return value.matches(value);
        }

        @Override
        public List<String> examples() {
            return Collections.emptyList();
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
        public String toString() {
            return "ExactlyPattern{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    private interface Pattern {
        boolean matches(String value);
        List<String> examples();
    }
}
