package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.PhpClassUtil;
import com.oroplatform.idea.oroplatform.symfony.BundleNamespace;
import com.oroplatform.idea.oroplatform.symfony.Bundles;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static java.util.Arrays.asList;

public class PhpClassReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;
    private final String rootBundlePath;
    private final com.oroplatform.idea.oroplatform.schema.PhpClass phpClass;
    private final Set<String> skippedClassNames = new HashSet<>();
    private final PhpIndex phpIndex;
    private final PhpClass repositoryInterface;

    public PhpClassReference(PsiElement psiElement, com.oroplatform.idea.oroplatform.schema.PhpClass phpClass, @NotNull String text, InsertHandler<LookupElement> insertHandler, Set<String> skippedClassNames) {
        super(psiElement);
        this.phpClass = phpClass;
        this.insertHandler = insertHandler;
        this.text = text.replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\");
        this.rootBundlePath = myElement.getContainingFile() == null ? "" : myElement.getContainingFile().getOriginalFile().getVirtualFile().getCanonicalPath().replaceFirst("/Resources/.*", "");
        this.skippedClassNames.addAll(skippedClassNames);
        this.phpIndex = PhpIndex.getInstance(psiElement.getProject());
        this.repositoryInterface = getRepositoryInterface(phpIndex);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return resolveClassNames(text).stream()
            .flatMap(className -> phpIndex.getClassesByFQN(className).stream())
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    private Collection<String> resolveClassNames(String text) {
        final Set<String> names = new HashSet<>();
        names.add(text);

        if(text.contains(":")) {
            final String simpleName = PhpClassUtil.getSimpleName(text);
            final String namespaceShortcut = text.substring(0, text.indexOf(':'));
            final Collection<PhpClass> phpClasses = phpIndex.getClassesByName(simpleName);
            final PrefixMatcher matcher = new StrictCamelHumpMatcher(namespaceShortcut);

            for(PhpClass phpClass : phpClasses) {
                final String simplifiedNamespace = phpClass.getNamespaceName().replace("\\Bundle\\", "").replace("\\Entity\\", "").replace("\\", "");
                if(matcher.isStartMatch(simplifiedNamespace)) {
                    names.add(phpClass.getFQN());
                }
            }
        }

        return names;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        if(phpClass.getNamespacePart() != null) {
            return getVariantsFromBundles();
        } else {
            return getVariantsFromAnyNamespace();
        }
    }

    @NotNull
    private Object[] getVariantsFromBundles() {
        return getPhpClassesFrom(getBundlesNamespaces()).stream()
            .flatMap(phpClass -> {
                final int priority = getPriorityFor(phpClass);
                if(this.phpClass.allowDoctrineShortcutNotation()) {
                    return toStream(Entity.fromFqn(phpClass.getPresentableFQN()))
                        .map(entity -> getLookupElement(phpClass, priority, entity));
                } else {
                    return Stream.of(PrioritizedLookupElement.withPriority(new PhpClassLookupElement(phpClass, true, getPhpClassInsertHandler()), priority));
                }
            }).toArray();
    }

    private Collection<PhpClass> getPhpClassesFrom(Collection<BundleNamespace> bundleNamespaces) {
        return bundleNamespaces.stream()
            .flatMap(name -> phpIndex.getNamespacesByName(name.getName()).stream())
            .flatMap(this::getPhpClassesFrom)
            .filter(this::isClassFromBundle)
            .collect(Collectors.toSet());
    }

    private boolean isClassFromBundle(PhpClass phpClass) {
        final boolean isClass = !phpClass.isInterface() && !phpClass.isTrait();
        return isClass && !skippedClassNames.contains(phpClass.getFQN()) &&
            (!this.phpClass.getNamespacePart().equals("Entity") || isEntity(phpClass));
    }

    @Nullable
    private static PhpClass getRepositoryInterface(PhpIndex phpIndex) {
        final Collection<PhpClass> repositoryInterfaces = phpIndex.getInterfacesByFQN("\\Doctrine\\Common\\Persistence\\ObjectRepository");
        return repositoryInterfaces.isEmpty() ? null : repositoryInterfaces.iterator().next();
    }

    private boolean isEntity(PhpClass phpClass) {
        return (repositoryInterface == null || !isInstanceOf(phpClass, repositoryInterface)) && !phpClass.getName().endsWith("Manager");
    }

    private boolean isInstanceOf(PhpClass subjectClass, PhpClass expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), phpIndex);
    }

    private Stream<PhpClass> getPhpClassesFrom(PhpNamespace phpNamespace) {
        final Stream<PhpClass> phpClasses = PsiTreeUtil.getChildrenOfTypeAsList(phpNamespace.getStatements(), PhpClass.class).stream();
        final String namespaceName = phpNamespace.getFQN().toLowerCase() + "\\";

        return Stream.concat(
            phpClasses,
            phpIndex.getChildNamespacesByParentName(namespaceName).stream()
                .flatMap(childNamespaceName -> phpIndex.getNamespacesByName(namespaceName + childNamespaceName).stream())
                .flatMap(this::getPhpClassesFrom)
        );
    }

    private Collection<BundleNamespace> getBundlesNamespaces() {
        return new Bundles(phpIndex).findAll().stream()
            .map(bundle -> new BundleNamespace(bundle, this.phpClass.getNamespacePart()))
            .collect(Collectors.toList());
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
        Entity.fromFqn(phpClass.getPresentableFQN())
            .map(entity -> getLookupElement(phpClass, priority, entity))
            .ifPresent(results::add);
    }

    @NotNull
    private LookupElement getLookupElement(PhpClass phpClass, int priority, Entity entity) {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder.create(entity.getShortcutName())
                .withIcon(Icons.DOCTRINE)
                .withTypeText(phpClass.getPresentableFQN())
                .withInsertHandler(insertHandler)
                .withLookupString(StringUtil.trimLeading(phpClass.getFQN(), '\\')),
            priority
        );
    }

    @NotNull
    private Object[] getVariantsFromAnyNamespace() {
        return getAllPhpClassNames()
            .flatMap(className -> phpIndex.getClassesByName(className).stream())
            .filter(phpClass -> !PhpClassUtil.isTestOrGeneratedClass(phpClass.getPresentableFQN()))
            .map(phpClass -> new PhpClassLookupElement(phpClass, true, getPhpClassInsertHandler()))
            .toArray();
    }

    private InsertHandler<LookupElement> getPhpClassInsertHandler() {
        return insertHandler != null ?
                    new ComposedInsertHandler(asList(PhpClassInsertHandler.INSTANCE, insertHandler)) : PhpClassInsertHandler.INSTANCE;
    }

    private Stream<String> getAllPhpClassNames() {
        final PrefixMatcher classMatcher = new CamelHumpMatcher(text);
        return phpIndex.getAllClassNames(classMatcher).stream();
    }

}
