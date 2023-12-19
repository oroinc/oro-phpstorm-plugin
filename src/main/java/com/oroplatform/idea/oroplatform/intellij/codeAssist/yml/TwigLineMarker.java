package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.twig.TwigFile;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.intellij.indexes.LayoutUpdateThemesIndex;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Bundle;
import com.oroplatform.idea.oroplatform.symfony.Bundles;
import com.oroplatform.idea.oroplatform.symfony.Resource;
import com.oroplatform.idea.oroplatform.symfony.TwigTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oroplatform.idea.oroplatform.Functions.toStream;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.elementFilter;

public class TwigLineMarker implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if(elements.isEmpty() || !OroPlatformSettings.getInstance(elements.get(0).getProject()).isPluginEnabled()) {
            return;
        }

        elements.stream()
            .flatMap(elementFilter(TwigFile.class))
            .filter(twigFile -> twigFile.getVirtualFile() != null)
            .flatMap(twigFile -> {
                final Project project = twigFile.getProject();
                final Bundles bundles = new Bundles(PhpIndex.getInstance(project));
                final LayoutUpdateThemesIndex index = LayoutUpdateThemesIndex.instance(project);
                final Predicate<VirtualFile> hasCommonAncestor = file -> {
                    final Optional<String> maybeFilePath = Optional.ofNullable(file.getParent()).map(VirtualFile::getPath);
                    final Optional<String> maybeTemplateParentPath = getParentVirtualFile(twigFile).map(VirtualFile::getPath);
                    return maybeTemplateParentPath.flatMap(templateParentPath -> maybeFilePath.map(templateParentPath::startsWith)).orElse(false);
                };

                final Collection<VirtualFile> layoutUpdates = Stream.concat(
                    index.findLayoutUpdates(twigFile.getName()).stream().filter(hasCommonAncestor),
                    getTwigTemplateAbsoluteNames(twigFile, bundles).flatMap(name -> index.findLayoutUpdates(name).stream())
                ).toList();

                if(layoutUpdates.isEmpty()) {
                    return Stream.empty();
                } else {
                    return Stream.of(
                        NavigationGutterIconBuilder.create(Icons.ORO)
                                .setTargets(layoutUpdates.stream()
                                    .flatMap(file -> toStream(PsiManager.getInstance(project).findFile(file)))
                                    .flatMap(elementFilter(YAMLFile.class))
                                    .flatMap(file -> ReferencesSearch.search(twigFile, GlobalSearchScope.fileScope(file)).findAll().stream())
                                    .map(ref -> ref.getElement())
                                    .collect(Collectors.toList())
                                )
                        .setTooltipText(OroPlatformBundle.message("gutter.navigateToLayout"))
                        .createLineMarkerInfo(twigFile)
                    );
                }


            })
            .forEach(result::add);
    }

    private Optional<VirtualFile> getParentVirtualFile(PsiFile file) {
        return Optional.ofNullable(file.getVirtualFile()).map(VirtualFile::getParent);
    }

    private Stream<String> getTwigTemplateAbsoluteNames(TwigFile twigFile, Bundles bundles) {
        return bundles.findBundleClasses().stream()
            .filter(phpClass -> twigFile.getVirtualFile().getPath().startsWith(phpClass.getContainingFile().getContainingDirectory().getVirtualFile().getPath()))
            .map(phpClass -> new Bundle(phpClass.getNamespaceName()))
            .map(bundle -> new Resource(bundle, twigFile.getVirtualFile().getPath().replaceFirst(".*/Resources/", "")))
            .flatMap(resource -> toStream(TwigTemplate.from(resource)))
            .map(TwigTemplate::getName);
    }
}
