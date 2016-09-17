package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.oroplatform.idea.oroplatform.schema.PropertyPath;

public interface PhpClassProviders {
    PhpClassProvider directProvider();
    PhpClassProvider fieldTypeProvider(PropertyPath classPropertyPath);
}
