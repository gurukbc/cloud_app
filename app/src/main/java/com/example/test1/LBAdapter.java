package com.example.test1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class LBAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<LBData> listData = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private Button btn_ser_img;
    private LBpopupAdapter adapter;
    APIcall_LB apIcall_lb = new APIcall_LB();
    Handler handler = new Handler(Looper.getMainLooper());

    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_lb, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

        messageViewHolder.onBind(listData.get(position), position);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(LBData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    void rmItem() {
        listData.clear();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        // API로 받아올 값들
        private ImageView imageView;
        private TextView name;
        private TextView server;
        private TextView lbType;
        private TextView lbOpt;
        private TextView zoneName;
        private TextView ip;
        private TextView port;
        private LBData lData;

        // API 여부와 관계없이 고정된 뷰들
        private ConstraintLayout item;
        private String id;


        // 포지션
        private int position;


        public MessageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_service_lb);
            name = view.findViewById(R.id.txt_lb_service_name);
            server = view.findViewById(R.id.txt_service_lb_server);
            lbType = view.findViewById(R.id.txt_service_lb_type);
            lbOpt = view.findViewById(R.id.txt_service_lb_opt);
            zoneName = view.findViewById(R.id.txt_service_lb_zone);
            ip = view.findViewById(R.id.txt_service_lb_ip);
            port = view.findViewById(R.id.txt_service_lb_port);
            item = view.findViewById(R.id.lay_service_lb_item);
        }

        void onBind(LBData data, int position) {
            this.lData = data;
            this.position = position;

            imageView.setImageResource(data.getResId());
            name.setText(data.getName());
            server.setText(data.getServer());
            lbType.setText(data.getLBType());
            zoneName.setText(data.getZoneName());
            lbOpt.setText(data.getLBOpt());
            ip.setText(data.getIp());
            port.setText(data.getPort());
            id = data.getId();

            changeVisibility(selectedItems.get(position));

            imageView.setOnClickListener(this);
            name.setOnClickListener(this);
            server.setOnClickListener(this);
            lbType.setOnClickListener(this);
            zoneName.setOnClickListener(this);
            lbOpt.setOnClickListener(this);
            ip.setOnClickListener(this);
            port.setOnClickListener(this);
        }

        //list click해서 접고 펼치기
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_lb_service_name:
                    if (selectedItems.get(position)) {
                        // 펼쳐진 Item을 클릭 시
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                    break;
            }

            if (v == btn_ser_img) {
                final String[] serContent = new String[200];
                final String zone = zoneName.getText().toString();
                final int[] count = {0};
                final String[] serSubject = {"서버명 : ", "Public IP : ", "Public Port : ", "Throughput : ", "Server connections : ", "TTFB : ", "Request : ", "상태 : ", "\n"};
                new Thread(new Runnable() {
                    ArrayList<String[]> list = new ArrayList<String[]>();//서버 정보를 받아올 ArrayList
                    @Override
                    public void run() {
                        try {
                            list = apIcall_lb.listLBWebServers(id, zone);
                            count[0] = list.size();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        for(int i=0;i<count[0];i++){
                            for(int j=0;j<serSubject.length;j++){
                                int tmp = i*serSubject.length + j;
                                if(j== serSubject.length-1) serContent[tmp] = "";
                                else serContent[tmp] = list.get(i)[j];
                            }
                        }
                        // 사용자가 클릭할 때 LB 적용 서버 정보 받아오면 됌
                        int serNum = count[0];  // 적용 서버 개수
                        final String[] serInfo = new String[serSubject.length * serNum];

                        int sub_idx = 0;
                        for (int i = 0; i < serSubject.length * serNum; i++) {
                            serInfo[i] = serSubject[sub_idx++] + serContent[i];
                            if (sub_idx == serSubject.length) sub_idx = 0;
                        }

                        final AlertDialog.Builder sDialog = new AlertDialog.Builder(mContext);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {//UI접근
                                sDialog.setTitle("적용 서버 상세 정보 리스트")
                                        .setItems(serInfo, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setCancelable(true)
                                        .show();
                            }
                        });
                    }
                }).start();

            }
        }


        /**
         * 클릭된 Item의 상태 변경
         *
         * @param isExpanded Item을 펼칠 것인지 여부
         */
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 168;
            float d = mContext.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    int value = (int) animation.getAnimatedValue();

                    item.getLayoutParams().height = value;
                    item.requestLayout();
                    item.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

                }
            });
            // Animation start
            va.start();
            btn_ser_img = (Button) item.findViewById(R.id.btn_service_lb_serInfo_img);
            btn_ser_img.setOnClickListener(this);
        }
    }
}