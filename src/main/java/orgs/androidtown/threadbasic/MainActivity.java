package orgs.androidtown.threadbasic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
}
