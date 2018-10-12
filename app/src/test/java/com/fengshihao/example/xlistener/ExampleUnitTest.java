package com.fengshihao.example.xlistener;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testListenerGenerate() {
        GenerateTest test1 = new GenerateTest();
        test1.testCameraListener();
        test1.testListener();
    }
}

