package com.oroplatform.idea.oroplatform.schema.requirements;

import com.intellij.openapi.util.text.StringUtil;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChoicesRequirement implements Requirement {
    private final Collection<String> allowedChoices;

    public ChoicesRequirement(Collection<String> allowedChoices) {
        this.allowedChoices = allowedChoices;
    }

    public List<String> getErrors(String text) {
        if(allowedChoices.contains(text)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(OroPlatformBundle.message("inspection.schema.notAllowedPropertyValue", text, StringUtil.join(allowedChoices, ", ")));
    }

}
