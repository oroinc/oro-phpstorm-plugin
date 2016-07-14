package com.oroplatform.idea.oroplatform.schema.requirements;

import com.intellij.openapi.util.text.StringUtil;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StrictLookupsRequirement implements Requirement {
    private final Collection<String> allowedLookups;

    public StrictLookupsRequirement(Collection<String> allowedLookups) {
        this.allowedLookups = allowedLookups;
    }

    public List<String> getErrors(String text) {
        if(allowedLookups.contains(text)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(OroPlatformBundle.message("inspection.schema.notAllowedPropertyValue", text, StringUtil.join(allowedLookups, ", ")));
    }

}
