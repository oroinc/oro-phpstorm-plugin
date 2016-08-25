package com.oroplatform.idea.oroplatform.symfony;

import com.intellij.openapi.util.text.StringUtil;

public class TwigTemplate {
    private final String name;

    public TwigTemplate(Bundle bundle, String topDirectory, String restPath) {
        this.name = bundle.getName() + ":" + topDirectory + ":" + StringUtil.trimStart(restPath, "/");
    }

    public String getName() {
        return name;
    }
}
