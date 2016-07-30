package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.*;

public class YamlPsiElements {

    public static Collection<YAMLKeyValue> getKeyValuesFrom(PsiElement element) {
        return filterKeyValues(Arrays.asList(element.getChildren()));
    }

    public static Collection<YAMLKeyValue> getKeyValuesFrom(Collection<? extends PsiElement> elements) {
        final Collection<PsiElement> children = new LinkedList<PsiElement>();
        for (PsiElement element : elements) {
            children.addAll(Arrays.asList(element.getChildren()));
        }
        return filterKeyValues(children);
    }

    private static Collection<YAMLKeyValue> filterKeyValues(Collection<? extends PsiElement> elements) {
        List<YAMLKeyValue> out = new LinkedList<YAMLKeyValue>();
        for(PsiElement element : elements) {
            if(element instanceof YAMLKeyValue) {
                out.add((YAMLKeyValue) element);
            }
        }

        return out;
    }

    static Collection<YAMLScalar> filterScalars(Collection<? extends PsiElement> elements) {
        List<YAMLScalar> children = new LinkedList<YAMLScalar>();
        for(PsiElement element : elements) {
            if(element instanceof YAMLScalar) {
                children.add((YAMLScalar) element);
            }
        }

        return children;
    }

    public static Collection<YAMLMapping> filterMappings(Collection<? extends PsiElement> elements) {
        List<YAMLMapping> out = new LinkedList<YAMLMapping>();
        for(PsiElement element : elements) {
            if(element instanceof YAMLMapping) {
                out.add((YAMLMapping) element);
            }
        }

        return out;
    }

    private static Collection<YAMLSequence> filterSequences(Collection<? extends PsiElement> elements) {
        List<YAMLSequence> out = new LinkedList<YAMLSequence>();
        for(PsiElement element : elements) {
            if(element instanceof YAMLSequence) {
                out.add((YAMLSequence) element);
            }
        }

        return out;
    }

    public static List<YAMLMapping> getMappingsFrom(@NotNull PsiFile file) {
        List<YAMLMapping> elements = new LinkedList<YAMLMapping>();
        for(PsiElement element : file.getChildren()) {
            if(element instanceof YAMLDocument) {
                elements.addAll(filterMappings(Arrays.asList(element.getChildren())));
            }
        }
        return elements;
    }

    public static List<YAMLPsiElement> getSequenceItems(Collection<? extends PsiElement> elements) {
        List<YAMLPsiElement> items = new LinkedList<YAMLPsiElement>();
        for(PsiElement element : elements) {
            if(element instanceof YAMLSequence) {
                for(YAMLSequenceItem item : ((YAMLSequence) element).getItems()) {
                    if(item.getValue() != null) {
                        items.add(item.getValue());
                    }
                }
            }
        }

        return items;
    }


    @Nullable
    public static YAMLKeyValue getYamlKeyValueSiblingWithName(YAMLKeyValue keyValue, String name) {
        for(PsiElement element : keyValue.getParent().getChildren()) {
            if(element instanceof YAMLKeyValue && name.equals(((YAMLKeyValue) element).getKeyText())) {
                return ((YAMLKeyValue) element);
            }
        }

        return null;
    }

    @Nullable
    public static YAMLMapping getFirstMapping(PsiElement element) {
        return getFirstMapping(element, Integer.MAX_VALUE);
    }

    @Nullable
    public static YAMLMapping getFirstMapping(PsiElement element, int maxDepth) {
        if(maxDepth == 0) return null;

        if(element instanceof YAMLMapping) {
            return (YAMLMapping) element;
        }

        return element == null ? null : getFirstMapping(element.getParent(), --maxDepth);
    }

    public static Set<PsiElement> getAncestors(PsiElement element) {
        return getAncestors(element, new HashSet<PsiElement>());
    }

    private static Set<PsiElement> getAncestors(PsiElement element, Set<PsiElement> ancestors) {
        final PsiElement parent = element.getParent();

        if(parent == null || parent instanceof PsiFile) {
            return ancestors;
        }

        ancestors.add(parent);

        return getAncestors(parent, ancestors);
    }

    public static Collection<String> getPropertyKeysFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getParentKeysFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyKeysFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    public static Collection<String> getPropertyValuesFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getPropertyValuesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyValuesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    @NotNull
    private static Collection<YAMLPsiElement> getElementsForProperty(PropertyPath.Property property, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        final Collection<YAMLPsiElement> newElements = new LinkedList<YAMLPsiElement>();

        if(property.isThis()) {
            for (YAMLKeyValue keyValue : getKeyValuesFrom(elements)) {
                if(keyValue.getValue() != null && ancestors.contains(keyValue.getValue())) {
                    newElements.add(keyValue.getValue());
                }
            }

            for (YAMLPsiElement item : getSequenceItems(elements)) {
                if(ancestors.contains(item)) {
                    newElements.add(item);
                }
            }
        } else {
            for (YAMLMapping mapping : filterMappings(elements)) {
                final YAMLKeyValue keyValue = mapping.getKeyValueByKey(property.getName());
                if(keyValue != null && keyValue.getValue() != null) {
                    newElements.add(keyValue.getValue());
                }
            }

            for (YAMLSequence sequence : filterSequences(elements)) {
                newElements.add(sequence);
            }
        }
        return newElements;
    }

    private static Collection<String> getParentKeysFrom(Collection<? extends YAMLPsiElement> elements) {
        final Collection<String> keys = new THashSet<String>();
        for (YAMLPsiElement element : elements) {
            final PsiElement parent = element.getParent();

            if(parent instanceof YAMLKeyValue) {
                keys.add(((YAMLKeyValue) parent).getKeyText());
            }
        }

        return keys;
    }

    private static Collection<String> getPropertyValuesFrom(Collection<? extends YAMLPsiElement> elements) {
        final Collection<String> values = new THashSet<String>();
        for (YAMLScalar element : filterScalars(elements)) {
            values.add(element.getTextValue());
        }

        for(YAMLMapping element : filterMappings(elements)) {
            for (YAMLKeyValue keyValue : element.getKeyValues()) {
                values.add(keyValue.getKeyText());
            }
        }

        return values;
    }

}
