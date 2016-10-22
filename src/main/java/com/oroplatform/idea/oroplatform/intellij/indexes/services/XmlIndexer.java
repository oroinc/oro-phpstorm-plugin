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

public class XmlIndexer implements DataIndexer<Service, Void, XmlFile> {

    @NotNull
    @Override
    public Map<Service, Void> map(@NotNull XmlFile file) {
        final Map<Service, Void> index = new THashMap<>();
        final Collection<XmlTag> roots = PsiTreeUtil.findChildrenOfType(file.getDocument(), XmlTag.class);

        for (XmlTag root : ofName(roots, "container")) {
            for (XmlTag servicesTag : ofName(PsiTreeUtil.findChildrenOfType(root, XmlTag.class), "services")) {
                for (XmlTag serviceTag : ofName(PsiTreeUtil.findChildrenOfType(servicesTag, XmlTag.class), "service")) {
                    final Collection<Tag> serviceTags = getServiceTags(serviceTag);
                    final String id = serviceTag.getAttributeValue("id");
                    final Service service = new Service(id, serviceTags);

                    index.put(service, null);
                }
            }
        }

        return index;
    }

    private static Collection<XmlTag> ofName(Collection<XmlTag> tags, String name) {
        return tags.stream()
            .filter(tag -> tag.getName().equals(name))
            .collect(Collectors.toList());
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
