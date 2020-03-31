package com.example.test1;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Monitoring_alarm extends AppCompatActivity {private MoniAlarmAdapter maAdapter;
    private List<MoniAlarmData> maData;
    APIcall_main API = (APIcall_main) getApplication();
    APIcall_watch apIcall_watch = new APIcall_watch();

    // ########################################################### //
    // 쓰레드에 있던 변수들 클래스 내 지역변수로 변경
    ArrayList<String[]> list = new ArrayList<String[]>();//ALARM 정보를 받아올 ArrayList
    final String[] state = new String[200];
    final String[] name =  new String[200];
    final String[] condi =  new String[200];
    final String[] onoff =  new String[200];
    final String[] act =  new String[200];
    final String[] type =  new String[200];

    final int[] list_size = new int[1];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moni_watch_alarm);
        final Handler handler = new Handler();

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        final LineChart lineChart_alarm1 = (LineChart) findViewById(R.id.chart_alarm1);
        final LineChart lineChart_alarm2 = (LineChart) findViewById(R.id.chart_alarm2);
        final LineChart lineChart_alarm3 = (LineChart) findViewById(R.id.chart_alarm3);

        final ArrayList<Entry> entries_alarm1 = new ArrayList<>();
        final ArrayList<Entry> entries_alarm1_1 = new ArrayList<>();
        final ArrayList<LineDataSet> lineDataSet_alarm1 = new ArrayList<>();
        final LineDataSet dataset_alarm1 = new LineDataSet(entries_alarm1, "values");
        final LineDataSet dataset_alarm1_1 = new LineDataSet(entries_alarm1_1, "threshold");
        final ArrayList<String> labels_alarm1 = new ArrayList<String>();

        final ArrayList<Entry> entries_alarm2 = new ArrayList<>();
        final ArrayList<Entry> entries_alarm2_1 = new ArrayList<>();
        final ArrayList<LineDataSet> lineDataSet_alarm2 = new ArrayList<>();
        final LineDataSet dataset_alarm2 = new LineDataSet(entries_alarm2, "values");
        final LineDataSet dataset_alarm2_1 = new LineDataSet(entries_alarm2_1, "threshold");
        final ArrayList<String> labels_alarm2 = new ArrayList<String>();

        final ArrayList<Entry> entries_alarm3 = new ArrayList<>();
        final ArrayList<Entry> entries_alarm3_1 = new ArrayList<>();
        final ArrayList<LineDataSet> lineDataSet_alarm3 = new ArrayList<>();
        final LineDataSet dataset_alarm3 = new LineDataSet(entries_alarm3, "values");
        final LineDataSet dataset_alarm3_1 = new LineDataSet(entries_alarm3_1, "threshold");
        final ArrayList<String> labels_alarm3 = new ArrayList<String>();

        init();


        final String[] statevalue = new String[1];

        menu_btn();

        Button btn_status = (Button) findViewById(R.id.btn_moni_alarm_status_sel);
        String value = null;
        if (getIntent().getExtras().getString("btn_value") != null) {
            value = getIntent().getExtras().getString("btn_value");
        }
        else {
            value = "전체";
        }
        btn_status.setText(value);

        //ALL, INSUFFICIENT_DATA, OK, ALARM
        switch(value){
            case "전체" : statevalue[0] = "ALL"; break;
            case "발생" : statevalue[0] = "ALARM"; break;
            case "안정" : statevalue[0] = "OK"; break;
            case "데이터 부족" : statevalue[0] = "INSUFFICIENT_DATA"; break;
        }
        new Thread(new Runnable() {
            ArrayList<String> list_name = new ArrayList<String>();//최근 ALARM 정보를 받아올 ArrayList

            HashMap<String, String> list_alarm1 = new HashMap<String, String>();//최근 알람 1에 대한 정보를 위함
            Set<String> xlist_alarm1 = new LinkedHashSet<>();
            String thres_alarm1;

            HashMap<String, String> list_alarm2 = new HashMap<String, String>();
            Set<String> xlist_alarm2 = new LinkedHashSet<>();
            String thres_alarm2;

            HashMap<String, String> list_alarm3 = new HashMap<String, String>();
            Set<String> xlist_alarm3 = new LinkedHashSet<>();
            String thres_alarm3;

            @Override
            public void run() {
                try {
                    apIcall_watch.listAlarmHistory();
                    list_name = apIcall_watch.getAlarmTitle();

                    list_alarm1 = apIcall_watch.getAlarmMetricInfo(list_name.get(0));
                    xlist_alarm1 = list_alarm1.keySet();
                    thres_alarm1 = apIcall_watch.getAlarmThresholdInfo(list_name.get(0));

                    list_alarm2 = apIcall_watch.getAlarmMetricInfo(list_name.get(1));
                    xlist_alarm2 = list_alarm2.keySet();
                    thres_alarm2 = apIcall_watch.getAlarmThresholdInfo(list_name.get(1));

                    list_alarm3 = apIcall_watch.getAlarmMetricInfo(list_name.get(2));
                    xlist_alarm3 = list_alarm3.keySet();
                    thres_alarm3 = apIcall_watch.getAlarmThresholdInfo(list_name.get(2));

                    list = apIcall_watch.listAlarms(statevalue[0]);
                    list_size[0] = list.size();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e){//list에러
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "알람이 없습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        String xarr_alarm1[] = xlist_alarm1.toArray(new String[xlist_alarm1.size()]);
                        String xarr_alarm2[] = xlist_alarm2.toArray(new String[xlist_alarm2.size()]);
                        String xarr_alarm3[] = xlist_alarm3.toArray(new String[xlist_alarm3.size()]);

                        for (int i = 0; i < 6; i++) {
                            entries_alarm1.add(new Entry(Float.parseFloat(list_alarm1.get(xarr_alarm1[i])), i));
                            entries_alarm1_1.add(new Entry(Float.parseFloat(thres_alarm1),i));
                            labels_alarm1.add(xarr_alarm1[i]);

                            entries_alarm2.add(new Entry(Float.parseFloat(list_alarm2.get(xarr_alarm2[i])), i));
                            entries_alarm2_1.add(new Entry(Float.parseFloat(thres_alarm2),i));
                            labels_alarm2.add(xarr_alarm2[i]);

                            entries_alarm3.add(new Entry(Float.parseFloat(list_alarm3.get(xarr_alarm3[i])), i));
                            entries_alarm3_1.add(new Entry(Float.parseFloat(thres_alarm3),i));
                            labels_alarm3.add(xarr_alarm3[i]);
                        }

                        dataset_alarm1.setColors(Collections.singletonList(0xFF94D1CA)); //그래프 선 색상 변경
                        dataset_alarm1.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm1.setDrawCubic(true); //선 둥글게 만들기

                        lineDataSet_alarm1.add(dataset_alarm1);
                        lineChart_alarm1.animateY(2000);//아래에서 올라오는 애니메이션 적용

                        dataset_alarm1_1.setColors(Collections.singletonList(0xFFff0000)); //그래프 선 색상 변경
                        dataset_alarm1_1.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm1_1.setDrawCubic(true); //선 둥글게 만들기
                        lineDataSet_alarm1.add(dataset_alarm1_1);
                        lineChart_alarm1.setData(new LineData(labels_alarm1,lineDataSet_alarm1));


                        dataset_alarm2.setColors(Collections.singletonList(0xFF94D1CA)); //그래프 선 색상 변경
                        dataset_alarm2.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm2.setDrawCubic(true); //선 둥글게 만들기

                        lineDataSet_alarm2.add(dataset_alarm2);
                        lineChart_alarm2.animateY(2000);//아래에서 올라오는 애니메이션 적용

                        dataset_alarm2_1.setColors(Collections.singletonList(0xFFff0000)); //그래프 선 색상 변경
                        dataset_alarm2_1.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm2_1.setDrawCubic(true); //선 둥글게 만들기
                        lineDataSet_alarm2.add(dataset_alarm2_1);
                        lineChart_alarm2.setData(new LineData(labels_alarm2,lineDataSet_alarm2));


                        dataset_alarm3.setColors(Collections.singletonList(0xFF94D1CA)); //그래프 선 색상 변경
                        dataset_alarm3.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm3.setDrawCubic(true); //선 둥글게 만들기

                        lineDataSet_alarm3.add(dataset_alarm3);
                        lineChart_alarm3.animateY(2000);//아래에서 올라오는 애니메이션 적용

                        dataset_alarm3_1.setColors(Collections.singletonList(0xFFff0000)); //그래프 선 색상 변경
                        dataset_alarm3_1.setLineWidth(3.5f); //그래프 선 굵기 변경
                        dataset_alarm3_1.setDrawCubic(true); //선 둥글게 만들기
                        lineDataSet_alarm3.add(dataset_alarm3_1);
                        lineChart_alarm3.setData(new LineData(labels_alarm3,lineDataSet_alarm3));

                        for (int i = 0; i < list.size(); i++) {
                            state[i] = list.get(i)[1];
                            condi[i] = list.get(i)[2];
                            name[i] = list.get(i)[0];
                            onoff[i] = list.get(i)[3];
                            act[i] = list.get(i)[4];
                            type[i] = list.get(i)[5];
                        }
                        getData(name, state, condi, onoff,act, type);
                    }
                });
            }
        }).start();
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private void init() {
        // recyclerView = server list
        RecyclerView recyclerView = findViewById(R.id.recyclerView_topic_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        maAdapter = new MoniAlarmAdapter();
        recyclerView.setAdapter(maAdapter);
    }

    // ########################################################### //
    // 함수 복붙, 알람 메뉴 버튼 UX 기능 추가
    public void menu_btn() {
        // 메트릭 그래프 추가 옵션(주기, 통계 등) 선택
        final Button btn_status = (Button) findViewById(R.id.btn_moni_alarm_status_sel);
        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);//v는 클릭된 뷰를 의미

                getMenuInflater().inflate(R.menu.alarm_status_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int idx = 0;
                        switch (item.getItemId()) {
                            case R.id.occ:
                                //maAdapter.notifyItemRangeRemoved(0, list.size());
                                maAdapter.rmItem();
                                valInit();
                                for (int i = 0; i < list.size(); i++) {
                                    if(list.get(i)[1].equals("알람 발생")) {
                                        state[idx] = list.get(i)[1];
                                        condi[idx] = list.get(i)[2];
                                        name[idx] = list.get(i)[0];
                                        onoff[idx] = list.get(i)[3];
                                        act[idx] = list.get(i)[4];
                                        type[idx++] = list.get(i)[5];
                                    }
                                }
                                getData(name, state, condi, onoff,act, type);
                                idx = 0;

                                btn_status.setText("발생");
                                break;

                            case R.id.nor:

                                maAdapter.rmItem();
                                valInit();
                                for (int i = 0; i < list.size(); i++) {
                                    if(list.get(i)[1].equals("안정")) {
                                        state[idx] = list.get(i)[1];
                                        condi[idx] = list.get(i)[2];
                                        name[idx] = list.get(i)[0];
                                        onoff[idx] = list.get(i)[3];
                                        act[idx] = list.get(i)[4];
                                        type[idx++] = list.get(i)[5];
                                    }
                                }
                                getData(name, state, condi, onoff,act, type);
                                idx = 0;

                                btn_status.setText("안정");
                                break;

                            case R.id.data:

                                maAdapter.rmItem();
                                valInit();
                                for (int i = 0; i < list.size(); i++) {
                                    if(list.get(i)[1].equals("데이터 부족")) {
                                        state[idx] = list.get(i)[1];
                                        condi[idx] = list.get(i)[2];
                                        name[idx] = list.get(i)[0];
                                        onoff[idx] = list.get(i)[3];
                                        act[idx] = list.get(i)[4];
                                        type[idx++] = list.get(i)[5];
                                    }
                                }
                                getData(name, state, condi, onoff,act, type);
                                idx = 0;

                                btn_status.setText("데이터 부족");
                                break;

                            case R.id.all:

                                maAdapter.rmItem();
                                valInit();
                                for (int i = 0; i < list.size(); i++) {
                                    state[idx] = list.get(i)[1];
                                    condi[idx] = list.get(i)[2];
                                    name[idx] = list.get(i)[0];
                                    onoff[idx] = list.get(i)[3];
                                    act[idx] = list.get(i)[4];
                                    type[idx++] = list.get(i)[5];
                                }
                                getData(name, state, condi, onoff,act, type);
                                idx = 0;

                                btn_status.setText("전체");
                                break;

                            default:
                                btn_status.setText("전체");
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    // ########################################################### //
    // 함수 자체 추가, 지역변수 초기화
    public void valInit() {
        for(int i = 0; i < state.length; i++) {
            state[i] = null;
            condi[i] = null;
            name[i] = null;
            onoff[i] = null;
            act[i] = null;
            type[i] = null;
        }
}


    private void getData(String[] name, String[] state, String[] condi, String[] onoff, String[] act, String[] type) {
        // 임의의 데이터입니다.
        List<String> listName = Arrays.asList(name);
        List<String> listState = Arrays.asList(state);
        List<String> listCondi = Arrays.asList(condi);
        List<String> listOnoff = Arrays.asList(onoff);
        List<String> listAct = Arrays.asList(act);
        List<String> listType = Arrays.asList(type);

        int num = 0;
        for (int i = 0; i < name.length; i++) {
            if (name[i] != null) { num += 1;}
        }

        Integer [] tmp = new Integer[list_size[0]];
        for(int i = 0; i < num; i++) {
            tmp[i] = R.drawable.ic_notifications_black_24dp;
        }

        List<Integer> listResId = Arrays.asList(tmp);

        for (int i = 0; i < num; i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            MoniAlarmData data = new MoniAlarmData();
            data.setName(listName.get(i));
            data.setState(listState.get(i));
            data.setResId(listResId.get(i));
            data.setCondi(listCondi.get(i));
            data.setOnoff(listOnoff.get(i));
            data.setAct(listAct.get(i));
            data.setType(listType.get(i));

            // 각 값이 들어간 data를 maAdapter에 추가합니다.
            maAdapter.addItem(data);
        }

        // maAdapter의 값이 변경되었다는 것을 알려줍니다.
        maAdapter.notifyDataSetChanged();
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
