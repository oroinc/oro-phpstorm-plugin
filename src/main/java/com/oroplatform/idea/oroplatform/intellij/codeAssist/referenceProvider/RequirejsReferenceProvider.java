package com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceProvider;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.oroplatform.idea.oroplatform.StringWrapper;
import com.oroplatform.idea.oroplatform.intellij.ExtensionFileFilter;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.*;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript.RequireJsComponent;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript.RequireJsConfig;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript.RequireJsInterface;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript.RequireJsService;
import com.oroplatform.idea.oroplatform.util.SubclassCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class RequirejsReferenceProvider {
    public static PsiReferenceProvider instance() {
        return new WrappedFileReferenceProvider(
            new BundleJsModuleWrappedStringFactory(),
            new BundleJsRootDirsFinder(),
            new ExtensionFileFilter("js"),
            new RequireJsFilePathTransformer()
        );
    }

    private static class BundleJsModuleWrappedStringFactory implements StringWrapperProvider {
        private final StringWrapperProvider stringWrapperProvider = new PublicResourceWrappedStringFactory();

        @Override
        public StringWrapper getStringWrapperFor(@NotNull PsiElement requestElement, @NotNull VirtualFile sourceDir) {
            return Optional.ofNullable(PsiManager.getInstance(requestElement.getProject()).findDirectory(sourceDir))
                //dir is used for StringWrapper building, because in this case not important is from which file completion
                //is triggered (requestElement) as in cases assets in config files, but source dir of completed element (eg. js module).
                .map(dir -> stringWrapperProvider.getStringWrapperFor(dir, sourceDir))
                .map(wrapper -> wrapper
                    .mapPrefix(prefix -> prefix.replace("bundles/", "")+"js/")
                    .mapSuffix(suffix -> ".js")
                ).orElse(new StringWrapper("", ""));
        }
    }

    private static class RequireJsFilePathTransformer implements WrappedFileReferenceProvider.FilePathTransformer {

        private final StringWrapperProvider stringWrapperProvider = new RequirejsReferenceProvider.BundleJsModuleWrappedStringFactory();
        private final RootDirsFinder rootDirsFinder = new PublicResourcesRootDirsFinder();

        @Override
        public String referenceFilePath(PsiElement element, String text) {
            final Project project = element.getProject();
            final RequireJsConfig config = RequireJsService.getInstance(project).getRequireJsConfig();

            return config.getPathForAlias(text).map(t -> StringUtil.trimEnd(StringUtil.trimStart(t, "bundles/"), ".js"))
                    .or(() -> getModuleName(element).flatMap(name -> config.getPackageForAlias(name, text)))
                    .orElse(text);
        }

        @Override
        public String variantLookupString(PsiElement element, String text) {
            final Project project = element.getProject();
            final RequireJsConfig config = RequireJsService.getInstance(project).getRequireJsConfig();;

            return config.getAliasForPath("bundles/" + text + ".js")
                    .or(() -> getModuleName(element).flatMap(name -> config.getPackageAliasFor(name, text)))
                    .orElse(text);
        }

        private Optional<String> getModuleName(PsiElement element) {
            final VirtualFile sourceDir = element.getContainingFile().getOriginalFile().getVirtualFile().getParent();
            if(sourceDir == null) return Optional.empty();

            final StringWrapper stringWrapper = stringWrapperProvider.getStringWrapperFor(element, sourceDir);
            final Optional<VirtualFile> rootDirs = rootDirsFinder.getRootDirs(element).stream().findFirst().flatMap(dir -> Optional.ofNullable(dir.findChild("js")));

            return rootDirs.map(dir -> element.getContainingFile().getOriginalFile().getVirtualFile().getPath().replace(dir.getPath() + "/", ""))
                .map(stringWrapper::addPrefixAndRemoveSuffix);
        }
    }

    private static class BundleJsRootDirsFinder implements RootDirsFinder {
        public Collection<VirtualFile> getRootDirs(PsiElement element) {
            return getBundleClasses(element.getProject()).stream()
                .flatMap(phpClass -> toStream(phpClass.getContainingFile().getVirtualFile().getParent()))
                .flatMap(file -> toStream(VfsUtil.findRelativeFile(file, "Resources", "public", "js")))
                .collect(Collectors.toList());
        }

        private Collection<PhpClass> getBundleClasses(Project project) {
            final PhpIndex phpIndex = PhpIndex.getInstance(project);
            SubclassCollector subclassCollector = new SubclassCollector(phpIndex);

            return subclassCollector.getAllSubclasses("\\Symfony\\Component\\HttpKernel\\Bundle\\Bundle");
        }
    }
}
