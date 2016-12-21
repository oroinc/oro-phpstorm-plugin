package com.oroplatform.idea.oroplatform.schema;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiReferenceProvider;
import com.oroplatform.idea.oroplatform.StringWrapper;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.*;
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
        return choices(Collections.singletonList(new ChoicesRequirement(Arrays.asList(choices))), new StaticChoicesProvider(choices));
    }

    private static Scalar choices(Collection<? extends Requirement> requirements, final ChoicesProvider choicesProvider) {
        return new Scalar(requirements) {
            @Override
            public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
                return providers.choices(choicesProvider, insertHandler);
            }
        };
    }

    static Scalar choices(final String... choices) {
        return choices(Collections.<Requirement>emptyList(), new StaticChoicesProvider(choices));
    }

    static Scalar choices(final ChoicesProvider choicesProvider) {
        return choices(Collections.<Requirement>emptyList(), choicesProvider);
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

    final static Scalar assetsFilter = new Scalar() {
        @Nullable
        @Override
        public CompletionProvider<CompletionParameters> getProvider(CompletionProviders providers, InsertHandler<LookupElement> insertHandler) {
            return providers.assetsFilter(insertHandler);
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

    static Scalar fileRelativeToAppIn(final String dir, String... extensions) {
        return file(new AppRelativeRootDirFinder(dir), new StaticStringWrapperProvider(new StringWrapper("", "")), extensions);
    }

    static Scalar file(final RootDirFinder rootDirFinder, final String... extensions) {
        return file(rootDirFinder, new PublicResourceWrappedStringFactory(), extensions);
    }

    private static Scalar file(final RootDirFinder rootDirFinder, final StringWrapperProvider stringWrapperProvider, final String... extensions) {
        return new Scalar() {
            @Nullable
            @Override
            public PsiReferenceProvider getProvider(ReferenceProviders providers, InsertHandler<LookupElement> insertHandler) {
                final VirtualFileFilter fileFilter = extensions.length > 0 ? new ExtensionFileFilter(extensions) : VirtualFileFilter.ALL;
                return providers.file(rootDirFinder, stringWrapperProvider, fileFilter, insertHandler);
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

    final static Scalar any = any(null);

    static Scalar any(@Nullable DefaultValueDescriptor defaultValueDescriptor) {
        return new Scalar(Collections.emptyList(), defaultValueDescriptor);
    }

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
