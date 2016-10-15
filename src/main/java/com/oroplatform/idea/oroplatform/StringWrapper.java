package com.oroplatform.idea.oroplatform;

public class StringWrapper {
    private final String prefix;
    private final String suffix;

    public StringWrapper(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public boolean startWith(String s) {
        return s.startsWith(prefix);
    }

    public String addPrefixAndRemoveSuffix(String s) {
        return prefix + s.replace(suffix, "");
    }

    public String removePrefixAndAddSuffix(String s) {
        return s.replace(prefix, "") + suffix;
    }
}
