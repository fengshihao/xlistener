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

            GenerateNotifier anotation = element.getAnnotation(GenerateNotifier.class);
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

    private void createFile(NotifierModel model) {
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

class NotifierModel {
    String className;
    String interfaceName;
    String packageName;
    boolean notifyOnMainThread;
    Map<String, List<String>> methods = new HashMap<>();

    private String getMethodsCode() {
        StringBuilder code = new StringBuilder();
        for (String methodName: methods.keySet()) {
            List<String> parameters = methods.get(methodName);
            code.append(getMethodCode(methodName, parameters));
        }
        return code.toString();
    }

    private String getMethodCode(String methodName, List<String> parameters) {
        StringBuilder code = new StringBuilder("  @Override\n" +
                "    public void " + methodName + " (");
        StringBuffer plist = new StringBuffer();

        int lastParam = parameters.size() - 2;
        for (int i = 0; i < parameters.size() ; i+=2) {
            String pt = parameters.get(i);
            String pn = parameters.get(i + 1);
            code.append(pt).append(" ").append(pn);
            plist.append(pn);
            if (i != lastParam) {
                code.append(", ");
                plist.append(", ");
            }
        }
        code.append(") {\n").append(
                "      for (").append(interfaceName).append(" l: mListeners) {\n")
                .append("        l.").append(methodName).append("(").append(plist).append(");\n").append("      }\n    }\n");

        return code.toString();
    }

    String toCode() {
        String code = Template.replace("notify_methods", getMethodsCode());
        return code.replace("INTERFACE", interfaceName);
    }

    private String Template = "package com.fengshihao.example.xlistener;\n" +
            "\n" +
            "\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class INTERFACEList implements INTERFACE {\n" +
            "    private List<INTERFACE> mListeners = new ArrayList<>();\n" +
            "    private String logTag = \"INTERFACEList\";\n" +
            "\n" +
            "    notify_methods\n" +
            "\n" +
            "    public void addListener(INTERFACE listener) {\n" +
            "        if (listener == null) {\n" +
            "            loge(\"addListener: wrong arg null\");\n" +
            "            return;\n" +
            "        }\n" +
            "        if (mListeners.contains(listener)) {\n" +
            "            loge(\"addListener: already in \" + listener);\n" +
            "            return;\n" +
            "        }\n" +
            "        mListeners.add(listener);\n" +
            "        log(\"addListener: now has listener=\" + mListeners.size());\n" +
            "    }\n" +
            "\n" +
            "    public INTERFACE removeListener(INTERFACE listener) {\n" +
            "        if (listener == null) {\n" +
            "            loge(\"removeListener: wrong arg null\");\n" +
            "            return null;\n" +
            "        }\n" +
            "        if (mListeners.isEmpty()) {\n" +
            "            return null;\n" +
            "        }\n" +
            "        int idx = mListeners.indexOf(listener);\n" +
            "        if (idx == -1) {\n" +
            "            loge(\"removeListener: did not find this listener \" + listener);\n" +
            "            return null;\n" +
            "        }\n" +
            "        INTERFACE r = mListeners.remove(idx);\n" +
            "        log(\"removeListener: now has listener=\" + mListeners.size());\n" +
            "        return r;\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    public void clean() {\n" +
            "        log(\"clean() called\");\n" +
            "        mListeners.clear();\n" +
            "    }\n" +
            "\n" +
            "    private void log(String info) {\n" +
            "        System.out.println(logTag + \" \" + info);\n" +
            "    }\n" +
            "\n" +
            "    private void loge(String info) {\n" +
            "        System.err.println(logTag + \" \" + info);\n" +
            "    }\n" +
            "    \n" +
            "}\n";
}
