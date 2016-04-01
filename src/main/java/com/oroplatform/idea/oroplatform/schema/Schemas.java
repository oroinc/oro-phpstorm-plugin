package com.oroplatform.idea.oroplatform.schema;

import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Schemas {

    public static final Element ACL = new Container(Collections.singletonList(
        new Property(Pattern.compile(".+"),
            new OneOf(
                new Container(asList(
                    new Property("type", new Scalar(asList("entity"))),
                    new Property("label", new Scalar()).required(),
                    new Property("bindings", new Sequence(new Container(asList(
                        new Property("class", new Scalar(new Scalar.PhpClass(Scalar.PhpClass.Type.Controller))).required(),
                        new Property("method", new Scalar(new Scalar.PhpMethod("*Action"))).required()
                    )))),
                    new Property("class", new Scalar(new Scalar.PhpClass(Scalar.PhpClass.Type.Entity))),
                    new Property("permission", new Scalar(asList("VIEW", "EDIT", "CREATE", "DELETE"))).required(),
                    new Property("group_name", new Scalar())
                )),
                new Container(asList(
                    new Property("type", new Scalar(asList("action"))),
                    new Property("label", new Scalar(), true),
                    new Property("bindings", new Sequence(new Container(asList(
                        new Property("class", new Scalar(new Scalar.PhpClass(Scalar.PhpClass.Type.Controller))).required(),
                        new Property("method", new Scalar(new Scalar.PhpMethod("*Action"))).required()
                    ))))
                ))
            )
        )
    ));

}
