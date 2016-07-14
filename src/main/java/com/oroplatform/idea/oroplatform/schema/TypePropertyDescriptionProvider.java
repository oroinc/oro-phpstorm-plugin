package com.oroplatform.idea.oroplatform.schema;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class TypePropertyDescriptionProvider implements PropertyDescriptionProvider {
    @Nullable
    @Override
    public String getDescription(@NotNull Property property) {
        return getType(property.getValueElement());
    }

    private String getType(@NotNull Element element) {
        if(element instanceof Container) {
            return "object";
        } else if(element instanceof Sequence) {
            return getType(((Sequence) element).getType()) + "[]";
        } else if(element instanceof Scalar) {
            if(element.equals(Scalars.bool)) {
                return "boolean";
            } else if(element.equals(Scalars.integer)) {
                return "integer";
            } else {
                return "string";
            }
        } else if(element instanceof OneOf) {
            final Set<String> types = new LinkedHashSet<String>();
            for (Element e : ((OneOf) element).getElements()) {
                String type = getType(e);
                if(type != null) {
                    types.add(type);
                }
            }

            return StringUtil.join(types, " or ");
        } else if(element instanceof Repeated) {
            return getType(((Repeated) element).getElement());
        }

        return null;
    }
}
