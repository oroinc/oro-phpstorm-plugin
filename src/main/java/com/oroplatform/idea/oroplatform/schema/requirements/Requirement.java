package com.oroplatform.idea.oroplatform.schema.requirements;

import java.util.List;

public interface Requirement {
    List<String> getErrors(String text);
}
