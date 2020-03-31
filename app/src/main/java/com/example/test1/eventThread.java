package com.example.test1;

import android.os.Handler;

public class eventThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public eventThread(Handler handler){
        this.handler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업
        while(isRun){
            handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
            try{
                Thread.sleep(10000); //10초씩 쉼
            }catch (Exception e) {}
        }
    }
}
