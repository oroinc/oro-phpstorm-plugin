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

    static Collection<YAMLScalar> filterScalars(Collection<PsiElement> elements) {
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

    public static List<YAMLMapping> getMappingsFrom(@NotNull PsiFile file) {
        List<YAMLMapping> elements = new LinkedList<YAMLMapping>();
        for(PsiElement element : file.getChildren()) {
            if(element instanceof YAMLDocument) {
                elements.addAll(filterMappings(Arrays.asList(element.getChildren())));
            }
        }
        return elements;
    }

    public static List<PsiElement> getSequenceItems(List<? extends PsiElement> elements) {
        List<PsiElement> items = new LinkedList<PsiElement>();
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

    public static Collection<String> getPropertiesFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getPropertiesFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertiesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
    }

    public static Collection<String> getPropertyNamesFrom(PropertyPath path, Collection<? extends YAMLPsiElement> elements, Set<PsiElement> ancestors) {
        if(path.getProperties().isEmpty()) {
            return getParentKeysFrom(elements);
        }

        final PropertyPath.Property property = path.getProperties().element();

        return getPropertyNamesFrom(path.dropHead(), getElementsForProperty(property, elements, ancestors), ancestors);
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
        } else {
            for (YAMLMapping mapping : filterMappings(elements)) {
                final YAMLKeyValue keyValue = mapping.getKeyValueByKey(property.getName());
                if(keyValue != null && keyValue.getValue() != null) {
                    newElements.add(keyValue.getValue());
                }
            }
        }
        return newElements;
    }

    private static Collection<String> getPropertiesFrom(Collection<? extends YAMLPsiElement> elements) {
        Collection<String> properties = new THashSet<String>();

        for (YAMLMapping mapping : filterMappings(elements)) {
            for (YAMLKeyValue keyValue : YamlPsiElements.getKeyValuesFrom(mapping)) {
                properties.add(keyValue.getKeyText());
            }
        }

        return properties;
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

}
