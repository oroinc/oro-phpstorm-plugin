package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class ServicesFileBasedIndex extends BaseServicesFileBasedIndex {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.services");

    ServicesFileBasedIndex() {
        super("");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @Override
    protected void index(Set<Service> services, Map<String, Void> index) {
        for (Service service : services) {
            if(service.getId() != null) {
                index.put(service.getId(), null);
            }
        }
    }
}
