package com.example.test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class service_server extends AppCompatActivity implements View.OnClickListener {
    public static Context mContext;
    Handler handler = new Handler();
    private ServerAdapter svAdpater;
    private List<ServerData> sData;
    private Button btn_zone;
    private EditText txt_zone;

    int list_size;

    APIcall_main API = (APIcall_main) getApplication();
    APIcall_server api_server = new APIcall_server();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_server);

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        init();
        list_size=0;
        mContext = this;

        final String[] state = new String[100];
        final String[] created = new String[100];
        final String[] name = new String[100];
        final String[] zonename = new String[100];
        final String[] osname = new String[100];

        btn_zone = (Button)findViewById(R.id.btn_server_zone_search);
        btn_zone.setOnClickListener(this);


        txt_zone = (EditText)findViewById(R.id.txt_server_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);
        txt_zone.setText(API.getZone());


        //사용자가 입력한 위치, 상태에 따른 서버 목록 가져오기
        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
            @Override
            public void run() {
                try {
                    API.setState("all");
                    list = api_server.listServers();
                    list_size = list.size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "해당 존에 서버가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list_size; i++) {
                            state[i] = list.get(i)[2];
                            created[i] = list.get(i)[3] ;
                            name[i] = list.get(i)[0];
                            zonename[i] = API.getZone();
                            osname[i] = list.get(i)[4];
                        }
                        getData_service_s(state, created, name, zonename, osname);
                    }
                });
            }
        }).start();
    }

    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_service_server);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        svAdpater = new ServerAdapter();
        recyclerView.setAdapter(svAdpater);
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private void getData_service_s(String[] state, String[] created, String[] name, String[] zonename, String[] osname) {
        // 임의의 데이터입니다.
        List<String> listState = Arrays.asList(state);
        List<String> listCreated = Arrays.asList(created);
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zonename);
        List<String> listOsname = Arrays.asList(osname);

        Integer[] tmp = new Integer[list_size];

        for (int i = 0; i < tmp.length; i++) {
            if (osname[i].contains("centos"))
                tmp[i] = R.drawable.linux;
            else
                tmp[i] = R.drawable.window;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size; i++) {//가진 객체만큼으로 수정
            // 각 List의 값들을 data 객체에 set 해줍니다.
            ServerData sData = new ServerData();
            sData.setName(listName.get(i));
            sData.setState(listState.get(i));
            sData.setResId(listResId.get(i));
            sData.setZonename(listZone.get(i));
            sData.setOsname(listOsname.get(i));
            sData.setCreated(listCreated.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            svAdpater.addItem(sData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        svAdpater.notifyDataSetChanged();
    }


    public void onButtonClicked_home(View v) {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
    }

    /**
     * 하단바의 Dashboard 버튼 클릭 처리 함수
     */
    public void DashboardClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
    }

    /**
     * 하단바의 service 버튼 클릭 처리 함수
     */
    public void ServiceClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_main.class);
        startActivity(intent);
    }

    /**
     * 하단바의 Monitoring 버튼 클릭 처리 함수
     */
    public void MonitoringClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Monitoring.class);
        startActivity(intent);
    }

    /**
     * 하단바의 Payment 버튼 클릭 처리 함수
     */
    public void PaymentClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), Payment.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if (v == btn_zone || v == txt_zone) {
            final CharSequence[] zoneItem = {"전체","Central-A", "Central-B", "Seoul-M", "Seoul-M2", "HA", "US-West"};

            AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

            oDialog.setTitle("존(Zone) 위치 선택")
                    .setItems(zoneItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText tmp = (EditText)findViewById(R.id.txt_server_zone_search);
                            tmp.setText(zoneItem[which]);
                            API.setZone((String) zoneItem[which]);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setCancelable(true)
                    .show();
        }
    }
}
