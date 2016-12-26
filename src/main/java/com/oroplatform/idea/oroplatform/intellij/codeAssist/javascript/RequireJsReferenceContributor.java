package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript;

import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.oroplatform.idea.oroplatform.StringWrapper;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements.fileInProjectWithPluginEnabled;

public class RequireJsReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<JSLiteralExpression> pattern =
            psiElement(JSLiteralExpression.class).inFile(fileInProjectWithPluginEnabled())
                .and(
                    psiElement().andOr(
                        psiElement()
                            .withSuperParent(2, functionCall("require")),
                        psiElement()
                            .withSuperParent(3, functionCall("define"))
                    )
                );

        registrar.registerReferenceProvider(pattern, new WrappedFileReferenceProvider(
            new BundleJsModuleWrappedStringFactory(),
            new BundleJsRootDirsFinder(),
            new RequireJsFilePathTransformer())
        );
    }

    private PsiElementPattern.Capture<JSCallExpression> functionCall(String name) {
        return psiElement(JSCallExpression.class).withChild(psiElement().withText(name));
    }

    private static class BundleJsModuleWrappedStringFactory implements StringWrapperProvider {
        private final StringWrapperProvider stringWrapperProvider = new PublicResourceWrappedStringFactory();

        @Override
        public StringWrapper getStringWrapperFor(PsiElement requestElement, VirtualFile sourceDir) {
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

        private final StringWrapperProvider stringWrapperProvider = new BundleJsModuleWrappedStringFactory();
        private final RootDirsFinder rootDirsFinder = new PublicResourcesRootDirsFinder();

        @Override
        public String referenceFilePath(PsiElement element, String text) {
            final Project project = element.getProject();
            final RequireJsConfig config = project.getComponent(RequireJsComponent.class).getRequireJsConfig();

            return config.getPathForAlias(text).map(t -> StringUtil.trimEnd(StringUtil.trimStart(t, "bundles/"), ".js")).orElse(text);
        }

        @Override
        public String variantLookupString(PsiElement element, String text) {
            final Project project = element.getProject();
            final RequireJsConfig config = project.getComponent(RequireJsComponent.class).getRequireJsConfig();

            final String moduleName = getModuleName(element).flatMap(name -> config.getPackageAliasFor(name, text)).orElse(text);

            return config.getAliasForPath("bundles/"+moduleName+".js").orElse(moduleName);
        }

        private Optional<String> getModuleName(PsiElement element) {
            final VirtualFile sourceDir = element.getContainingFile().getOriginalFile().getVirtualFile().getParent();
            final StringWrapper stringWrapper = stringWrapperProvider.getStringWrapperFor(element, sourceDir);
            final Optional<VirtualFile> rootDirs = rootDirsFinder.getRootDirs(element).stream().findFirst().flatMap(dir -> Optional.ofNullable(dir.findChild("js")));

            return rootDirs.map(dir -> element.getContainingFile().getOriginalFile().getVirtualFile().getPath().replace(dir.getPath() + "/", ""))
                .map(stringWrapper::addPrefixAndRemoveSuffix);
        }
    }
}
