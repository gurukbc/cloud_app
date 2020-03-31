package com.example.test1;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Monitoring extends AppCompatActivity {

    APIcall_main API = (APIcall_main) getApplication();
    APIcall_watch apIcall_watch = new APIcall_watch();
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitoring);

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

        final LineChart lineChart_networkin = (LineChart) findViewById(R.id.chart_networkin);
        final LineChart lineChart_networkout = (LineChart) findViewById(R.id.chart_networkout);
        final LineChart lineChart_cpuutil = (LineChart) findViewById(R.id.chart_cpuutilization);
        final LineChart lineChart_diskreadbytes = (LineChart) findViewById(R.id.chart_diskreadbytes);
        final LineChart lineChart_diskwritebytes = (LineChart) findViewById(R.id.chart_diskwritebytes);
        final LineChart lineChart_memoryinternalfree = (LineChart) findViewById(R.id.chart_memoryinternalfree);

        final ArrayList<Entry> entries_networkin = new ArrayList<>();
        final LineDataSet dataset_networkin = new LineDataSet(entries_networkin, "values");
        final ArrayList<String> labels_networkin = new ArrayList<String>();

        final ArrayList<Entry> entries_networkout = new ArrayList<>();
        final LineDataSet dataset_networkout = new LineDataSet(entries_networkout, "values");
        final ArrayList<String> labels_networkout = new ArrayList<String>();

        final ArrayList<Entry> entries_cpuutil = new ArrayList<>();
        final LineDataSet dataset_cpuutil = new LineDataSet(entries_cpuutil, "values");
        final ArrayList<String> labels_cpuutil = new ArrayList<String>();

        final ArrayList<Entry> entries_diskreadbytes = new ArrayList<>();
        final LineDataSet dataset_diskreadbytes = new LineDataSet(entries_diskreadbytes, "values");
        final ArrayList<String> labels_diskreadbytes = new ArrayList<String>();

        final ArrayList<Entry> entries_diskwritebytes = new ArrayList<>();
        final LineDataSet dataset_diskwritebytes = new LineDataSet(entries_diskwritebytes, "values");
        final ArrayList<String> labels_diskwritebytes = new ArrayList<String>();

        final ArrayList<Entry> entries_memoryinternalfree = new ArrayList<>();
        final LineDataSet dataset_memoryinternalfree = new LineDataSet(entries_memoryinternalfree, "values");
        final ArrayList<String> labels_memoryinternalfree = new ArrayList<String>();


        new Thread(new Runnable() {
            HashMap<String, String> list_networkin = new HashMap<String, String>();
            Set<String> xlist_networkin = new LinkedHashSet<>();

            HashMap<String, String> list_networkout = new HashMap<String, String>();
            Set<String> xlist_networkout = new LinkedHashSet<>();

            HashMap<String, String> list_cpuutil = new HashMap<String, String>();
            Set<String> xlist_cpuutil = new LinkedHashSet<>();

            HashMap<String, String> list_diskreadbytes = new HashMap<String, String>();
            Set<String> xlist_diskreadbytes = new LinkedHashSet<>();

            HashMap<String, String> list_diskwritebytes = new HashMap<String, String>();
            Set<String> xlist_diskwritebytes = new LinkedHashSet<>();

            HashMap<String, String> list_memoryinternalfree = new HashMap<String, String>();
            Set<String> xlist_memoryinternalfree = new LinkedHashSet<>();

            @Override
            public void run() {
                try {
                    apIcall_watch.showMetric("NetworkIn");
                    list_networkin = apIcall_watch.getInfo("NetworkIn");
                    xlist_networkin = list_networkin.keySet();

                    apIcall_watch.showMetric("NetworkOut");
                    list_networkout = apIcall_watch.getInfo("NetworkOut");
                    xlist_networkout = list_networkout.keySet();

                    apIcall_watch.showMetric("CPUUtilization");
                    list_cpuutil = apIcall_watch.getInfo("CPUUtilization");
                    xlist_cpuutil = list_cpuutil.keySet();

                    apIcall_watch.showMetric("DiskReadBytes");
                    list_diskreadbytes = apIcall_watch.getInfo("DiskReadBytes");
                    xlist_diskreadbytes = list_diskreadbytes.keySet();

                    apIcall_watch.showMetric("DiskWriteBytes");
                    list_diskwritebytes = apIcall_watch.getInfo("DiskWriteBytes");
                    xlist_diskwritebytes = list_diskwritebytes.keySet();

                    apIcall_watch.showMetric("MemoryInternalFree");
                    list_memoryinternalfree = apIcall_watch.getInfo("MemoryInternalFree");
                    xlist_memoryinternalfree = list_memoryinternalfree.keySet();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        String xarr_networkin[] = xlist_networkin.toArray(new String[xlist_networkin.size()]);
                        String xarr_networkout[] = xlist_networkout.toArray(new String[xlist_networkout.size()]);
                        String xarr_cpuutil[] = xlist_cpuutil.toArray(new String[xlist_cpuutil.size()]);
                        String xarr_diskreadbytes[] = xlist_diskreadbytes.toArray(new String[xlist_diskreadbytes.size()]);
                        String xarr_diskwritebytes[] = xlist_diskwritebytes.toArray(new String[xlist_diskwritebytes.size()]);
                        String xarr_memoryinternalfree[] = xlist_memoryinternalfree.toArray(new String[xlist_memoryinternalfree.size()]);
                        for (int i = 0; i < 6; i++) {
                            entries_networkin.add(new Entry(Float.parseFloat(list_networkin.get(xarr_networkin[i])), i));
                            labels_networkin.add(xarr_networkin[i]);
                            entries_networkout.add(new Entry(Float.parseFloat(list_networkout.get(xarr_networkout[i])), i));
                            labels_networkout.add(xarr_networkout[i]);
                            entries_cpuutil.add(new Entry(Float.parseFloat(list_cpuutil.get(xarr_cpuutil[i])), i));
                            labels_cpuutil.add(xarr_cpuutil[i]);
                            entries_diskreadbytes.add(new Entry(Float.parseFloat(list_diskreadbytes.get(xarr_diskreadbytes[i])), i));
                            labels_diskreadbytes.add(xarr_diskreadbytes[i]);
                            entries_diskwritebytes.add(new Entry(Float.parseFloat(list_diskwritebytes.get(xarr_diskwritebytes[i])), i));
                            labels_diskwritebytes.add(xarr_diskwritebytes[i]);
                            entries_memoryinternalfree.add(new Entry(Float.parseFloat(list_memoryinternalfree.get(xarr_memoryinternalfree[i])), i));
                            labels_memoryinternalfree.add(xarr_memoryinternalfree[i]);
                        }

                        LineData data_networkin = new LineData(labels_networkin, dataset_networkin);
                        dataset_networkin.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_networkin.setLineWidth(3.5f);
                        dataset_networkin.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_networkin.setData(data_networkin);
                        lineChart_networkin.animateY(2000);

                        LineData data_networkout = new LineData(labels_networkout, dataset_networkout);
                        dataset_networkout.setLineWidth(3.5f);
                        dataset_networkout.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_networkout.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_networkout.setData(data_networkout);
                        lineChart_networkout.animateY(2000);

                        LineData data_cpuutil = new LineData(labels_cpuutil, dataset_cpuutil);
                        dataset_cpuutil.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_cpuutil.setLineWidth(3.5f);
                        dataset_cpuutil.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_cpuutil.setData(data_cpuutil);
                        lineChart_cpuutil.animateY(2000);

                        LineData data_diskreadbytes = new LineData(labels_diskreadbytes, dataset_diskreadbytes);
                        dataset_diskreadbytes.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_diskreadbytes.setLineWidth(3.5f);
                        dataset_diskreadbytes.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_diskreadbytes.setData(data_diskreadbytes);
                        lineChart_diskreadbytes.animateY(2000);

                        LineData data_diskwritebytes = new LineData(labels_diskwritebytes, dataset_diskwritebytes);
                        dataset_diskwritebytes.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_diskwritebytes.setLineWidth(3.5f);
                        dataset_diskwritebytes.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_diskwritebytes.setData(data_diskwritebytes);
                        lineChart_diskwritebytes.animateY(2000);

                        LineData data_memoryinternalfree = new LineData(labels_memoryinternalfree, dataset_memoryinternalfree);
                        dataset_memoryinternalfree.setColors(Collections.singletonList(0xFF94D1CA));
                        dataset_memoryinternalfree.setLineWidth(3.5f);
                        dataset_memoryinternalfree.setDrawCubic(true); //선 둥글게 만들기

                        lineChart_memoryinternalfree.setData(data_memoryinternalfree);
                        lineChart_memoryinternalfree.animateY(2000);
                    }
                });
            }
        }).start();

        // 메트릭 이동
        Button b = (Button)findViewById(R.id.btn_moni_main_metric);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_Metric.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        // 알람 이동
        Button al = (Button)findViewById(R.id.btn_moni_main_alarm);
        al.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_alarm.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "전체");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        // 메시지 이동
        msg_btn();

        // 알람 상황별 이동
        alarm_btn();
    }

//    //액션버튼 메뉴 액션바에 집어 넣기
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    public void msg_btn() {
        Button msg = (Button)findViewById(R.id.btn_moni_main_msg);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_Messaging.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "전체");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        Button tmsg = (Button)findViewById(R.id.tbtn_moni_main_msg);
        tmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_Messaging.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "전체");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    }

    public void alarm_btn() {
        final Button occ = (Button)findViewById(R.id.btn_moni_alarm_occ);
        occ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_alarm.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "발생");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        final Button data = (Button)findViewById(R.id.btn_moni_alarm_data);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_alarm.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "데이터 부족");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        final Button nor = (Button)findViewById(R.id.btn_moni_alarm_normal);
        nor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        Monitoring_alarm.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("btn_value", "안정");
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        //알람 현황창 구성
        new Thread(new Runnable() {
            int list[] = new int[3];
            @Override
            public void run() {
                try {
                    list = apIcall_watch.listAlarmStatus();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {//UI접근
                        occ.setText("발생 " + Integer.valueOf(list[0]));
                        data.setText("데이터 부족 "+ Integer.valueOf(list[1]));
                        nor.setText("안정 "+ Integer.valueOf(list[2]));
                    }
                });
            }
        }).start();
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