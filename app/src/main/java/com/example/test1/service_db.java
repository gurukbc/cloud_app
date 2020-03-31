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

public class service_db extends AppCompatActivity implements View.OnClickListener {

    private DBAdapter DBAdapter;
    private List<DBData> dbData;
    private Button btn_zone;
    private EditText txt_zone;

    APIcall_main API = (APIcall_main) getApplication();
    APIcall_DB api_db = new APIcall_DB();

    final int[] list_size = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_db);

        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        init();

        final String []state = new String[100];
        final String []created = new String[100];
        final String []name = new String[100];
        final String []zoneName = new String[100];
        final String []dev = new String[100];
        final String []size = new String[100];
        final String []DBstate = new String[100];

        btn_zone = (Button)findViewById(R.id.btn_db_zone_search);
        btn_zone.setOnClickListener(this);

        txt_zone = (EditText)findViewById(R.id.txt_db_zone_search);
        txt_zone.setFocusable(false);
        txt_zone.setOnClickListener(this);
        txt_zone.setText(API.getZone());

        new Thread(new Runnable() {
            ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList

            @Override
            public void run() {
                try {
//                    API.setZone(zone);//default 값 설정 - UI변경되고 수정해야 함 - 수정 완료
                    API.setState("all");//default 값 설정 - 추후 UI변경 시 수정
                    list = api_db.listMysqlDB();//이름, 상태, DB상태(보류), 용량, 생성일, 종속장치, 위치
                    list_size[0] = list.size();
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
                            Toast.makeText(getApplicationContext(), "해당 존에 DB가 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        for (int i = 0; i < list.size(); i++) {
                            state[i] = list.get(i)[1];
                            DBstate[i] = list.get(i)[2];
                            created[i] = list.get(i)[4];
                            name[i] = list.get(i)[0];
                            zoneName[i] = list.get(i)[6];
                            dev[i] = list.get(i)[5];
                            size[i] = list.get(i)[3];
                        }

                        getData_service_db(state, created, name, zoneName, dev, size, DBstate);
                    }
                });
            }
        }).start();

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
                            EditText tmp = (EditText)findViewById(R.id.txt_db_zone_search);
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
        // recyclerView = db list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_service_db);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DBAdapter = new DBAdapter();
        recyclerView.setAdapter(DBAdapter);
    }


    private void getData_service_db(String[] state, String[] created, String[] name, String[] zoneName, String[] dev, String[] size, String[] DBstate) {
        // 임의의 데이터입니다.
        List<String> listState = Arrays.asList(state);
        List<String> listCreated = Arrays.asList(created);
        List<String> listName = Arrays.asList(name);
        List<String> listZone = Arrays.asList(zoneName);
        List<String> listDev = Arrays.asList(dev);
        List<String> listSize = Arrays.asList(size);
        List<String> listDBstate = Arrays.asList(DBstate);

        Integer [] tmp = new Integer[list_size[0]];
        for(int i = 0; i < tmp.length; i++) {
            tmp[i] = R.drawable.db;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < list_size[0]; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            DBData dbData = new DBData();
            dbData.setName(listName.get(i));
            dbData.setState(listState.get(i));
            dbData.setResId(listResId.get(i));
            dbData.setZoneName(listZone.get(i));
            dbData.setDev(listDev.get(i));
            dbData.setSize(listSize.get(i));
            dbData.setCreated(listCreated.get(i));
            dbData.setDBstate(listDBstate.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            DBAdapter.addItem(dbData);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        DBAdapter.notifyDataSetChanged();
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
