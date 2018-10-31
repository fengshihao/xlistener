
## 使用

第一步：
   gradle 添加依赖：
```
    annotationProcessor 'com.fengshihao.xlistener:xlistener:1.0.1'
    compileOnly 'com.fengshihao.xlistener:xlistener:1.0.1'
```

第二部：
```java
import com.fengshihao.xlistener.XListener;

@XListener  //使用完成代码自动生成 TestListenerList 类，和TestListener包一样.
interface TestListener {
    default void onX(int x) {}
    default void onY(int x, float y, String z) {}
    void onZ();
}

```

第三部：
```java
TestListenerList t = new TestListenerList();
// 之后onX, onZ... 将会被在主线程调用。 如果调用attachToCurrentThread，会让回调在当前线程
t.attachToMainThread(); 
t.addListener(new TestListener() {
   @Override
   public void onX(int x) {
       Log.d(TAG, "onX() called with: x = [" + x + "] thread=" + Thread.currentThread().getName());
   }

   @Override
   public void onZ() {
       Log.d(TAG, "onZ() called on thread=" + Thread.currentThread().getName());
   }
});

t.onX(100);
t.onZ();    
t.clean(); //清空listener列表
```
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
于是Player.mListener变成了一个列表 PlayerListenerList。 你得维护它。
 ```java
 //这个类里边充斥着样板代码。 XListener可以方便的生成这种样板代码。 
 public interface PlayerListenerList implements PlayerListener {
   private List<PlayerListener> mListeners = new ArrayList<>();
   
   public void addListener(PlayerListener listener) {
     ...
   }
   public void removeListener(PlayerListener listener) {
     ...
   }
   
   
   @Override
   public void onStarted() {
      for (PlayerListener l: mListeners) {
         l.onStarted();
      }
   }
   
   ....
}
 ```
## 为什么放弃EventBus
 这时候有很多人会想到EventBus。 但是EventBus 会引诱人们写各种凌乱的event。 然后就成了EventBugs。 
* EventBus 使得程序逻辑杂乱无章， 看似解耦实则是为错误的设计做了补丁。 
* 被迫定义各种Event， 调用和被调用方使用都很不方便。
* 使用了反射的机制效率不高。

## XListener特点
* 使用自动代码生成方式让开发者省去了编写addXListener() removeXListener() 这样的样板代码
* 使用代码生成方式保证Listener的调用效率， 和类型检查不失效，调用方和被调用方在重构时保证参数正确。
* 非常小的代价。几乎不影响现有接口， 生成的XListenerList 和 XListene的接口保持一致。
* 可以设置XListenerList回调线程。 线程切换异常方便。
