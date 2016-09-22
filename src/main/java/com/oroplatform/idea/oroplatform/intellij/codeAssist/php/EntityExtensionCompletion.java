package com.oroplatform.idea.oroplatform.intellij.codeAssist.php;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.MemberReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.oroplatform.idea.oroplatform.Icons;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Entity;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class EntityExtensionCompletion extends CompletionContributor {
    public EntityExtensionCompletion() {
        final PsiElementPattern.Capture<PsiElement> capture = psiElement().afterLeaf(psiElement(PhpTokenTypes.ARROW));
        extend(CompletionType.BASIC, capture, new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                final PsiElement element = parameters.getPosition();

                if(!(element.getParent() instanceof MemberReference)) return;
                final MemberReference phpElement = (MemberReference) element.getParent();
                if(phpElement.getClassReference() == null) return;

                final PhpType type = phpElement.getClassReference().getType();

                final Project project = element.getProject();
                final VirtualFile appDir = getProjectRoot(project).findFileByRelativePath(OroPlatformSettings.getInstance(project).getAppDir());

                if(appDir == null) return;
                final String extensionsPath = appDir.getPath() + "/cache/dev/oro_entities/Extend/Entity";
                if(!excluded(project, extensionsPath)) return;

                final EntityExtensions extensions = EntityExtensions.instance(appDir.getFileSystem(), extensionsPath);
                final Entities entities = Entities.instance(project);

                for (String t : type.getTypes()) {
                    for (Entity entity : entities.findEntities(t)) {
                        for (EntityExtensions.ExtensionMethod extensionMethod : extensions.getMethods(entity)) {
                            result.addElement(LookupElementBuilder.create(extensionMethod.name+"()").withIcon(Icons.PUBLIC_METHOD));
                        }
                    }
                }
            }

            private boolean excluded(Project project, String path) {
                for (Module module : ModuleManager.getInstance(project).getModules()) {
                    for (VirtualFile excluded : ModuleRootManager.getInstance(module).getExcludeRoots()) {
                        if(path.startsWith(excluded.getPath())) return true;
                    }
                }
                return false;
            }

            private VirtualFile getProjectRoot(Project project) {
                return ApplicationManager.getApplication().isUnitTestMode() ?
                    VirtualFileManager.getInstance().findFileByUrl("temp:///").findChild("src") : project.getBaseDir();
            }
        });
    }
}
