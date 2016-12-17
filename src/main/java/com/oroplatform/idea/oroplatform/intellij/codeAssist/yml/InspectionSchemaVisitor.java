package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.schema.*;
import com.oroplatform.idea.oroplatform.schema.requirements.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.jetbrains.yaml.psi.YAMLSequence;

import java.util.*;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

class InspectionSchemaVisitor implements Visitor {
    private final SchemaInspection.Errors errors;
    private final int currentDepth;
    private final List<PsiElement> elements = new LinkedList<>();

    InspectionSchemaVisitor(SchemaInspection.Errors errors, List<? extends PsiElement> elements) {
        this(errors, elements, 0);
    }

    private InspectionSchemaVisitor(SchemaInspection.Errors errors, List<? extends PsiElement> elements, int currentDepth) {
        this.errors = errors;
        this.currentDepth = currentDepth;
        this.elements.addAll(elements.stream().filter(Objects::nonNull).collect(Collectors.<PsiElement>toList()));
    }

    @Override
    public void visitSequence(Sequence sequence) {
        Visitor visitor = new InspectionSchemaVisitor(errors, getSequenceItems(elements), currentDepth + 1);
        sequence.getType().accept(visitor);

        final Collection<YAMLSequence> sequences = filterSequences(elements);

        checkType(sequences, "sequence");
    }

    @Override
    public void visitContainer(Container container) {
        final Collection<YAMLMapping> mappings = filterMappings(elements);
        for(YAMLMapping element : mappings) {
            Collection<YAMLKeyValue> keyValues = getKeyValuesFrom(element);

            for(Property property : container.getProperties()) {
                boolean found = false;
                for(YAMLKeyValue keyValue : keyValues) {
                    if(property.nameMatches(keyValue.getName())) {
                        found = true;
                        Visitor visitor = new InspectionSchemaVisitor(errors, Collections.<PsiElement>singletonList(keyValue.getValue()), currentDepth + 1);
                        property.getValueElement().accept(visitor);

                        if(keyValue.getValue() == null) {
                            errors.add(keyValue, OroPlatformBundle.message("inspection.schema.emptyValue", keyValue.getName()), currentDepth);
                        }
                    }
                }

                if(!found && property.isRequired()) {
                    errors.add(element, OroPlatformBundle.message("inspection.schema.required", property.getName()), currentDepth);
                }
            }

            final Set<String> alreadyProcessedPropertyNames = new HashSet<>();
            for(YAMLKeyValue keyValue : keyValues) {
                if(!container.areExtraPropertiesAllowed() && !existsPropertyMatchingTo(container.getProperties(), keyValue.getKeyText())) {
                    errors.add(keyValue, OroPlatformBundle.message("inspection.schema.notAllowedProperty", keyValue.getKeyText()), currentDepth);
                } else if(alreadyProcessedPropertyNames.contains(keyValue.getName())) {
                    errors.add(keyValue, OroPlatformBundle.message("inspection.schema.propertyAlreadyDefined", keyValue.getName()), currentDepth);
                }

                alreadyProcessedPropertyNames.add(keyValue.getName());
            }
        }

        checkType(mappings, "object");
    }

    private <T extends PsiElement> void checkType(Collection<T> correctTypeElements, String typeName) {
        if(correctTypeElements.isEmpty() && !elements.isEmpty()) {
            for (PsiElement element : elements) {
                if(!isDefaultValue(element)) {
                    errors.add(element, OroPlatformBundle.message("inspection.schema.invalidType", typeName), currentDepth);
                }
            }
        }
    }

    private boolean isDefaultValue(PsiElement element) {
        return element instanceof YAMLScalar && "~".equals(((YAMLScalar) element).getTextValue());
    }

    private boolean existsPropertyMatchingTo(List<Property> properties, String name) {
        for(Property property : properties) {
            if(property.nameMatches(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void visitOneOf(OneOf oneOf) {
        final List<SchemaInspection.Errors> allErrors = new LinkedList<>();
        for(Element element : oneOf.getElements()) {
            SchemaInspection.Errors newErrors = new SchemaInspection.Errors();
            Visitor visitor = new InspectionSchemaVisitor(newErrors, elements, currentDepth + 1);
            element.accept(visitor);
            allErrors.add(newErrors);
        }

        final NavigableSet<SchemaInspection.Errors> sortedProblemsHolders = getSortedErrors(allErrors);

        if(sortedProblemsHolders.size() > 0) {
            SchemaInspection.Errors theShortestProblemsHolder = sortedProblemsHolders.first();
            theShortestProblemsHolder.getErrors().forEach(errors::add);
        }
    }

    @NotNull
    private NavigableSet<SchemaInspection.Errors> getSortedErrors(List<SchemaInspection.Errors> errorsList) {
        NavigableSet<SchemaInspection.Errors> sortedProblemsHolders = new TreeSet<>(new ProblemsHolderComparator());
        sortedProblemsHolders.addAll(errorsList);
        return sortedProblemsHolders;
    }

    @Override
    public void visitRepeatAtAnyLevel(Repeated repeated) {
        //right now it is not used for inspections, so skip implementation
    }

    @Override
    public void visitScalar(Scalar scalar) {
        final Collection<YAMLScalar> scalarElements = filterScalars(elements);
        for (Requirement requirement : scalar.getRequirements()) {
            for(YAMLScalar element : scalarElements) {
                for (String error : requirement.getErrors(element.getTextValue())) {
                    errors.add(element, error, currentDepth);
                }
            }
        }

        checkType(scalarElements, "scalar");
    }

    private static class ProblemsHolderComparator implements Comparator<SchemaInspection.Errors> {

        private final String pattern = OroPlatformBundle.message("inspection.schema.notAllowedPropertyValue", "PLACEHOLDER", "PLACEHOLDER").replace(".", "\\.").replace("PLACEHOLDER", ".*");

        @Override
        public int compare(SchemaInspection.Errors o1, SchemaInspection.Errors o2) {
            final boolean choiceInO1 = containsNotAllowedPropertyValueProblem(o1);
            final boolean choiceInO2 = containsNotAllowedPropertyValueProblem(o2);
            final int depth1 = o1.getErrors().stream().map(error -> error.depth).min(Integer::compareTo).orElse(Integer.MAX_VALUE);
            final int depth2 = o2.getErrors().stream().map(error -> error.depth).min(Integer::compareTo).orElse(Integer.MAX_VALUE);

            if(choiceInO1 == choiceInO2) {
                if(depth2 == depth1) {
                    return o1.getErrors().size() - o2.getErrors().size();
                } else {
                    return depth2 - depth1;
                }
            } else {
                if(depth2 == depth1) {
                    return choiceInO1 ? 1 : -1;
                } else {
                    return depth2 - depth1;
                }
            }
        }

        private boolean containsNotAllowedPropertyValueProblem(SchemaInspection.Errors o1) {
            return o1.getErrors().stream().anyMatch(error -> error.message.matches(pattern));
        }
    }
}
