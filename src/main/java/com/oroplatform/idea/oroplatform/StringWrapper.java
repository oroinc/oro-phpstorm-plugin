package com.oroplatform.idea.oroplatform;

import com.intellij.openapi.util.text.StringUtil;

import java.util.function.Function;

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
        return prefix + StringUtil.trimEnd(s, suffix);
    }

    public String removePrefixAndAddSuffix(String s) {
        return StringUtil.trimStart(s, prefix) + suffix;
    }

    public StringWrapper mapPrefix(Function<String, String> f) {
        return new StringWrapper(f.apply(prefix), suffix);
    }

    public StringWrapper mapSuffix(Function<String, String> f) {
        return new StringWrapper(prefix, f.apply(suffix));
    }
}
