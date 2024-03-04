package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class EntityExtensionTypeProvider implements PhpTypeProvider4 {
    @Override
    public char getKey() {
        return '\u0189';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement psiElement) {
        final Project project = psiElement.getProject();

        if(!OroPlatformSettings.getInstance(project).isPluginEnabled() || DumbService.isDumb(project)) {
            return null;
        }

        if(psiElement instanceof Variable variable) {

            if(!StringUtil.startsWith(variable.getSignature(), "#C")) return null;
            final String className = StringUtil.trimStart(variable.getSignature(), "#C");

            return getType(project, className);
        }

        if(psiElement instanceof Method method) {
            final PhpType phpType = new PhpType();

            for (String localType : method.getLocalType(false).getTypes()) {
                phpType.add(getType(project, localType));
            }

            return phpType;
        }

        if(psiElement instanceof Field field) {

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

        if(psiElement instanceof AssignmentExpression assignment) {

            if(assignment.getValue() == null ||
                    assignment.getValue().getFirstPsiChild() == null ||
                    !(assignment.getValue().getFirstPsiChild() instanceof ClassReference)) return null;

            final String className = assignment.getValue().getFirstPsiChild().getText();

            return getType(project, className);
        }

        return null;
    }

    private PhpType getType(Project project, String className) {
        final PhpType outputType = new PhpType();

        for (Entity entity : Entities.instance(project).findEntities(className)) {
            final String bundleName = entity.getBundle().getName();

            final String extensionClassName = "\\Extend\\Entity\\EX_" + bundleName + "_" + entity.getSimpleName();

            outputType.add("#C" + extensionClassName);
        }

        return outputType;
    }

    @SuppressWarnings("unused")
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {
        return null;
    }

    @Override
    public @Nullable PhpType complete(String s, Project project) {
        return null; //TODO this is a stub, currently this functionality is not operational, but implementation might become necessary once it's restored
    }

    @Override
    public boolean emptyResultIsComplete() {
        return PhpTypeProvider4.super.emptyResultIsComplete();
    }

    @Override
    public boolean interceptsNativeSignature() {
        return PhpTypeProvider4.super.interceptsNativeSignature();
    }
}
