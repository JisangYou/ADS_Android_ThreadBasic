# ThreadBasic

## 개념
- 스레드(thread)는 어떠한 프로그램 내에서, 특히 프로세스 내에서 실행되는 흐름의 단위를 말한다. 일반적으로 한 프로그램은 하나의 스레드를 가지고 있지만, 프로그램 환경에 따라 둘 이상의 스레드를 동시에 실행할 수 있다. 이러한 실행 방식을 멀티스레드(multithread)라고 한다. (출처: 위키백과)
- 멀티작업을 하기 위한 기능
- 예를 들어 메시지를 보내면서, 음악을듣거나, 네비게이션을 키는 등 여러 작업을 동시에 할 수 있는 기능
- 개념 보강 필요!

### 예제 및 사용방법
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
            msg.what = MainActivity.ACTION_SET;
            handler.sendMessage(msg);

// handler.sendEmptyMessage(MainActivity.ACTION_SET); 위3줄과 같은 코드

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
}

```
- 버튼을 누르면 직사각형 버튼이 시계처럼 돌아가는 예제임
- 핸들러를 생성하는 스레드만이 다른스레드가 전송하는 Message나 Runnable객체 를 수신하는 기능을 할 수 있음
- 핸들러는 동시 작업이 들어올시에 순서대로 처리할 수 있음.
- Rotater 클래스는 Thread를 상속받아 쓰레드를 구현했음.
- Thread를 제어하기 위해서는 inturrupt(), stop()등도 있으나 위험도가 있기에 플래그 값을 주어 제어가능(setStop()메서드 참고)
- 쓰레드를 구성한 쪽에서 핸들러쪽으로 message를 보내면, 그 message에 따라 쓰레드를 실행시켜줌.
- 다음을 보면,

```Java
class Rotater extends Thread {

    Handler handler;
    Boolean RUNNING = true;

    public Rotater(Handler handler) {
        this.handler = handler;
    }

    public void run() {
       while(RUNNING){
        handler.sendEmptyMessage(MainActivity.ACTION_SET);
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
}
```
- 핸들러 쪽에서 보낸 ACTION_SET(int타입의 정해놓은 값)을 받으면 쓰레드 실행


```Java
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
```
- ACTION_SET이란 값을 받으면, 메인클래스에서 정의해놓은 애니메이션 실행
