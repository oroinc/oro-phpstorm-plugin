package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.DataExternalizer;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class WorkflowScopeFileBasedIndex extends BaseServicesFileBasedIndex<Void> {
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.workflow_scopes");

    public WorkflowScopeFileBasedIndex() {
        super("oro_scope.provider");
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }

    @Override
    protected void index(Set<Service> services, Map<String, Void> index) {
        services.stream()
            .filter(this::isWorkflowScope)
            .flatMap(service -> toStream(service.getClassName()))
            .forEach(className -> index.put(className.getClassName(), null));
    }

    private boolean isWorkflowScope(Service service) {
        return service.getTags().stream().anyMatch(tag -> "oro_scope.provider".equals(tag.getName()) && tag.get("scopeType").filter("workflow_definition"::equals).isPresent());
    }
}
