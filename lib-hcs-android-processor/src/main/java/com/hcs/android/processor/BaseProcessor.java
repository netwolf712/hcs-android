
package com.hcs.android.processor;

import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.annotation.annotation.HcsApp;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;
public abstract class BaseProcessor extends AbstractProcessor {

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        Set<Class<? extends Annotation>> classSet = new HashSet<>();
        addAnnotation(classSet);

        Set<String> nameSet = new HashSet<>();
        for (Class<? extends Annotation> clazz : classSet) {
            nameSet.add(clazz.getCanonicalName());
        }
        return nameSet;
    }
    protected abstract void addAnnotation(Set<Class<? extends Annotation>> classSet);

}