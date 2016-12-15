package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.DataIndexer;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class ParameterXmlIndexer implements DataIndexer<String, String, XmlFile> {
    @NotNull
    @Override
    public Map<String, String> map(@NotNull XmlFile file) {
        final Map<String, String> index = new THashMap<>();

        final Collection<XmlTag> roots = PsiTreeUtil.findChildrenOfType(file.getDocument(), XmlTag.class);

        ofName(roots, "container")
            .flatMap(element -> PsiTreeUtil.findChildrenOfType(element, XmlTag.class).stream())
            .filter(element -> "parameters".equals(element.getName()))
            .flatMap(element -> PsiTreeUtil.findChildrenOfType(element, XmlTag.class).stream())
            .filter(element -> "parameter".equals(element.getName()))
            .filter(parameter -> parameter.getAttributeValue("key") != null)
            .forEach(parameter -> index.put(parameter.getAttributeValue("key"), parameter.getValue().getText()));

        return index;
    }

    private static Stream<XmlTag> ofName(Collection<XmlTag> tags, String name) {
        return tags.stream()
            .filter(tag -> tag.getName().equals(name));
    }
}
