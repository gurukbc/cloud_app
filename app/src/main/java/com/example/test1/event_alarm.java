package com.example.test1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class event_alarm extends Service {
    NotificationManager noti_m;
    eventThread thread;
    Notification noti;

    private static final String TAG = "event_alarm";
    public event_alarm() {

    }

    @Override
    public void onCreate() {//최초 생성시
        super.onCreate();
        Log.d(TAG,"oncreate호출됨");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//백그라운드에서 실행
        noti_m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new eventThread(handler);
        thread.start();
        return START_STICKY;
//        if(intent == null){
//            return Service.START_STICKY;//자동 실행 옵션
//        }else{
//            processCommand(intent);
//        }
//        return super.onStartCommand(intent, flags, startId);
    }
    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(event_alarm.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(event_alarm.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            noti = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.inbound)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            noti.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            noti.flags = Notification.FLAG_AUTO_CANCEL;


            noti_m.notify( 777 , noti);

            //토스트 띄우기
            Toast.makeText(event_alarm.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    };

    private void processCommand(Intent intent){
        String command = intent.getStringExtra("command");
        String name = intent.getStringExtra("name");

        Log.d(TAG,"전달받은 데이터"+command+name);
    }

    @Override
    public void onDestroy() {//서비스 종료시
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
