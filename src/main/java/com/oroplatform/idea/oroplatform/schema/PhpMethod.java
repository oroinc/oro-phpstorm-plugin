package com.oroplatform.idea.oroplatform.schema;

import java.util.regex.Pattern;

public class PhpMethod {
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
