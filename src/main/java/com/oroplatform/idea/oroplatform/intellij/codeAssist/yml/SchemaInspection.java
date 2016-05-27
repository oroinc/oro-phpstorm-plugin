package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.Schema;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
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

        ProblemsHolder problems = new ProblemsHolder(manager, file, isOnTheFly);

        for(Schema schema : Schemas.ALL) {
            if(file.getVirtualFile().getPath().endsWith(schema.filePathPattern)) {
                Visitor visitor = new InspectionSchemaVisitor(problems, getMappingsFrom(file));
                schema.rootElement.accept(visitor);
            }
        }

        return problems.getResultsArray();
    }


}
