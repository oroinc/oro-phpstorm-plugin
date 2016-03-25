package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Schemas {

    public static final Element ACL = new Container(Collections.singletonList(
        new Property(Pattern.compile(".+"),
            new Container(asList(
                new Property("type", new Literal(asList("entity", "action"))),
                new Property("class", new Literal(new Literal.PhpClass())),
                new Property("permission", new Literal(asList("VIEW", "EDIT", "CREATE", "DELETE"))),
                new Property("label", new Literal()),
                new Property("group_name", new Literal()),
                new Property("bindings", new Array(new Container(asList(
                    new Property("class", new Literal()),
                    new Property("method", new Literal())
                ))))
            ))
        )
    ));

}
