package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpClassLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import com.oroplatform.idea.oroplatform.schema.Scalar;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Arrays.asList;

public class PhpClassReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;
    private final String rootBundlePath;
    private final Scalar.PhpClass phpClass;
    private final Set<String> skippedClassNames = new HashSet<String>();

    public PhpClassReference(PsiElement psiElement, Scalar.PhpClass phpClass, @NotNull String text, InsertHandler<LookupElement> insertHandler) {
        this(psiElement, phpClass, text, insertHandler, new HashSet<String>());
    }

    public PhpClassReference(PsiElement psiElement, Scalar.PhpClass phpClass, @NotNull String text, InsertHandler<LookupElement> insertHandler, Set<String> skippedClassNames) {
        super(psiElement);
        this.phpClass = phpClass;
        this.insertHandler = insertHandler;
        this.text = text.replace("IntellijIdeaRulezzz", "").trim().replace("\\\\", "\\");
        this.rootBundlePath = myElement.getContainingFile() == null ? "" : myElement.getContainingFile().getOriginalFile().getVirtualFile().getCanonicalPath().replaceFirst("/Resources/.*", "");
        this.skippedClassNames.addAll(skippedClassNames);
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

        final List<LookupElement> results = new LinkedList<LookupElement>();

        for (PhpClass phpClass : getPhpClassesFrom(phpIndex, getBundlesNamespaceNames(phpIndex))) {
            final int priority = getPriorityFor(phpClass);
            if(this.phpClass.allowDoctrineShortcutNotation()) {
                addEntitiesShortcutsLookups(results, phpClass, priority);
            } else {
                final InsertHandler<LookupElement> customInsertHandler = insertHandler != null ?
                    new ComposedInsertHandler(asList(PhpClassInsertHandler.INSTANCE, insertHandler)) : PhpClassInsertHandler.INSTANCE;
                results.add(PrioritizedLookupElement.withPriority(new PhpClassLookupElement(phpClass, true, customInsertHandler), priority));
            }
        }

        return results.toArray();
    }

    private Collection<PhpClass> getPhpClassesFrom(PhpIndex phpIndex, Collection<String> namespaceNames) {
        final Set<PhpClass> phpClasses = new HashSet<PhpClass>();
        final Collection<PhpClass> reporitoryInterfaces = phpIndex.getInterfacesByName("\\Doctrine\\Common\\Persistence\\ObjectRepository");
        final PhpClass repositoryInterface = reporitoryInterfaces.isEmpty() ? null : reporitoryInterfaces.iterator().next();

        for (String namespaceName : namespaceNames) {

            for (PhpNamespace phpNamespace : phpIndex.getNamespacesByName(namespaceName)) {
                for (PhpClass phpClass : getPhpClassesFrom(phpIndex, phpNamespace)) {
                    final boolean isClass = !phpClass.isInterface() && !phpClass.isTrait();
                    if(isClass && !skippedClassNames.contains(phpClass.getFQN()) &&
                        (!this.phpClass.getNamespacePart().equals("Entity") || repositoryInterface == null || isInstanceOf(phpIndex, phpClass, repositoryInterface))) {
                        phpClasses.add(phpClass);
                    }
                }
            }
        }

        return phpClasses;
    }

    private static boolean isInstanceOf(PhpIndex phpIndex,  PhpClass subjectClass, PhpClass expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), phpIndex);
    }

    private Collection<PhpClass> getPhpClassesFrom(PhpIndex phpIndex, PhpNamespace phpNamespace) {
        final List<PhpClass> phpClasses = PsiTreeUtil.getChildrenOfTypeAsList(phpNamespace.getStatements(), PhpClass.class);

        final String namespaceName = phpNamespace.getFQN().toLowerCase() + "\\";
        for (String parentNamespaceName : phpIndex.getChildNamespacesByParentName(namespaceName)) {
            for (PhpNamespace parentNamespace : phpIndex.getNamespacesByName(namespaceName + parentNamespaceName)) {
                phpClasses.addAll(getPhpClassesFrom(phpIndex, parentNamespace));
            }
        }

        return phpClasses;
    }

    private Collection<String> getBundlesNamespaceNames(PhpIndex phpIndex) {
        Collection<PhpClass> classes = phpIndex.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
        Collection<String> namespaces = new HashSet<String>();

        for (PhpClass phpClass : classes) {
            namespaces.add(phpClass.getNamespaceName()+this.phpClass.getNamespacePart());
        }

        return namespaces;
    }

    private static class ComposedInsertHandler implements InsertHandler<LookupElement> {
        private List<InsertHandler<LookupElement>> handlers = new LinkedList<InsertHandler<LookupElement>>();

        ComposedInsertHandler(List<InsertHandler<LookupElement>> handlers) {
            this.handlers.addAll(handlers);
        }

        @Override
        public void handleInsert(InsertionContext context, LookupElement item) {
            for (InsertHandler<LookupElement> handler : handlers) {
                handler.handleInsert(context, item);
            }
        }
    }

    private int getPriorityFor(PhpClass phpClass) {
        int priority = 150;

        if(isFromVendors(phpClass)) {
            priority -= 50;
        }

        final String classRootPath = phpClass.getNamespaceName().replace("\\", "/").replaceFirst("/"+this.phpClass.getNamespacePart()+"/.*", "");
        if(!rootBundlePath.endsWith(classRootPath)) {
            priority -= 50;
        }

        return priority;
    }

    private boolean isFromVendors(@NotNull PhpClass phpClass) {
        //sad null pointer checks
        return phpClass.getContainingFile() != null && phpClass.getContainingFile().getVirtualFile() != null &&
                phpClass.getContainingFile().getVirtualFile().getCanonicalPath() != null &&
                phpClass.getContainingFile().getVirtualFile().getCanonicalPath().contains("/vendor/");
    }

    private void addEntitiesShortcutsLookups(List<LookupElement> results, PhpClass phpClass, int priority) {
        final String shortcutName = PhpClassUtil.getDoctrineShortcutClassName(phpClass.getPresentableFQN());
        if(shortcutName != null) {
            results.add(PrioritizedLookupElement.withPriority(
                LookupElementBuilder.create(shortcutName).withIcon(Icons.DOCTRINE).withTypeText(phpClass.getPresentableFQN()).withInsertHandler(insertHandler),
                priority
            ));
        }
    }
}
