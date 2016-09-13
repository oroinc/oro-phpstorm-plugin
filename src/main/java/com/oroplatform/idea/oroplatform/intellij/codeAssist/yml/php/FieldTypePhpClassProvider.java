package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.php;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpUse;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.php.PhpClassProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import gnu.trove.THashSet;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getAncestors;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getPropertyFrom;

class FieldTypePhpClassProvider implements PhpClassProvider {
    private final PropertyPath classPropertyPath;

    FieldTypePhpClassProvider(PropertyPath classPropertyPath) {
        this.classPropertyPath = classPropertyPath;
    }

    @Override
    public Collection<String> getPhpClasses(PhpIndex phpIndex, PsiElement element, PropertyPath propertyPath) {
        final YAMLFile file = (YAMLFile) element.getContainingFile();
        final Set<PsiElement> ancestors = getAncestors(element);

        final List<YAMLMapping> elements = YamlPsiElements.getMappingsFrom(file);
        final Collection<String> fields = getPropertyFrom(propertyPath, elements, ancestors);
        final Collection<String> fieldClassNames = getPropertyFrom(classPropertyPath, elements, ancestors);

        final List<String> classNames = new LinkedList<String>();

        for (String className : fieldClassNames) {
            for (PhpClass phpClass : phpIndex.getClassesByFQN(className)) {
                for (Field field : phpClass.getFields()) {
                    if(fields.contains(field.getName())) {
                        classNames.addAll(getFieldTypes(field));
                    }
                }
            }
        }

        return classNames;
    }

    private Collection<String> getFieldTypes(Field field) {
        final Collection<String> classNames = new THashSet<String>();
        final PhpDocComment docComment = field.getDocComment();

        if(docComment == null) return classNames;

        final Pattern targetEntityPattern = Pattern.compile("targetEntity=\"(.*?)\"");

        for (PsiElement maybeDocTag : docComment.getChildren()) {
            if(!(maybeDocTag instanceof PhpDocTag)) continue;
            final PhpDocTag docTag = (PhpDocTag) maybeDocTag;

            if(!(docTag.getName().contains("ManyToMany") || docTag.getName().contains("ManyToOne") || docTag.getName().contains("OneToMany"))) continue;

            final Matcher matcher = targetEntityPattern.matcher(docTag.getText());
            if(!matcher.find()) continue;
            final String className = matcher.group(1);
            classNames.add(className);
            classNames.add(StringUtil.trimStart(field.getNamespaceName()+className, "\\"));

            for (PhpNamedElement phpNamedElement : ((PhpFile) field.getContainingFile()).getTopLevelDefs().values()) {
                if(!(phpNamedElement instanceof PhpUse)) continue;
                final PhpUse phpUse = (PhpUse) phpNamedElement;

                if(phpUse.getAliasName() == null) {
                    final int prefixNamespaceChar = className.indexOf("\\");

                    if(prefixNamespaceChar >= 0) {
                        classNames.add(StringUtil.trimStart(phpUse.getFQN() + className.substring(prefixNamespaceChar), "\\"));
                    } else {
                        classNames.add(StringUtil.trimStart(phpUse.getFQN() + className, "\\"));
                    }
                } else {
                    classNames.add(className.replaceFirst(phpUse.getAliasName(), phpUse.getFQN()));
                }
            }
        }

        return classNames;
    }
}
