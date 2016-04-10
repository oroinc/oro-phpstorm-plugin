package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class PsiElements {

    static Collection<YAMLKeyValue> getKeyValuesFrom(YAMLMapping element) {
        return getKeyValues(Arrays.asList(element.getChildren()));
    }

    static Collection<YAMLKeyValue> getKeyValues(Collection<PsiElement> elements) {
        List<YAMLKeyValue> children = new LinkedList<YAMLKeyValue>();
        for(PsiElement child : elements) {
            if(child instanceof YAMLKeyValue) {
                children.add((YAMLKeyValue) child);
            }
        }

        return children;
    }

    static Collection<YAMLScalar> getScalars(Collection<PsiElement> elements) {
        List<YAMLScalar> children = new LinkedList<YAMLScalar>();
        for(PsiElement child : elements) {
            if(child instanceof YAMLScalar) {
                children.add((YAMLScalar) child);
            }
        }

        return children;
    }

    static Collection<YAMLMapping> getMappings(Collection<PsiElement> elements) {
        List<YAMLMapping> children = new LinkedList<YAMLMapping>();
        for(PsiElement child : elements) {
            if(child instanceof YAMLMapping) {
                children.add((YAMLMapping) child);
            }
        }

        return children;
    }

    static List<PsiElement> getMappingsFrom(@NotNull PsiFile file) {
        List<PsiElement> elements = new LinkedList<PsiElement>();
        for(PsiElement element : file.getChildren()) {
            if(element instanceof YAMLDocument) {
                elements.addAll(Arrays.asList(element.getChildren()));
            }
        }
        return elements;
    }

    static List<PsiElement> getSequenceItems(List<PsiElement> elements) {
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
    static YAMLKeyValue getYamlKeyValueSiblingWithName(YAMLKeyValue keyValue, String name) {
        for(PsiElement element : keyValue.getParent().getChildren()) {
            if(element instanceof YAMLKeyValue && name.equals(((YAMLKeyValue) element).getKeyText())) {
                return ((YAMLKeyValue) element);
            }
        }

        return null;
    }
}
