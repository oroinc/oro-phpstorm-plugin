package com.oroplatform.idea.oroplatform.intellij.indexes.services;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.DataIndexer;
import com.oroplatform.idea.oroplatform.symfony.Service;
import com.oroplatform.idea.oroplatform.symfony.Tag;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class XmlIndexer implements DataIndexer<String, Collection<Service>, XmlFile> {
    @NotNull
    @Override
    public Map<String, Collection<Service>> map(@NotNull XmlFile file) {
        final Map<String, Collection<Service>> index = new THashMap<String, Collection<Service>>();
        final Collection<XmlTag> roots = PsiTreeUtil.findChildrenOfType(file.getDocument(), XmlTag.class);

        for (XmlTag root : ofName(roots, "container")) {
            for (XmlTag servicesTag : ofName(PsiTreeUtil.findChildrenOfType(root, XmlTag.class), "services")) {
                for (XmlTag serviceTag : ofName(PsiTreeUtil.findChildrenOfType(servicesTag, XmlTag.class), "service")) {
                    final Collection<Tag> serviceTags = getServiceTags(serviceTag);
                    final Service service = new Service(getXmlAttribute(servicesTag, "id"), getXmlAttribute(servicesTag, "class"), serviceTags);

                    addServicesToIndex(service, index);
                }
            }
        }

        return index;
    }

    private static Collection<XmlTag> ofName(Collection<XmlTag> tags, String name) {
        Collection<XmlTag> tagsWithName = new LinkedList<XmlTag>();
        for (XmlTag tag : tags) {
            if(tag.getName().equals(name)) {
                tagsWithName.add(tag);
            }
        }

        return tagsWithName;
    }

    private static void addServicesToIndex(Service service, Map<String, Collection<Service>> index) {
        Collection<Tag> serviceTags = service.getTags();
        for (Tag tag : serviceTags) {
            if(tag.getName() != null) {
                if(index.containsKey(tag.getName())) {
                    index.get(tag.getName()).add(service);
                } else {
                    Collection<Service> services = new THashSet<Service>();
                    services.add(service);
                    index.put(tag.getName(), services);
                }
            }
        }
    }

    @NotNull
    private static Collection<Tag> getServiceTags(XmlTag serviceTag) {
        final Collection<Tag> serviceTags = new HashSet<Tag>();

        for (XmlTag tag : PsiTreeUtil.findChildrenOfType(serviceTag, XmlTag.class)) {
            if(tag.getName().equals("tag")) {
                serviceTags.add(new Tag(getXmlAttribute(tag, "name"), getXmlAttribute(tag, "alias")));
            }
        }
        return serviceTags;
    }

    private static String getXmlAttribute(XmlTag tag, String attributeName) {
        final XmlAttribute attribute = tag.getAttribute(attributeName);

        return attribute == null ? null : attribute.getValue();
    }
}
