package com.oroplatform.idea.oroplatform;

import java.util.regex.Pattern;

public class SimpleSuffixMatcher {

    private final Pattern pattern;

    public SimpleSuffixMatcher(String pattern) {
        this.pattern = Pattern.compile(pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*")+"$");
    }

    public boolean matches(String s) {
        return this.pattern.matcher(s).find();
    }
}
