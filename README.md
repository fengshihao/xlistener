
## 什么是XListener？

XListener是观察者模型的实现。 可以让开发者用最简单的方式写出各种Listener。 它主要目的是替换EventBus。

## 为什么放弃EventBus

* EventBus 使得程序逻辑杂乱无章， 看似解耦实则是为错误的设计做了补丁。 
* 被迫定义各种Event， 调用和被调用方使用都很不方便。
* 使用了反射的机制效率不高。

## 特点
* 使用模板类方式让开发者省去了编写addXXXListener() removeXXXListener() notifyXXXListener() 这样的样板代码
* 使用代码生成方式保证Listener的调用效率， 和类型检查不失效。
* 非常小的代价。 只有一个类模板， 一个预处理代码生成类。

## 使用


```
import com.fengshihao.xlistener.XListener;
import com.fengshihao.xlistenerprocessor.GenerateNotifier;


@GenerateNotifier 
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String cc, Buffer bf) {};
}

public class GenerateTest {

    XListener<TestListener> mTestListeners = new XListener<>("name_for_log");

    public void testListener() {
        mTestListeners.addListener(new TestListener() {
            @Override
            public void onX(int x) {
                log("onX() called with: x = [" + x + "]");
            }
        });
        TestListenerNotifier.notifyOnX(mTestListeners, 100);
        TestListenerNotifier.notifyOnY(mTestListeners, 19, 2, "hello", null);

        mCameraListeners.clean();
    }
```

