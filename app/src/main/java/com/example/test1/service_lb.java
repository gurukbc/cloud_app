package com.example.test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

public class service_lb extends AppCompatActivity implements View.OnClickListener {

    private LBAdapter lbAdpater;
    private List<LBData> lData;
    private Button btn_zone;
    private Button btn_ser;
    private Button btn_ser_img;
    private EditText txt_zone;

    final int[] list_size = new int[1];
    APIcall_main API = (APIcall_main) getApplication();
    APIcall_LB apicall_lb = new APIcall_LB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_lb);
        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        init();

        final String []lbType =  new String[100];
        final String []lbOpt =  new String[100];
        final String []name =  new String[100];
        final String []zonename = new String[100];
        final String []ip =  new String[100];
        final String []port =  new String[100];
        final String []server =  new String[100];
        final String []id =  new String[100];

        btn_zone = (Button)findViewById(R.id.btn_lb_zone_search);
        btn_zone.setOnClickListener(this);
        txt_zone = (EditText)findViewById(R.id.txt_lb_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);
        txt_zone.setText(API.getZone());

        //사용자가 입력한 위치, 상태에 따른 서버 목록 가져오기
        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList

            @Override
            public void run() {
                try {
//                    API.setZone(zone);//default 값 설정 - UI변경되고 수정해야 함 - 수정 완료
                    API.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                    list = apicall_lb.listLB();//LB이름, 위치, 옵션, 타입, IP, Port, id
                    list_size[0] = list.size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "해당 존에 LB가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list.size(); i++) {
                            lbOpt[i] = list.get(i)[2];
                            lbType[i] = list.get(i)[3];
                            name[i] = list.get(i)[0];
                            zonename[i] = list.get(i)[1];
                            server[i] = "(상세 보기)";
                            ip[i] = list.get(i)[4];
                            port[i] = list.get(i)[5];
                            id[i] = list.get(i)[6];
                        }
                        getData_service_lb(name,lbType, lbOpt, ip, port, server, zonename,id);
                    }
                });
            }
        }).start();

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
                            EditText tmp = (EditText)findViewById(R.id.txt_lb_zone_search);
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

    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_service_lb);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        lbAdpater = new LBAdapter();
        recyclerView.setAdapter(lbAdpater);
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }


    private void getData_service_lb(String[] name, String[] lbType, String[] lbOpt, String[] ip, String[] port, String[] server, String[] zonename, String[] id) {
        // 임의의 데이터입니다.
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zonename);
        List<String> listLBType = Arrays.asList(lbType);
        List<String> listLBOpt = Arrays.asList(lbOpt);
        List<String> listIp = Arrays.asList(ip);
        List<String> listPort = Arrays.asList(port);
        List<String> listServer = Arrays.asList(server);
        List<String> listId = Arrays.asList(id);

        Integer [] tmp = new Integer[list_size[0]];
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.lb;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[0]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            LBData lbData = new LBData();
            lbData.setName(listName.get(i));
            lbData.setLBOpt(listLBOpt.get(i));
            lbData.setResId(listResId.get(i));
            lbData.setZoneName(listZone.get(i));
            lbData.setIp(listIp.get(i));
            lbData.setPort(listPort.get(i));
            lbData.setLBType(listLBType.get(i));
            lbData.setServer(listServer.get(i));
            lbData.setId(listId.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            lbAdpater.addItem(lbData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        lbAdpater.notifyDataSetChanged();
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

}
