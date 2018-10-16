package com.fengshihao.xlistener;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class XListenerProcessor extends AbstractProcessor{
    private Filer mFiler;
    private Messager mMessager;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(XListener.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> allInterfaces = roundEnvironment.getElementsAnnotatedWith(XListener.class);
        for (Element element : allInterfaces) {
            if (element.getKind() != ElementKind.INTERFACE) {
                note("dont use XListener on a non-Interface object " + element.getSimpleName());
                return false;
            }
            CodeGenerator model = new CodeGenerator();

            XListener anotation = element.getAnnotation(XListener.class);
            model.notifyOnMainThread = anotation.notifyOnMainThread();

            model.interfaceName = element.getSimpleName().toString();
            model.className = model.interfaceName + "Notifier";
            //1.获取包名
            PackageElement packageElement = mElementUtils.getPackageOf(element);
            String pkName = packageElement.getQualifiedName().toString();
            model.packageName = pkName;
            //note(String.format("package = %s", pkName));

            List<? extends Element> methods = element.getEnclosedElements();
            for (Element m: methods) {
                String methodName = m.getSimpleName().toString();
                //note("m=" + methodName);
                List<String> plist = new LinkedList<>();
                ExecutableElement em = (ExecutableElement) m;
                List<? extends VariableElement> parameters = em.getParameters();
                for (VariableElement p: parameters) {
                    //note("p=" + p.getSimpleName() + " " + p.asType());
                    plist.add(p.asType().toString());
                    plist.add(p.getSimpleName().toString());
                }
                model.methods.put(methodName, plist);
            }

            createFile(model);
        }
        return true;
    }

    private void createFile(CodeGenerator model) {
        try {
            JavaFileObject jfo = mFiler.createSourceFile(model.packageName + "." + model.interfaceName + "List", new Element[]{});
            Writer writer = jfo.openWriter();
            writer.write(model.toCode());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            note("error ", e);
        }
    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void note(String format, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }
}

