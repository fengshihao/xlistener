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
            code.append("\n");
        }
        return code.toString();
    }


    private String getMethodCode(String methodName, List<String> parameters) {
        final String METHOD_TMPL =
                "    @Override"                                         + "\n" +
                "    public void METHOD(PARAMS) {"                      + "\n" +
                "        if (mHandler == null || isRightThread()) {"    + "\n" +
                "METHOD_INNER_TMPL"                                     + "\n" +
                "            return;"                                   + "\n" +
                "        }"                                             + "\n" +
                "        mHandler.post(() -> {"                         + "\n" +
                "METHOD_INNER_TMPL"                                     + "\n" +
                "        });"                                           + "\n" +
                "    }"                                                 + "\n";

        final String METHOD_INNER_TMPL =
                "           for (INTERFACE l: mListeners) {"               + "\n" +
                "               l.METHOD(NAMES);"                          + "\n" +
                "           }";

        final String METHOD_T = METHOD_TMPL.replace("METHOD_INNER_TMPL", METHOD_INNER_TMPL);
        String params = "";
        String paramNames = "";

        int lastParam = parameters.size() - 2;
        for (int i = 0; i < parameters.size() ; i+=2) {
            String paramType = parameters.get(i);
            String paramName = parameters.get(i + 1);
            params += paramType + " " + paramName;
            paramNames += paramName;
            if (i != lastParam) {
                params += ", ";
                paramNames += ", ";
            }
        }
        return METHOD_T.replace("METHOD", methodName)
                .replace("PARAMS", params)
                .replace("NAMES", paramNames);
    }

    String toCode() {
        String code = Template.replace("PACKAGE_NAME", packageName)
                .replace("METHODS_BODY", getMethodsCode());
        return code.replace("INTERFACE", interfaceName);
    }

    private String Template = "package PACKAGE_NAME;\n" +
            "\n" +
            "\n" +
            "import android.os.Handler;\n" +
            "import android.os.Looper;\n" +
            "import android.util.Log;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +

            "\n" +
            "public class INTERFACEList implements INTERFACE {\n" +
            "    private List<INTERFACE> mListeners = new ArrayList<>();\n" +
            "    private static final String TAG = \"INTERFACEList\";\n" +
            "METHODS_BODY" +
            "    private Handler mHandler;\n" +

            "    public void attachToCurrentThread() {\n" +
            "        if (Looper.myLooper() == null) {\n" +
            "            Log.e(TAG, \"this thread do not has looper!\");\n" +
            "            return;\n" +
            "        }\n" +
            "        mHandler = new Handler(Looper.myLooper());\n" +
            "    }\n" +
            "\n" +
            "    public void attachToMainThread() {\n" +
            "        mHandler = new Handler(Looper.getMainLooper());\n" +
            "    }" +
            "\n" +
            "    private boolean isRightThread() {\n" +
            "        return mHandler.getLooper() == Looper.myLooper();\n" +
            "    }\n" +
            "\n" +
            "    public void addListener(INTERFACE listener) {\n" +
            "        if (mHandler == null || isRightThread()) {"    + "\n" +
            "            addListener_(listener);"                   + "\n" +
            "            return;"                                   + "\n" +
            "        }"                                             + "\n" +
            "        mHandler.post(() -> {"                         + "\n" +
            "            addListener_(listener);"                   + "\n" +
            "        });"                                           + "\n" +
            "    }\n" +
            "\n" +
            "    public void removeListener(INTERFACE listener) {\n" +
            "        if (mHandler == null || isRightThread()) {"    + "\n" +
            "            removeListener_(listener);"                + "\n" +
            "            return;"                                   + "\n" +
            "        }"                                             + "\n" +
            "        mHandler.post(() -> {"                         + "\n" +
            "            removeListener_(listener);"                + "\n" +
            "        });"                                           + "\n" +
            "    }\n" +
            "\n" +
            "    public void clean() {\n" +
            "        if (mHandler == null || isRightThread()) {"    + "\n" +
            "            clean_();"                                 + "\n" +
            "            return;"                                   + "\n" +
            "        }"                                             + "\n" +
            "        mHandler.post(() -> {"                         + "\n" +
            "            clean_();"                                 + "\n" +
            "        });"                                           + "\n" +
            "    }\n" +
            "\n" +
            "    private void addListener_(INTERFACE listener) {\n" +
            "        if (listener == null) {\n" +
            "            Log.e(TAG, \"addListener: wrong arg null\");\n" +
            "            return;\n" +
            "        }\n" +
            "        if (mListeners.contains(listener)) {\n" +
            "            Log.e(TAG, \"addListener: already in \" + listener);\n" +
            "            return;\n" +
            "        }\n" +
            "        mListeners.add(listener);\n" +
            "        Log.d(TAG, \"addListener: now has listener=\" + mListeners.size());\n" +
            "    }\n" +
            "\n" +
            "    private INTERFACE removeListener_(INTERFACE listener) {\n" +
            "        if (listener == null) {\n" +
            "            Log.e(TAG, \"removeListener: wrong arg null\");\n" +
            "            return null;\n" +
            "        }\n" +
            "        if (mListeners.isEmpty()) {\n" +
            "            return null;\n" +
            "        }\n" +
            "        int idx = mListeners.indexOf(listener);\n" +
            "        if (idx == -1) {\n" +
            "            Log.e(TAG, \"removeListener: did not find this listener \" + listener);\n" +
            "            return null;\n" +
            "        }\n" +
            "        INTERFACE r = mListeners.remove(idx);\n" +
            "        Log.d(TAG, \"removeListener: now has listener=\" + mListeners.size());\n" +
            "        return r;\n" +
            "    }\n" +
            "\n" +
            "    private void clean_() {\n" +
            "        Log.d(TAG, \"clean() called\");\n" +
            "        mListeners.clear();\n" +
            "    }\n" +
            "}\n";
}
