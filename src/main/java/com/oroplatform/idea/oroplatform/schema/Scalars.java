package com.oroplatform.idea.oroplatform.schema;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RootDirFinder;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RelativeToAppDirectoryResolver;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.referenceProvider.RelativeToElementResolver;
import com.oroplatform.idea.oroplatform.schema.requirements.ChoicesRequirement;
import com.oroplatform.idea.oroplatform.schema.requirements.PatternRequirement;
import com.oroplatform.idea.oroplatform.schema.requirements.Requirement;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

final class Scalars {
    private Scalars() {}

    static Scalar strictChoices(final String... choices) {
        return choices(Collections.singletonList(new ChoicesRequirement(Arrays.asList(choices))), choices);
    }

    private static Scalar choices(Collection<? extends Requirement> requirements, final String[] choices) {
        return new Scalar(requirements) {
            @Override
            public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.choices(Arrays.asList(choices), insertHandler);
            }
        };
    }

    static Scalar choices(final String... choices) {
        return choices(Collections.<Requirement>emptyList(), choices);
    }

    static Scalar propertiesFromPath(final PropertyPath path, final String prefix) {
        return new Scalar() {
            @Override
            public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.propertiesFromPath(path, prefix, insertHandler);
            }
        };
    }

    static Scalar propertiesFromPath(PropertyPath path) {
        return propertiesFromPath(path, "");
    }

    static Scalar phpMethod(final String pattern) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpMethod(pattern, insertHandler);
            }
        };
    }

    static Scalar regexp(String pattern) {
        return new Scalar(Collections.singletonList(new PatternRequirement(Pattern.compile(pattern))));
    }

    final static Scalar condition = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.condition(insertHandler);
        }
    };

    final static Scalar action = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.action(insertHandler);
        }
    };

    final static Scalar service = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.service(insertHandler);
        }
    };

    final static Scalar massActionProvider = new Scalar() {
        @Nullable
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.massActionProvider(insertHandler);
        }
    };

    final static Scalar formType = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.formType(insertHandler);
        }
    };

    final static Scalar datagrid = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.datagrid(insertHandler);
        }
    };

    final static Scalar operation = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.operation(insertHandler);
        }
    };

    final static Scalar acl = new Scalar() {
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.acl(insertHandler);
        }
    };

    final static Scalar filePath = new Scalar() {
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.filePath(new RelativeToElementResolver(), insertHandler);
        }
    };

    static Scalar filePathRelativeToElementIn(final String dir, final int allowedDepth) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.filePath(new RelativeToElementResolver(dir), allowedDepth, insertHandler);
            }
        };
    }

    static Scalar filePathRelativeToAppIn(final String dir) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.filePath(new RelativeToAppDirectoryResolver(dir), insertHandler);
            }
        };
    }

    static Scalar file(final RootDirFinder rootDirFinder) {
        return new Scalar() {
            @Nullable
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.file(rootDirFinder, new ExtensionFileFilter("css", "less", "sass"), insertHandler);
            }
        };
    }

    final static Scalar twig = new Scalar() {
        @Nullable
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.twigTemplate(insertHandler);
        }
    };

    final static Scalar route = new Scalar() {
        @Nullable
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.route(insertHandler);
        }
    };

    final static Scalar trans = new Scalar() {
        @Nullable
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.translation(insertHandler);
        }
    };

    final static Element transDomain = new Scalar() {
        @Nullable
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.translationDomain(insertHandler);
        }
    };

    final static Scalar any = new Scalar();

    final static Scalar fullEntity = phpClass(PhpClass.entity(false));

    final static Scalar entity = phpClass(PhpClass.entity(true));

    final static Scalar controller = phpClass(PhpClass.controller());

    final static Scalar phpClass = phpClass(PhpClass.any());

    final static Scalar callable = new Scalar() {
        @Nullable
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.phpCallable(insertHandler);
        }
    };

    private static Scalar phpClass(final PhpClass clazz) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpClass(clazz, insertHandler);
            }
        };
    }

    final static Scalar phpCallback = new Scalar() {
        @Override
        public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.phpCallback(insertHandler);
        }
    };

    static Scalar field(final PropertyPath classPropertyPath) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpField(classPropertyPath, providers.phpClassProviders().directProvider(), insertHandler);
            }
        };
    }

    static Scalar fieldOfFieldTypeClass(final PropertyPath classPropertyPath, final PropertyPath fieldPropertyPath) {
        return new Scalar() {
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.phpField(fieldPropertyPath, providers.phpClassProviders().fieldTypeProvider(classPropertyPath), insertHandler);
            }
        };
    }

    final static Scalar bool = strictChoices("true", "false");

    final static Scalar integer = regexp("^-?\\d+$");
}
