package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.jetbrains.yaml.psi.YAMLScalar;

import static com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements.*;

public class ElementByPathReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final PropertyPath path;
    private final String prefix;
    private final String elementName;
    private final InsertHandler<LookupElement> insertHandler;

    public ElementByPathReference(PsiElement element, PropertyPath path, String prefix, String elementName, InsertHandler<LookupElement> insertHandler) {
        super(element);
        this.path = path;
        this.prefix = prefix;
        this.elementName = StringUtil.trimStart(elementName, prefix);
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return getElementsByPath(path, myElement).stream()
            .filter(element -> elementName.equals(getElementName(element)))
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    private static String getElementName(YAMLPsiElement element) {
        if(element instanceof YAMLScalar) {
            return ((YAMLScalar) element).getTextValue();
        } else if(element instanceof YAMLKeyValue) {
            return ((YAMLKeyValue) element).getKeyText();
        } else {
            return element.getText();
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getPropertyFrom(path, myElement).stream()
            .map(property -> LookupElementBuilder.create(prefix + property).withInsertHandler(insertHandler))
            .toArray();
    }
}
