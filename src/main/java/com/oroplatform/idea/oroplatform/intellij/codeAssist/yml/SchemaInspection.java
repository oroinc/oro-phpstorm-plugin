package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.getMappingsFrom;

public class SchemaInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if(!OroPlatformSettings.getInstance(file.getProject()).isPluginEnabled()) {
            return new ProblemDescriptor[0];
        }

        final ProblemsHolder problems = new ProblemsHolder(manager, file, isOnTheFly);
        final InspectionSchemaVisitor visitor = new InspectionSchemaVisitor(problems, getMappingsFrom(file));

        Schemas.ALL.stream()
            .filter(schema -> schema.fileMatcher.matches(file))
            .forEach(schema -> schema.rootElement.accept(visitor));

        return problems.getResultsArray();
    }
}
