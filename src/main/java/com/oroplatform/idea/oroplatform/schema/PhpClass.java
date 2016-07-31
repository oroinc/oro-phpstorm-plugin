package com.oroplatform.idea.oroplatform.schema;

public class PhpClass {
    private final String namespacePart;
    private final boolean allowDoctrineShortcutNotation;

    static PhpClass controller() {
        return new PhpClass("Controller", false);
    }

    static PhpClass entity(boolean allowDoctrineShortcutNotation) {
        return new PhpClass("Entity", allowDoctrineShortcutNotation);
    }

    public static PhpClass any() {
        return new PhpClass(null, false);
    }

    private PhpClass(String namespacePart, boolean allowDoctrineShortcutNotation) {
        this.namespacePart = namespacePart;
        this.allowDoctrineShortcutNotation = allowDoctrineShortcutNotation;
    }

    public boolean allowDoctrineShortcutNotation() {
        return allowDoctrineShortcutNotation;
    }

    public String getNamespacePart() {
        return namespacePart;
    }
}
