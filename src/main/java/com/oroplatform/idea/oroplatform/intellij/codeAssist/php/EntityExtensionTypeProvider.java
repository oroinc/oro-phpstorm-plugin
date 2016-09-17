package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class EntityExtensionTypeProvider implements PhpTypeProvider3 {
    @Override
    public char getKey() {
        return '\u0189';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        final Project project = psiElement.getProject();

        if(!OroPlatformSettings.getInstance(project).isPluginEnabled()) {
            return null;
        }

        if(psiElement instanceof Variable) {
            final Variable variable = (Variable) psiElement;

            if(!StringUtil.startsWith(variable.getSignature(), "#C")) return null;
            final String className = StringUtil.trimStart(variable.getSignature(), "#C");

            return getType(project, className);
        }

        if(psiElement instanceof Method) {
            final Method method = (Method) psiElement;
            final PhpType phpType = new PhpType();

            for (String localType : method.getLocalType(false).getTypes()) {
                phpType.add(getType(project, localType));
            }

            return phpType;
        }

        if(psiElement instanceof FieldReference) {
            final FieldReference fieldReference = (FieldReference) psiElement;
            final Field field = (Field) fieldReference.resolve();

            if(field == null) return null;

            final PhpType phpType = new PhpType();

            for (String type : field.getType().getTypes()) {
                phpType.add(getType(project, type));
            }

            if(field.getDocComment() == null || field.getDocComment().getVarTag() == null) return phpType;

            final PhpDocParamTag varTag = field.getDocComment().getVarTag();

            for (String type : varTag.getType().getTypes()) {
                phpType.add(getType(project, type));
            }

            return phpType;
        }

        return null;
    }

    @Nullable
    private PhpType getType(Project project, String className) {
        final PhpIndex phpIndex = PhpIndex.getInstance(project);
        final PhpType outputType = new PhpType();

        for (PhpClass phpClass : phpIndex.getClassesByFQN(className)) {
            final Entity entity = Entity.fromFqn(phpClass.getPresentableFQN());

            if(entity == null) return null;

            final String bundleName = entity.getBundle().getName();

            final String extensionClassName = "\\Extend\\Entity\\EX_" + bundleName + "_" + phpClass.getName();

            for (PhpClass extensionClass : phpIndex.getClassesByFQN(extensionClassName)) {
                outputType.add(extensionClass);
            }
        }

        return outputType;
    }

    //TODO: should be it defined correctly if PhpIndex.getBySignature is not used?
    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        return null;
    }
}
