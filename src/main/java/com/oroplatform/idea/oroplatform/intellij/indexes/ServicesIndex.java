package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpReturn;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.ServiceClassName;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class ServicesIndex {

    private final Project project;

    private ServicesIndex(Project project) {
        this.project = project;
    }

    public static ServicesIndex instance(Project project) {
        return new ServicesIndex(project);
    }

    public Collection<String> findConditionNames() {
        return FileBasedIndex.getInstance().getAllKeys(ConditionsFileBasedIndex.KEY, project);
    }

    public Collection<String> findActionNames() {
        return FileBasedIndex.getInstance().getAllKeys(ActionsFileBasedIndex.KEY, project);
    }

    public Collection<String> findFormTypes() {
        return FileBasedIndex.getInstance().getAllKeys(FormTypesFileBasedIndex.KEY, project);
    }

    public Collection<String> findApiFormTypes() {
        final Collection<String> standardApiFormTypes = findStandardApiFormTypes().stream()
            .map(formType -> formType.replace("form.type.", "")).collect(Collectors.toSet());

        return Stream.concat(
            findFormTypes().stream()
                .filter(standardApiFormTypes::contains),
            FileBasedIndex.getInstance().getAllKeys(ApiFormTypesFileBasedIndex.KEY, project).stream()
        ).collect(Collectors.toList());
    }

    private Collection<String> findStandardApiFormTypes() {
        return FileBasedIndex.getInstance().getAllKeys(StandardApiFormTypeFileBasedIndex.KEY, project);
    }

    public Collection<String> findServices() {
        return FileBasedIndex.getInstance().getAllKeys(ServicesFileBasedIndex.KEY, project);
    }

    public Collection<String> findServices(Predicate<Service> predicate) {
        final Collection<String> serviceNames = FileBasedIndex.getInstance().getAllKeys(ServicesFileBasedIndex.KEY, project);
        final GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        return serviceNames.stream().filter(serviceName -> {
            final Collection<Service> services = new LinkedList<>();
            FileBasedIndex.getInstance().processValues(ServicesFileBasedIndex.KEY, serviceName, null, ((file, value) -> {
                services.add(value);
                return false;
            }), scope);

            return services.stream().anyMatch(predicate);
        }).collect(Collectors.toList());
    }

    public Optional<Service> findService(String id) {
        return FileBasedIndex.getInstance().getValues(ServicesFileBasedIndex.KEY, id, GlobalSearchScope.allScope(project)).stream()
            .findFirst();
    }

    public Collection<String> findMassActionProviders() {
        return FileBasedIndex.getInstance().getAllKeys(MassActionProviderFileBasedIndex.KEY, project);
    }

    public Optional<String> findParameterValue(String name) {
        return FileBasedIndex.getInstance().getValues(ServiceParametersFileBasedIndex.KEY, name, GlobalSearchScope.allScope(project)).stream()
            .findFirst();
    }

    public Collection<WorkflowScope> findWorkflowScopes() {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);

        return FileBasedIndex.getInstance().getAllKeys(WorkflowScopeFileBasedIndex.KEY, project).stream()
            .map(ServiceClassName::new)
            .map(serviceClassName -> {
                return serviceClassName.getServiceParameter()
                    .flatMap(this::findParameterValue)
                    .orElse(serviceClassName.getClassName());
            })
            .flatMap(className -> phpIndex.getClassesByFQN(className).stream())
            .flatMap(phpClass -> toStream(findWorkflowScope(phpClass)))
            .collect(Collectors.toList());
    }

    private Optional<WorkflowScope> findWorkflowScope(PhpClass phpClass) {
        return phpClass.getMethods().stream()
            .filter(method -> "getCriteriaField".equals(method.getName()))
            .flatMap(method -> PsiTreeUtil.collectElementsOfType(method, PhpReturn.class).stream())
            .flatMap(phpReturn -> toStream(phpReturn.getArgument()))
            .flatMap(argument -> {
                if(argument instanceof ClassConstantReference && ((ClassConstantReference) argument).resolve() instanceof Field) {
                    final Field classConstant = (Field) ((ClassConstantReference) argument).resolve();
                    return toStream(classConstant.getDefaultValue()).map(PsiElement::getText);
                } else {
                    return Stream.of(argument.getText());
                }
            })
            .map(StringUtil::stripQuotesAroundValue)
            .map(name -> new WorkflowScope(phpClass, name))
            .findFirst();
    }

    public static class WorkflowScope {
        public final PhpClass phpClass;
        public final String name;

        private WorkflowScope(PhpClass phpClass, String name) {
            this.phpClass = phpClass;
            this.name = name;
        }
    }
}
