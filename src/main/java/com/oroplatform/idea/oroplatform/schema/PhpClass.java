package com.oroplatform.idea.oroplatform.schema;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PhpClass {
    private final String namespacePart;
    private final boolean allowDoctrineShortcutNotation;
    private final PropertyPath excludedClassesPath;

    static PhpClass controller() {
        return new PhpClass("Controller", false, null);
    }

    static PhpClass entity(boolean allowDoctrineShortcutNotation) {
        return new PhpClass("Entity", allowDoctrineShortcutNotation, null);
    }

    public static PhpClass any() {
        return new PhpClass(null, false, null);
    }

    private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation, @Nullable PropertyPath excludedClassesPath) {
        this.namespacePart = namespacePart;
        this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
        this.excludedClassesPath = excludedClassesPath;
    }

    public boolean allowDoctrineShortcutNotation() {
        return allowDoctrineShortcutNotation;
    }

    public String getNamespacePart() {
        return namespacePart;
    }

    public Optional<PropertyPath> getExcludedClassesPath() {
        return Optional.ofNullable(excludedClassesPath);
    }

    public PhpClass withExcludedClassesPath(PropertyPath excludedClassesPath) {
        return new PhpClass(namespacePart, allowDoctrineShortcutNotation, excludedClassesPath);
    }
}
