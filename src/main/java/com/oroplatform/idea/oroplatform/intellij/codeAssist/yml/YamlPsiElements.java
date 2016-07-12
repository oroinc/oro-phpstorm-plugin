package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.*;

public class YamlPsiElements {

    static Collection<YAMLKeyValue> getKeyValuesFrom(PsiElement element) {
        return filterKeyValues(Arrays.asList(element.getChildren()));
    }

    static Collection<YAMLKeyValue> getKeyValuesFrom(Collection<? extends PsiElement> elements) {
        final Collection<PsiElement> children = new LinkedList<PsiElement>();
        for (PsiElement element : elements) {
            children.addAll(Arrays.asList(element.getChildren()));
        }
        return filterKeyValues(children);
    }

    private static Collection<YAMLKeyValue> filterKeyValues(Collection<PsiElement> elements) {
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

    static Collection<YAMLMapping> filterMappings(Collection<? extends PsiElement> elements) {
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
    static YAMLMapping getFirstMapping(PsiElement element, int maxDepth) {
        if(maxDepth == 0) return null;

        if(element instanceof YAMLMapping) {
            return (YAMLMapping) element;
        }

        return element == null ? null : getFirstMapping(element.getParent(), --maxDepth);
    }

    static Set<PsiElement> getAncestors(PsiElement element) {
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
}
