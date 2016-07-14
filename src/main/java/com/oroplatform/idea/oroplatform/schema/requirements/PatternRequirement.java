package com.oroplatform.idea.oroplatform.schema.requirements;

import com.oroplatform.idea.oroplatform.OroPlatformBundle;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class PatternRequirement implements Requirement {
    private final Pattern pattern;

    public PatternRequirement(Pattern pattern) {
        this.pattern = pattern;
    }


    @Override
    public List<String> getErrors(String text) {
        if(pattern.matcher(text).matches()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(OroPlatformBundle.message("inspection.schema.valueDoesNotMatchPattern", text, pattern.pattern()));
    }
}
