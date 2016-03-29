package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import com.oroplatform.idea.oroplatform.schema.Schemas;
import com.oroplatform.idea.oroplatform.schema.Visitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLPsiElement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

public class AclInspection extends LocalInspectionTool {
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        List<ProblemDescriptor> collectedProblems = new LinkedList<ProblemDescriptor>();
        List<YAMLPsiElement> elements = file.getFirstChild() instanceof YAMLPsiElement ? Collections.singletonList((YAMLPsiElement) file.getFirstChild()) : Collections.<YAMLPsiElement>emptyList();
        Visitor visitor = new InspectionSchemaVisitor(collectedProblems, elements, psiElement(YAMLDocument.class).inFile(psiFile().withName("acl.yml").withLanguage(YAMLLanguage.INSTANCE)));
        Schemas.ACL.accept(visitor);

        return collectedProblems.toArray(new ProblemDescriptor[collectedProblems.size()]);
    }
}
