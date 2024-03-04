package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.StubIndexKey;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

class PhpMethodLookupElement extends PhpLookupElement {
    private static final StubIndexKey<String, Method> INDEX_KEY = StubIndexKey.createIndexKey("com.oroplatform.idea.oroplatform.phpMethod");

    @NotNull
    private final Method method;

    PhpMethodLookupElement(@NotNull Method method) {
        super(method); // OPP-75: there's no default constructor, so choosing the least problematic solution
        this.method = method;
        this.lookupString = getFQN(method); // TODO FQN should come ideally from method itself, check why it's not the case
    }

    private static String getFQN(Method method) {
        if(method.getContainingClass() == null) return method.getName();

        return method.getContainingClass().getPresentableFQN()+"::"+method.getName();
    }

    @Override
    public void renderElement(@NotNull LookupElementPresentation presentation) {
        super.renderElement(presentation);

        final Collection<String> parameters = new LinkedList<>();
        for (Parameter parameter : method.getParameters()) {
            parameters.add(parameter.getType().toStringResolved());
        }

        presentation.setItemText(method.getName() + "(" + StringUtil.join(parameters, ", ") + ")");
    }
}
