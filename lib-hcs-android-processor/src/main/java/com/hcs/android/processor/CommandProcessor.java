/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcs.android.processor;

import com.google.auto.service.AutoService;
import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.annotation.annotation.HcsApp;
import com.hcs.android.processor.constant.Constants;
import com.hcs.android.processor.util.CollectionUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.hcs.android.processor.util.Logger;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class CommandProcessor extends BaseProcessor{

    /**
     * 类名是固定的
     */
    private final static String CLASS_NAME = "CommandManagerImpl";
    private Filer mFiler;
    private Elements mElements;
    private Logger mLog;

    private TypeName mCommandManagerInterface;
    private TypeName mHandlerType;
    private TypeName mStringUtils;

    private TypeName mMappingList;

    private TypeName mString = TypeName.get(String.class);
    private TypeName mObject = TypeName.get(Object.class);



    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mLog = new Logger(processingEnv.getMessager());

        mStringUtils = TypeName.get(mElements.getTypeElement(Constants.STRING_UTIL_TYPE).asType());
        mCommandManagerInterface = TypeName.get(mElements.getTypeElement(Constants.COMMAND_MANAGER_TYPE).asType());
        mHandlerType = TypeName.get(mElements.getTypeElement(Constants.HANDLER_TYPE).asType());
        mMappingList = ParameterizedTypeName.get(ClassName.get(Map.class), mString, mHandlerType);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) {
            return false;
        }

        Set<? extends Element> appSet = roundEnv.getElementsAnnotatedWith(HcsApp.class);
        //String registerPackageName = getRegisterPackageName(appSet);
        String registerPackageName = getPackageName(appSet.iterator().next()).getQualifiedName().toString();
        Map<TypeElement, List<ExecutableElement>> commands = new HashMap<>();
        findMapping(roundEnv.getElementsAnnotatedWith(CommandId.class), commands);
        if (!commands.isEmpty()) {
            createHandlerAdapter(registerPackageName,commands);
        }
        return true;
    }

    private void findMapping(Set<? extends Element> set, Map<TypeElement, List<ExecutableElement>> controllerMap) {
        for (Element element : set) {
            if (element instanceof ExecutableElement) {
                ExecutableElement execute = (ExecutableElement) element;
                Element enclosing = element.getEnclosingElement();
                if (!(enclosing instanceof TypeElement)) {
                    continue;
                }

                TypeElement type = (TypeElement) enclosing;
                Annotation commandMap = type.getAnnotation(CommandMapping.class);
                if ( commandMap == null) {
                    mLog.w(String.format("CommandMapping annotations may be missing on %s.",
                        type.getQualifiedName()));
                    continue;
                }

                String host = type.getQualifiedName() + "#" + execute.getSimpleName() + "()";

                Set<Modifier> modifiers = execute.getModifiers();
                Validate.isTrue(!modifiers.contains(Modifier.PRIVATE), "The modifier private is redundant on %s.",
                    host);

                if (modifiers.contains(Modifier.STATIC)) {
                    mLog.w(String.format("The modifier static is redundant on %s.", host));
                }

                List<ExecutableElement> elementList = controllerMap.get(type);
                if (CollectionUtils.isEmpty(elementList)) {
                    elementList = new ArrayList<>();
                    controllerMap.put(type, elementList);
                }
                elementList.add(execute);
            }
        }
    }

    private void createHandlerAdapter(String managerPackageName,Map<TypeElement, List<ExecutableElement>> controllers) {
        //String managerPackageName = Constants.COMMAND_MANAGER_PACKAGE;
        String className = CLASS_NAME;
        //成员变量
        FieldSpec mappingField = FieldSpec.builder(mMappingList, "mHandlerMap", Modifier.PRIVATE).build();
        CodeBlock.Builder rootCode = CodeBlock.builder()
                .addStatement("this.mHandlerMap = new $T<>()", LinkedHashMap.class);
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : controllers.entrySet()) {
            TypeElement type = entry.getKey();
            mLog.i(String.format("------ Processing %s ------", type.getSimpleName()));
            List<ExecutableElement> executes = entry.getValue();
            for (ExecutableElement execute : executes) {
                String commandId = execute.getAnnotation(CommandId.class).value();
                String handlerName = createHandler(type, execute);
                mLog.i("create handlerName " + handlerName);
                mLog.i("get commandId " + commandId);
                rootCode.addStatement("try{\n  Class<?> clazz = Class.forName(\"$L\");\n  IHandler handler = (IHandler)clazz.newInstance();\n  mHandlerMap.put(\"$L\", handler); \n} catch (Exception e){\n" +
                        "  e.printStackTrace();\n" +
                        "}",handlerName,commandId);
            }

        }
        //构造函数
        MethodSpec rootMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(rootCode.build())
                .build();

        //实体化函数
        MethodSpec doCommandMethod = MethodSpec.methodBuilder("handleCommand")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mString,"commandId")
                .addParameter(mObject,"commandData")
                .returns(mObject)
                .addStatement("return mHandlerMap.get(commandId) == null ? null : mHandlerMap.get(commandId).handle(commandData)")
                .build();
        TypeSpec adapterClass = TypeSpec.classBuilder(className)
                .addJavadoc(Constants.DOC_EDIT_WARN)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(mCommandManagerInterface)
                .addMethod(rootMethod)
                .addMethod(doCommandMethod)
                .addField(mappingField)
                .build();

        JavaFile javaFile = JavaFile.builder(managerPackageName, adapterClass).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Create a handler class and return the simple name of the handler.
     *
     * @return the simple name, such as the simple name of the class {@code com.example.User} is {@code User}.
     */
    private String createHandler(TypeElement type, ExecutableElement execute) {
        FieldSpec hostField = FieldSpec.builder(Object.class, "mHost").addModifiers(Modifier.PRIVATE).build();
        String packageName = getPackageName(type).getQualifiedName().toString();
        String executeName = execute.getSimpleName().toString();
        String className = String.format("%s_%s_Handler", type.getSimpleName(), executeName);
        MethodSpec rootMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.mHost = new $T()",type)
                .build();
        //实体化函数
        MethodSpec handleMethod = MethodSpec.methodBuilder("handle")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mObject,"commandData")
                .returns(mObject)
                .addStatement("return (($L)mHost).$L(($L)commandData)",type,execute.getSimpleName().toString(),
                        execute.getParameters().size() > 0 ? execute.getParameters().get(0).asType().toString() : "Object"
                        )
                .build();


        TypeSpec handlerClass = TypeSpec.classBuilder(className)
                .addJavadoc(Constants.DOC_EDIT_WARN)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(mHandlerType)
                .addField(hostField)
                .addMethod(rootMethod)
                .addMethod(handleMethod)
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, handlerClass).build();
        try {
            javaFile.writeTo(mFiler);
            return packageName + "." + className;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PackageElement getPackageName(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(CommandId.class);
        classSet.add(CommandMapping.class);
        classSet.add(HcsApp.class);
    }
}