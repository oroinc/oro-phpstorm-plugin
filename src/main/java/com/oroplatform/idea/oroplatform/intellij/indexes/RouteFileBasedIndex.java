package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import com.oroplatform.idea.oroplatform.symfony.Route;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class RouteFileBasedIndex extends FileBasedIndexExtension<String, Route> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    private final DataExternalizer<Route> valueExternalizer = new RouteExternalizer();
    public static final ID<String, Route> KEY = ID.create("com.oroplatform.idea.oroplatform.routes");

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getPath().contains("/cache/dev/appDevUrlGenerator.php");
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID<String, Route> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Route, FileContent> getIndexer() {
        return new DataIndexer<String, Route, FileContent>() {
            @NotNull
            @Override
            public Map<String, Route> map(@NotNull FileContent inputData) {
                final THashMap<String, Route> index = new THashMap<String, Route>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                final PhpFile file = (PhpFile) inputData.getPsiFile();

                //TODO: refactoring
                for (PhpNamedElement element : file.getTopLevelDefs().values()) {
                    if(element instanceof PhpClass) {
                        final Collection<Field> fields = ((PhpClass) element).getOwnFieldMap().get("declaredRoutes");
                        for (Field field : fields) {
                            final PsiElement value = field.getDefaultValue();
                            if(value != null && value instanceof ArrayCreationExpression) {
                                final Iterable<ArrayHashElement> hashes = ((ArrayCreationExpression) value).getHashElements();
                                for (ArrayHashElement hash : hashes) {
                                    if(hash.getKey() != null) {
                                        final String name = StringUtil.stripQuotesAroundValue(hash.getKey().getText());
                                        if(!name.isEmpty()) {
                                            index.put(name, new Route("", ""));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return index;
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Route> getValueExternalizer() {
        return valueExternalizer;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
