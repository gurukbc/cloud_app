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

public class service_nas extends AppCompatActivity implements View.OnClickListener {

    private NASAdapter nsAdapter;
    private List<NASData> nData;
    private Button btn_zone;
    private EditText txt_zone;

    APIcall_main API = (APIcall_main) getApplication();
    APIcall_NAS api_nas = new APIcall_NAS();

    final int[] list_size = new int[1];
    final ArrayList<String[]>[] list = new ArrayList[]{new ArrayList<String[]>()};//NAS 정보를 받아올 ArrayList-thread내 접근을 위해 배열로 변경
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_nas);

        final Handler handler = new Handler();
        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        init();

        final String[] name = new String[100];
        final String[] zonename = new String[100];
        final String[] curSize = new String[100];
        final String[] tarSize = new String[100];
        final String[] protocol = new String[100];
        final String[] id = new String[100];

        btn_zone = (Button) findViewById(R.id.btn_nas_zone_search);
        btn_zone.setOnClickListener(this);
        txt_zone = (EditText) findViewById(R.id.txt_nas_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);

        txt_zone.setText(API.getZone());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    API.setZone(zone);//default 값 설정 - UI변경되고 수정해야 함 - 수정 완료
                    API.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                    list[0] = api_nas.listNas();//이름, 위치, 신청용량, 현재 사용량, 프로토콜
                    list_size[0] = list[0].size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "해당 존에 NAS가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list[0].size(); i++) {
                            curSize[i] = list[0].get(i)[2];
                            tarSize[i] = list[0].get(i)[3];
                            name[i] = list[0].get(i)[0];
                            zonename[i] = list[0].get(i)[1];
                            protocol[i] = list[0].get(i)[4];
                            id[i] = list[0].get(i)[5];
                        }
                        getData_service_nas(name, zonename, curSize, tarSize, protocol,id);
                    }
                });
            }
        }).start();

    }

    /**
     * @brief 용량변경 처리 함수
     */
    public void Updatevolume(View v) {
        Button btn_change = (Button) findViewById(R.id.btn_nas_tarSize_change);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_zone || v == txt_zone) {
            final CharSequence[] zoneItem = {"전체", "Central-A", "Central-B", "Seoul-M", "Seoul-M2", "HA", "US-West"};

            AlertDialog.Builder oDialog = new AlertDialog.Builder(this);

            oDialog.setTitle("존(Zone) 위치 선택")
                    .setItems(zoneItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText tmp = (EditText) findViewById(R.id.txt_nas_zone_search);
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView_service_nas);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        nsAdapter = new NASAdapter();
        recyclerView.setAdapter(nsAdapter);
    }

    private void getData_service_nas(String[] name, String[] zonename, String[] curSize, String[] tarSize, String[] protocol, String[] id) {
        // 임의의 데이터입니다.
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zonename);
        List<String> listCurSize = Arrays.asList(curSize);
        List<String> listTarSize = Arrays.asList(tarSize);
        List<String> listProtocol = Arrays.asList(protocol);
        List<String> listId = Arrays.asList(id);

        Integer[] tmp = new Integer[list_size[0]];

        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.nas;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[0]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            NASData nData = new NASData();
            nData.setName(listName.get(i));
            nData.setResId(listResId.get(i));
            nData.setZoneName(listZone.get(i));
            nData.setCurSize(listCurSize.get(i));
            nData.setTarSize(listTarSize.get(i));
            nData.setProtocol(listProtocol.get(i));
            nData.setId(listId.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            nsAdapter.addItem(nData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        nsAdapter.notifyDataSetChanged();
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
