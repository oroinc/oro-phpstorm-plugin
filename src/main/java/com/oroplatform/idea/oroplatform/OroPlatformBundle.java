package com.oroplatform.idea.oroplatform;

import com.intellij.AbstractBundle;
import com.intellij.CommonBundle;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class OroPlatformBundle {
    private static final String BUNDLE_NAME = "com.oroplatform.idea.oroplatform.messages.OroPlatformBundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return AbstractBundle.message(BUNDLE, key, params);
    }
}