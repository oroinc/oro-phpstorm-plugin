package com.oroplatform.idea.oroplatform.schema;

import com.jetbrains.php.lang.psi.elements.Method;

import java.util.regex.Pattern;

public class PhpMethod {
    private final PhpMethodMatcher matcher;

    /**
     * @param pattern Simple pattern, use * for any string
     */
    public PhpMethod(String pattern) {
        this(new PhpMethodPatternMatcher(Pattern.compile("^"+pattern.replace("*", ".*")+"$")));
    }

    public PhpMethod(PhpMethodMatcher matcher) {
        this.matcher = matcher;
    }

    public boolean matches(Method method) {
        return matcher.matches(method);
    }

    public interface PhpMethodMatcher {
        boolean matches(Method method);
    }

    private static class PhpMethodPatternMatcher implements PhpMethodMatcher {
        private final Pattern pattern;

        public PhpMethodPatternMatcher(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(Method method) {
            return pattern.matcher(method.getName()).matches();
        }
    }

    public static class PhpMethodStaticMatcher implements PhpMethodMatcher {
        @Override
        public boolean matches(Method method) {
            return method.isStatic();
        }
    }

    public static class PhpMethodNonStaticMatcher implements PhpMethodMatcher {
        @Override
        public boolean matches(Method method) {
            return !method.isStatic();
        }
    }
}
