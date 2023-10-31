package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.MemberReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class EntityExtensionCompletion extends CompletionContributor {
    public EntityExtensionCompletion() {
        final PsiElementPattern.Capture<PsiElement> capture = psiElement().afterLeaf(psiElement(PhpTokenTypes.ARROW));
        extend(CompletionType.BASIC, capture, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                final PsiElement element = parameters.getPosition();
                final Project project = element.getProject();
                final OroPlatformSettings settings = OroPlatformSettings.getInstance(project);

                if(!settings.isPluginEnabled()) return;

                final PhpType type = getPhpType(element);
                if (type == null) return;

                final VirtualFile appDir = settings.getAppVirtualDir();
                if (appDir == null) return;

                final String extensionsPath = appDir.getPath() + "/" + EntityExtensions.EXTENSIONS_DIR_RELATIVE_PATH;
                if(!excluded(project, extensionsPath)) return;

                final EntityExtensions extensions = EntityExtensions.instance(project);
                final Entities entities = Entities.instance(project);

                type.getTypes().stream()
                    .flatMap(t -> entities.findEntities(t).stream())
                    .flatMap(entity -> extensions.getMethods(entity).stream())
                    .map(method -> LookupElementBuilder.create(method.name+"()").withIcon(Icons.PUBLIC_METHOD))
                    .forEach(result::addElement);
            }

            private boolean excluded(Project project, String path) {
                List<String> excludedRootUrls = Arrays.stream(ModuleManager.getInstance(project).getModules())
                        .flatMap(module -> Arrays.stream(ModuleRootManager.getInstance(module).getExcludeRootUrls()))
                        .map(url -> url.replaceAll(".+://", ""))
                        .toList();
                return Arrays.stream(ModuleManager.getInstance(project).getModules())
                    .flatMap(module -> Arrays.stream(ModuleRootManager.getInstance(module).getExcludeRootUrls()))
                    .map(url -> url.replaceAll(".+://", ""))
                    .anyMatch(path::startsWith);
            }

            @Nullable
            private PhpType getPhpType(PsiElement element) {
                if(!(element.getParent() instanceof MemberReference)) return null;
                final MemberReference phpElement = (MemberReference) element.getParent();
                if(phpElement.getClassReference() == null) return null;

                return phpElement.getClassReference().getType();
            }
        });
    }
}
