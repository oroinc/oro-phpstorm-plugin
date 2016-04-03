package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpClassLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PhpClassReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final Scalar.PhpClass.Type type;

    public PhpClassReference(PsiElement psiElement, Scalar.PhpClass.Type type, @NotNull String text) {
        super(psiElement);
        this.type = type;
        this.text = text.replace("IntellijIdeaRulezzz", "").trim().replace("\\\\", "\\");
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());
        final List<ResolveResult> results = new LinkedList<ResolveResult>();

        for(String className : resolveClassNames(phpIndex, text)) {
            for(PhpClass phpClass : phpIndex.getClassesByFQN(className)) {
                results.add(new PsiElementResolveResult(phpClass));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    private Collection<String> resolveClassNames(PhpIndex phpIndex, String text) {
        final Set<String> names = new HashSet<String>();
        names.add(text);

        if(text.contains(":")) {
            final String simpleName = PhpClassUtil.getSimpleName(text);
            final String namespaceShortcut = text.substring(0, text.indexOf(':'));
            final Collection<PhpClass> phpClasses = phpIndex.getClassesByName(simpleName);

            for(PhpClass phpClass : phpClasses) {
                final PrefixMatcher matcher = new CamelHumpMatcher(namespaceShortcut);
                if(matcher.isStartMatch(phpClass.getNamespaceName().replace("\\", ""))) {
                    names.add(phpClass.getFQN());
                }
            }
        }

        return names;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final PhpIndex phpIndex = PhpIndex.getInstance(myElement.getProject());
        final String namespacePart = "\\"+ type.toString() +"\\";

        final List<LookupElement> results = new LinkedList<LookupElement>();
        for(String className : findClassNames(phpIndex)) {
            final PhpClass phpClass = phpIndex.getClassByName(className);
            if(phpClass != null && phpClass.getNamespaceName().contains(namespacePart)) {
                final int priority = getPriorityFor(phpClass);
                results.add(PrioritizedLookupElement.withPriority(new PhpClassLookupElement(phpClass, true, PhpClassInsertHandler.INSTANCE), priority));
                if(type == Scalar.PhpClass.Type.Entity) {
                    addEntitiesShortcutsLookups(results, phpClass, priority);
                }
            }
        }

        return results.toArray();
    }

    private int getPriorityFor(PhpClass phpClass) {
        if (myElement.getContainingFile() == null) {
            return 0;
        }

        final String rootPath = myElement.getContainingFile().getOriginalFile().getVirtualFile().getCanonicalPath().replaceFirst("/Resources/.*", "");
        final String classRootPath = phpClass.getNamespaceName().replace("\\", "/").replaceFirst("/"+type.toString()+"/.*", "");

        return rootPath.endsWith(classRootPath) ? 1 : 0;
    }

    private void addEntitiesShortcutsLookups(List<LookupElement> results, PhpClass phpClass, int priority) {
        final String shortcutName = PhpClassUtil.getDoctrineShortcutClassName(phpClass.getPresentableFQN());
        if(shortcutName != null) {
            results.add(PrioritizedLookupElement.withPriority(
                LookupElementBuilder.create(shortcutName).withIcon(Icons.DOCTRINE).withTypeText(phpClass.getPresentableFQN()),
                priority
            ));
        }
    }

    private Collection<String> findClassNames(PhpIndex phpIndex) {
        final PrefixMatcher classMatcher = new CamelHumpMatcher(text);
        return phpIndex.getAllClassNames(classMatcher);
    }
}
