# ADS04 Android

## 수업 내용

- Thread 기본을 학습

## Code Review

### MainActivity

```Java
public class MainActivity extends AppCompatActivity {

    Button button;
    Rotater rotater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        rotater = new Rotater(handler);

        rotater.start();
    }

    //thread를 종료시키는 함수
    public void stop(View view){
        rotater.setStop();
    } // xml에서 그냥 온클릭으로 지정해준것!

    public static final int ACTION_SET = 999;
    //seekbar를 변경하하는 handler 작성
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);

            switch (msg.what) {
                case ACTION_SET:
                    float curRot = button.getRotation();
                    button.setRotation(curRot + 6);
                    break;
            }
        }
    };
}


class Rotater extends Thread {

    Handler handler;
    Boolean RUNNING = true;

    public Rotater(Handler handler) {
        this.handler = handler;
    }

    // start 메서드가 호출되면 실행된다.
    public void run() {// run함수 안의 코드만 sub Thread에서 실행
       while(RUNNING){
            // 매초 seekbar의 회전값을 변경한다.
            //핸들러 측으로 메시지를 보낸다.
            Message msg = new Message();
            msg.what = MainActivity.ACTION_SET; // 메인액티비
            handler.sendMessage(msg);
//            handler.sendEmptyMessage(MainActivity.ACTION_SET); 위3줄과 같은 코드

            try {

                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    public void setStop(){
        RUNNING = false;
    }
```

- 버튼을 누르면 직사각형 버튼이 시계처럼 돌아가는 예제
- 핸들러를 생성하는 스레드만이 다른스레드가 전송하는 Message나 Runnable객체 를 수신하는 기능을 할 수 있음
- 핸들러는 동시 작업이 들어올시에 순서대로 처리할 수 있음.
- Rotater 클래스는 Thread를 상속받아 쓰레드를 구현했음.
- Thread를 제어하기 위해서는 inturrupt(), stop()등도 있으나 위험도가 있기에 플래그 값을 주어 제어가능(setStop()메서드 참고)
- 쓰레드를 구성한 쪽에서 핸들러쪽으로 message를 보내면, 그 message에 따라 쓰레드를 실행시켜줌.
- 핸들러 쪽에서 보낸 ACTION_SET(int타입의 정해놓은 값)을 받으면 쓰레드 실행
- ACTION_SET이란 값을 받으면, 메인클래스에서 정의해놓은 애니메이션 실행


## 보충설명

### Thread란?
- 스레드(thread)는 어떠한 프로그램 내에서, 특히 프로세스 내에서 실행되는 흐름의 단위를 말한다. 일반적으로 한 프로그램은 하나의 스레드를 가지고 있지만, 프로그램 환경에 따라 둘 이상의 스레드를 동시에 실행할 수 있다. 이러한 실행 방식을 멀티스레드(multithread)라고 한다. (출처: 위키백과)

### Thread의 필요성

- 외부 Thread가 없이 Main Thread만으로만 구현하게 된다면, 어떠한 버튼을 눌렀을 때 Main Thread 내부적으로 10초 이상이 걸리는 작업을 한다고 치면, 사용자는 그 일이 끝날 때까지 멈춰있는 화면만 보고 있어야 한다.
- 멀티 작업을 할 떄 필요하다. 
> ex) 메시지를 보내면서, 음악을듣거나, 네비게이션을 키는 등 여러 작업을 동시에 할 수 있는 기능


### Thread 사용법

1. 작업이 끝난 후 UI변경이 필요 없는 경우를 위한 사용법(안드로이드 기준)

```Java
new Thread(new Runnable() { 
    @Override 
    public void run() { // TODO Auto-generated method stub 

            firstWork(getApplicationContext()); 
    } 
}).start();


```

2. 기본적인 사용법
- Thread 클래스를 상속 받는 하위 스레드 클래스 생성
- 하위 스레드 내에 run( )을 오버라이드 한다. run( )은 Thread가 실행되면 수행되는 곳이다.
- Thread 객체를 생성해주고 start( ) 메소드로 Tread의 run( ) 메소드를 실행시켜준다

```Java
public class ThreadTest extends Activity { 
   
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_main);
        
         ExampleThread thread = new ExampleThread(); 
         thread.start(); } 
        
         private class ExampleThread extends Thread {
              
              private static final String TAG = "ExampleThread"; 
              public ExampleThread() { 
                  // 초기화 작업
               } 
               public void run() { 
                   // 스레드에게 수행시킬 동작들 구현 
               } 
         }
    }

```

### Handler의 개념 및 필요성

- Thread는 사용자에게 보이지 않는 Background에서 실행되기 때문에 하나의 프로그램에서 여러가지 작업을 동시에 진행되도록 만들어 주는 개념
- 안드로이드에서는 Thread가 __View자원들에 직접 접근이 불가__함.
- 이유는 다음과 같은 문제가 생길 수도 있기 때문임. 

![쓰레드예제](http://cfile4.uf.tistory.com/image/2336F64B577C0CC51AF873)

- 상기와 같은 문제를 해결하기 위해 핸들러가 필요하고, 그 역할은 하기 이미지를 참고.

![핸들러의필요성](http://cfile27.uf.tistory.com/image/21372744580E23183110B7)

- 핸들러는 여러 스레드로 부터 전달받은 요청들을 메세지를 이용해 간결하게 처리

### Handler 관련 Method

#### 핸들러의 메시지 수령

핸들러에 메시지가 도착하게 되면 아래의 메서드가 호출

- public void handleMessage(Message msg)
- 인수로 메시지 객체를 전달받는데 이는 스레드 간에 통신을 해야 할 내용에 관한 객체
- 몇 가지 정보가 추가 될 수 있기 때문에 여러 개의 필드를 가지고 있음.

```Java

메시지의 Field

int what - 메시지의 ID
int arg1 - 메시지가 보낼 수 있는 정보
int arg2 - 메시지가 보낼 수 있는 또 다른 정보
Object obj - Integer로 표현 불가능 할 경우 객체를 보냄
Messenger replyTo - 응답을 받을 객체를 지정

```

#### 핸들러의 메시지 전송

- 메시지를 전송할 때는 다음의 메서드를 사용

1. 메시지의 ID에 해당하는 값을 전달할 때 사용

```Java
boolean Handler.sendEmptyMessage(int what)
```

2. ID만으로 불가능하고 좀 더 내용이 있는 정보를 전송할 때 사용

```Java
boolean Handler.sendMessage(Message msg)
```

3. 메시지가 큐에 순서대로 쌓여서 FIFO(First In First Out)형태로 처리되지만 이 메서드를 사용하면 노래방에서 우선예약 하듯이 사용

```Java
boolean sendMessageAtFrontOfQueue(Message msg)
```


#### 핸들러의 객체 전송

앞선 메서드로 특정 정보를 보낼 수 있지만 메시지를 보내는 대신에 객체를 보낼 수도 있음

```Java
boolean post(Runnable r)
```

핸들러로 다음의 매서드를 통해 Runnable 객체를 보내면 해당 객체의 run 메서드가 실행
이럴 경우 메시지를 받는 쪽은 다른 것을 정의하지 않고 핸들러만 정의하면 해당 내용을 수행할 수 있음


### Looper의 개념 및 관련 Method

- Handler는 Thread와 Looper, MessageQueue 가 꼭 필요

![looper의 역할](http://cfile4.uf.tistory.com/image/232EB335577C080F21D36D)

- Looper는 하나의 스레드만을 담당할 수 있고 하나의 스레드도 오직 하나의 Looper만을 가질 수 있습니다. Looper는 MessageQueue가 비어있는 동안은 아무 행동도 안하고 메시지가 들어오면 해당 메시지를 꺼내 적절한 Handler로 전달

- 메인 스레드의 경우 기본적으로 루퍼를 가지나, 일반 작업을 수행하는 스레드의 경우 기본적으로 루퍼를 가지지 않음.
만약 이런 스레드가 메시지를 받아야 할 경우 루퍼를 직접 생성시켜야 함.

- 관련 Method

```Java
static void prepare() - 현재 스레드를 위한 루퍼를 준비
static void loop() - 큐에서 메시지를 꺼내 핸들러로 전달하는 루프를 실행
void quit() - 루프를 종료
Thread getThread() - 루퍼와 연결된 스레드를 구함
static Looper getMainLooper() - 주 스레드의 루퍼를 구함
static Looper myLooper() - 현재 스레드의 루퍼를 구함. 루퍼가 없을 경우 null이 리턴

```



### 출처

- [출처] : http://itmining.tistory.com/4 [IT 마이닝]
- [출처] : http://itmining.tistory.com/6 [IT 마이닝]
- [출처] : http://tedrepository.tistory.com/3 [Ted's IT Repository]
- [출처] : 안드로이드의 스레드(Thread) - C. 루퍼(Looper)|작성자 녹차
- [출처] : 안드로이드의 스레드(Thread) - B. 핸들러|작성자 녹차


## TODO

- handler 관련 Method 검색 및 예제 학습
- Thread 관련 개념 및 활용하는 법 추가 학습

## Retrospect

- Thread를 왜 사용하고, 언제 사용하는지 생각해보기
- handler를 사용해 간단한 프로그램 만들어 보기.

## Output
- 생략

