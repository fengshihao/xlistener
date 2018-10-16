package com.fengshihao.xlistener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fengshihao on 18-7-6.
 */

@Retention(RetentionPolicy.SOURCE)
public @interface XListener {

    boolean notifyOnMainThread() default false;
}
