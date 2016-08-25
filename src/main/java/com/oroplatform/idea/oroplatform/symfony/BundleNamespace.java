package com.oroplatform.idea.oroplatform.symfony;

public class BundleNamespace {
    private final Bundle bundle;
    private final String namespace;

    public BundleNamespace(Bundle bundle, String namespace) {
        this.bundle = bundle;
        this.namespace = namespace;
    }

    public String getName() {
        return bundle.getNamespaceName() + "\\" + this.namespace;
    }
}
