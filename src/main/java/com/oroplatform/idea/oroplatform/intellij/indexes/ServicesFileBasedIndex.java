package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.intellij.util.io.DataExternalizer;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class ServicesFileBasedIndex extends BaseServicesFileBasedIndex<Service> {
    public static final ID<String, Service> KEY = ID.create("com.oroplatform.idea.oroplatform.services");
    private static final DataExternalizer<Service> serviceExternalizer = new JsonExternalizer<>(Service.class);

    ServicesFileBasedIndex() {
        super("");
    }

    @NotNull
    @Override
    public ID<String, Service> getName() {
        return KEY;
    }

    @Override
    protected void index(Set<Service> services, Map<String, Service> index) {
        services.stream()
            .filter(service -> service.getId() != null)
            .forEach(service -> index.put(service.getId(), service));
    }

    @NotNull
    @Override
    public DataExternalizer<Service> getValueExternalizer() {
        return serviceExternalizer;
    }

    @Override
    public int getVersion() {
        return 3;
    }
}
