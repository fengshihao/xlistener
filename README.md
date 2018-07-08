
## 什么是XListener？

XListener是观察者模型的实现。 可以让开发者用最简单的方式写出各种Listener。 它主要目的是替换EventBus。

想象这么个场景， 你创建了一个播放器类 
```java
class Player {
   public void play() {}
   public void pause() {}
   public void stop() {}
}
```
播放器会有一些事件需要其他模块处理，不能把其他的逻辑，比如UI的展示，写在播放器中，于是你创建一个listener

```java
interface PlayerListener {
   void onStarted();
   void onProgress(int progress);
   void onStop();
}
// 这时候player需要做些改动 增加一个成员 listener, 并在合适的时候调用通知， 这样外边的模块就可以根据事件做出相应逻辑。
class Player {
   PlayerListener mListener;
   public void play() {
        mListener.onStarted();
   }
   
   //在程序某个地方
       mListener.onProgress(pro);
       
   public void pause() {
   }
   
   public void stop() {
       mListener.onStop();
   }
}
```

但是问题很快又来了。 关心这些事件的不是一个模块， 比如字幕模块， 声音模块...他们都需要知道什么时候开始播放 什么时候停止播放。
于是Player.mListener变成了一个列表。 你得维护它。 并且写一堆for循环去通知这个list中的每个成员。 
你的程序会有很多很多类 很多很多事件。 最后充斥这些看似一样但又稍微不同的代码。 这时候你和魔鬼（EventBus）做了个交易 让它给你
自由， 从此你走上了不归路。 XListener 是解决这些问题的。 了解一下。


## 为什么放弃EventBus

* EventBus 使得程序逻辑杂乱无章， 看似解耦实则是为错误的设计做了补丁。 
* 被迫定义各种Event， 调用和被调用方使用都很不方便。
* 使用了反射的机制效率不高。

## 特点
* 使用模板类方式让开发者省去了编写addXXXListener() removeXXXListener() notifyXXXListener() 这样的样板代码
* 使用代码生成方式保证Listener的调用效率， 和类型检查不失效。
* 非常小的代价。 只有一个类模板， 一个预处理代码生成类。

## 使用

这个库还没有传送到maven或者jcenter上。 只能copy源码中的两个模块到自己工程中使用。 



```java
// 主要是用这两个类
import com.fengshihao.xlistener.XListener;
import com.fengshihao.xlistenerprocessor.GenerateNotifier;

// 如果一个接口加了GenerateNotifier注解之后会生成相应的class TestListenerNotifier
// TestListenerNotifier 作用在下边代码有解释。
@GenerateNotifier 
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String cc, Buffer bf) {};
}

public class GenerateTest {

    // XListener内部维护着一个TestListener列表。 可以管理这个list， 省去编写addXListener这种样板代码
    XListener<TestListener> mTestListeners = new XListener<>("name_for_log");

    public void testListener() {
        mTestListeners.addListener(new TestListener() {
            @Override
            public void onX(int x) {
                log("onX() called with: x = [" + x + "]");
            }
        });
        // 这里就是TestListenerNotifier的作用， 用来通知XListener中每个listener什么事件发生了。 注意第一个参数是个XListener类型
        TestListenerNotifier.notifyOnX(mTestListeners, 100);
        TestListenerNotifier.notifyOnY(mTestListeners, 19, 2, "hello", null);

        mCameraListeners.clean();
    }
```

