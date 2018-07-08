package com.fengshihao.xlistenerprocessor;

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
        annotations.add(GenerateNotifier.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> allInterfaces = roundEnvironment.getElementsAnnotatedWith(GenerateNotifier.class);
        for (Element element : allInterfaces) {
            if (element.getKind() != ElementKind.INTERFACE) {
                note("dont use GenerateNotifier on a non-Interface object " + element.getSimpleName());
                return false;
            }
            NotifierModel model = new NotifierModel();

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

    private void createFile(NotifierModel model) {
        try {
            JavaFileObject jfo = mFiler.createSourceFile(model.packageName + "." + model.className, new Element[]{});
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

class NotifierModel {
    String className;
    String interfaceName;
    String packageName;
    Map<String, List<String>> methods = new HashMap<>();

    public String toCode() {
        String code = "package " + packageName + ";\n\n\n"
                +"import com.fengshihao.xlistener.XListener;\n\n"
                + "public class " + interfaceName + "Notifier {\n"
                + getMethodsCode()
                + "}";
        return code;
    }

    private String getMethodsCode() {
        StringBuilder code = new StringBuilder();
        for (String methodName: methods.keySet()) {
            List<String> parameters = methods.get(methodName);
            code.append(getMethodCode(methodName, parameters));
        }
        return code.toString();
    }

    private String getMethodCode(String methodName, List<String> parameters) {

        String nname = "notify" + captureName(methodName);
        StringBuilder code = new StringBuilder("  public static void " + nname + " (XListener<" + interfaceName + "> xListener");
        StringBuffer plist = new StringBuffer();
        for (int i = 0; i < parameters.size() ; i+=2) {
            String pt = parameters.get(i);
            String pn = parameters.get(i + 1);
            code.append(", ").append(pt).append(" ").append(pn);
            plist.append(pn);
            if (i != parameters.size() - 2) {
                plist.append(",");
            }
        }
        code.append(") {\n").append(
            "    for (").append(interfaceName).append(" l: xListener.getListeners()) {\n")
                .append("      l.").append(methodName).append("(").append(plist).append(");\n").append("    }\n}\n");

        return code.toString();
    }

    private static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return  name;

    }
}