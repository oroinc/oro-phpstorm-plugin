package com.oroplatform.idea.oroplatform.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class Schemas {

    public static final Collection<Schema> ALL = asList(
        new Schema("acl.yml", new Container(Collections.singletonList(
            new Property(Pattern.compile(".+"),
                new OneOf(
                    new Container(asList(
                        new Property("type", new Scalar(asList("entity"))),
                        new Property("bindings", new Sequence(new Container(asList(
                            new Property("class", new Scalar(new Scalar.PhpClass(Scalar.PhpClass.Type.Controller))).required(),
                            new Property("method", new Scalar(new Scalar.PhpMethod("*Action"))).required()
                        )))),
                        new Property("class", new Scalar(new Scalar.PhpClass(Scalar.PhpClass.Type.Entity))).required(),
                        new Property("permission", new Scalar(asList("VIEW", "EDIT", "CREATE", "DELETE"))).required(),
                        new Property("group_name", new Scalar())
                    )),
                    new Container(asList(
                        new Property("type", new Scalar(asList("action"))),
                        new Property("label", new Scalar(), true).required(),
                        new Property("bindings", new Sequence(new Container(asList(
                            //define as regular scalars in order to avoid double suggestions
                            new Property("class", new Scalar()).required(),
                            new Property("method", new Scalar()).required()
                        ))))
                    ))
                )
            )
        )))
    );

}
