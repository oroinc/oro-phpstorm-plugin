package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getMappingsFrom;

public class SchemaInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if(!OroPlatformSettings.getInstance(file.getProject()).isPluginEnabled()) {
            return new ProblemDescriptor[0];
        }

        final Errors errors = new Errors();
        final InspectionSchemaVisitor visitor = new InspectionSchemaVisitor(errors, getMappingsFrom(file));

        Schemas.ALL.stream()
            .filter(schema -> schema.fileMatcher.matches(file))
            .forEach(schema -> schema.rootElement.accept(visitor));

        final ProblemsHolder problems = new ProblemsHolder(manager, file, isOnTheFly);
        //TODO: collector?
        errors.getErrors().forEach(error -> problems.registerProblem(error.element, error.message));

        return problems.getResultsArray();
    }

    public static class Errors {
        private final List<Error> errors = new LinkedList<>();
        private final List<Error> unmodifiableErrors = Collections.unmodifiableList(errors);

        public void add(PsiElement element, String message, int depth) {
            errors.add(new Error(element, message, depth));
        }

        public void add(Error error) {
            errors.add(error);
        }

        List<Error> getErrors() {
            return unmodifiableErrors;
        }
    }

    public static class Error {
        public final PsiElement element;
        public final String message;
        public final int depth;

        Error(PsiElement element, String message, int depth) {
            this.element = element;
            this.message = message;
            this.depth = depth;
        }
    }
}
