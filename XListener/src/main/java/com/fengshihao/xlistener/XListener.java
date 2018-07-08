package com.fengshihao.xlistener;

import java.util.LinkedList;
import java.util.List;

public class XListener<T> {
    private List<T> mListeners = new LinkedList<>();
    private String logTag = XListener.class.getSimpleName();

    public XListener(String name) {
        if (name == null) {
            return;
        }
        logTag = name;
    }

    public void addListener(T listener) {
        if (listener == null) {
            loge("addListener: wrong arg null");
            return;
        }
        if (mListeners.contains(listener)) {
            loge("addListener: already in " + listener);
            return;
        }
        mListeners.add(listener);
        log("addListener: now has listener=" + mListeners.size());
    }

    public T removeListener(T listener) {
        if (listener == null) {
            loge("removeListener: wrong arg null");
            return null;
        }
        if (mListeners.isEmpty()) {
            return null;
        }
        int idx = mListeners.indexOf(listener);
        if (idx == -1) {
            loge("removeListener: did not find this listener " + listener);
            return null;
        }
        T r = mListeners.remove(idx);
        log("removeListener: now has listener=" + mListeners.size());
        return r;
    }


    public void clean() {
        log("clean() called");
        mListeners.clear();
    }

    public List<T> getListeners() {
        return mListeners;
    }

    private void log(String info) {
        System.out.println(logTag + " " + info);
    }

    private void loge(String info) {
        System.err.println(logTag + " " + info);
    }
}