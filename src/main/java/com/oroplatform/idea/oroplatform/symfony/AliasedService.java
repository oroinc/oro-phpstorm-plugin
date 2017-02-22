package com.oroplatform.idea.oroplatform.symfony;

public class AliasedService {
    private final Service service;
    private final String alias;

    public AliasedService(Service service, String alias) {
        this.service = service;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public Service getService() {
        return service;
    }
}
