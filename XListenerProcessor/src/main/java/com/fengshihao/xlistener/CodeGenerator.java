package com.fengshihao.xlistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengshihao on 18-10-16.
 */
class CodeGenerator {
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
        code.append("\n");
        return code.toString();
    }

    private String getMethodCode(String methodName, List<String> parameters) {
        StringBuilder code = new StringBuilder("    @Override\n" +
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
            "notify_methods" +
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
