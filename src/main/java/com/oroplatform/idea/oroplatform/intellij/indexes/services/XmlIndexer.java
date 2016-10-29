package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.DataIndexer;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlIndexer implements DataIndexer<Service, Void, XmlFile> {

    @NotNull
    @Override
    public Map<Service, Void> map(@NotNull XmlFile file) {
        final Map<Service, Void> index = new THashMap<>();
        final Collection<XmlTag> roots = PsiTreeUtil.findChildrenOfType(file.getDocument(), XmlTag.class);

        ofName(roots, "container")
            .flatMap(root -> ofName(PsiTreeUtil.findChildrenOfType(root, XmlTag.class), "services"))
            .flatMap(servicesTag -> ofName(PsiTreeUtil.findChildrenOfType(servicesTag, XmlTag.class), "service"))
            .map(serviceTag -> new Service(serviceTag.getAttributeValue("id"), getServiceTags(serviceTag)))
            .forEach(service -> index.put(service, null));

        return index;
    }

    private static Stream<XmlTag> ofName(Collection<XmlTag> tags, String name) {
        return tags.stream()
            .filter(tag -> tag.getName().equals(name));
    }

    @NotNull
    private static Collection<Tag> getServiceTags(XmlTag serviceTag) {
        return PsiTreeUtil.findChildrenOfType(serviceTag, XmlTag.class).stream()
            .filter(tag -> tag.getName().equals("tag"))
            .map(tag -> new Tag(getXmlAttribute(tag, "name"), getXmlAttribute(tag, "alias")))
            .collect(Collectors.toList());
    }

    private static String getXmlAttribute(XmlTag tag, String attributeName) {
        final XmlAttribute attribute = tag.getAttribute(attributeName);

        return attribute == null ? null : attribute.getValue();
    }
}
