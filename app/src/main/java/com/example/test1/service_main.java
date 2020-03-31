package com.example.test1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class service_main extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_main);

        //액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF94D1CA));

    }

    /**
     * @brief 메뉴 중 서버 버튼 클릭 처리 함수
     */
    public void ServerClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_server.class);
        startActivity(intent);
    }

    /**
     * @brief 메뉴 중 디스크 버튼 클릭 처리 함수
     */
    public void DiskClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_disk.class);
        startActivity(intent);
    }
    /**
     * @brief 메뉴 중 LoadBalancer 버튼 클릭 처리 함수
     */
    public void LBClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_lb.class);
        startActivity(intent);
    }
    /**
     * @brief 메뉴 중 autoscaling 버튼 클릭 처리 함수
     */
    public void AutoScalingClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_autoscaling.class);
        startActivity(intent);
    }
    /**
     * @brief 메뉴 중 DB 버튼 클릭 처리 함수
     */
    public void DBClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_db.class);
        startActivity(intent);
    }
    /**
     * @brief 메뉴 중 NAS 버튼 클릭 처리 함수
     */
    public void NASClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), service_nas.class);
        startActivity(intent);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
