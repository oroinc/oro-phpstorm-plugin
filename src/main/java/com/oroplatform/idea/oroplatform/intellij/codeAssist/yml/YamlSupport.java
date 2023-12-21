package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.List;

import static java.util.Arrays.asList;

public class YamlSupport implements ProjectActivity, Disposable {
    private final List<? extends ManipulatorItem<? extends PsiElement>> manipulators = asList(
        new ManipulatorItem<>(YAMLKeyValue.class, new YamlKeyValueManipulator()),
        new ManipulatorItem<>(LeafPsiElement.class, new LeafPsiElementManipulator())
    );

    @Override
    public void dispose() {
        for (ManipulatorItem<? extends PsiElement> manipulator : manipulators) {
            if(!ElementManipulators.INSTANCE.forKey(manipulator.key).isEmpty()) {
                ElementManipulators.INSTANCE.removeExplicitExtension(manipulator.key, manipulator.manipulator);
            }
        }
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        for (ManipulatorItem<? extends PsiElement> manipulator : manipulators) {
            if(ElementManipulators.INSTANCE.forKey(manipulator.key).isEmpty()) {
                ElementManipulators.INSTANCE.addExplicitExtension(manipulator.key, manipulator.manipulator);
            }
        }
        return continuation;
    }

    private static class ManipulatorItem<T extends PsiElement> {
        final Class<T> key;
        final ElementManipulator<T> manipulator;

        private ManipulatorItem(Class<T> key, ElementManipulator<T> manipulator) {
            this.key = key;
            this.manipulator = manipulator;
        }
    }
}
