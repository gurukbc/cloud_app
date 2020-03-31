package com.example.test1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MetricAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<MetricData> listData = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_metric_opt, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

        messageViewHolder.onBind(listData.get(position), position);



        //in some cases, it will prevent unwanted situations
        final MetricData objIncome = listData.get(position);
        ((MessageViewHolder) holder).cbtn.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        ((MessageViewHolder) holder).cbtn.setChecked(objIncome.checked);

        ((MessageViewHolder) holder).cbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                objIncome.setSelected();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(MetricData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    void rmItem() {
        listData.clear();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // API로 받아올 값들
        private CheckBox cbtn;
        private TextView opt;
        private MetricData maData;

        // 포지션
        private int position;


        public MessageViewHolder(View view) {
            super(view);
            cbtn = view.findViewById(R.id.cbtn_metric_opt);
            opt = view.findViewById(R.id.txt_metric_opt_title);
        }

        void onBind(MetricData data, int position) {
            this.maData = data;
            this.position = position;

            opt.setText(data.getOpt());

            changeVisibility(selectedItems.get(position));

            cbtn.setOnClickListener(this);
            opt.setOnClickListener(this);
        }

        //list click해서 접고 펼치기
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_disk_service_name:
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

        }

        /**
         * 클릭된 Item의 상태 변경
         * @param isExpanded Item을 펼칠 것인지 여부
         */
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 172;
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
                }
            });
            // Animation start
            va.start();
        }
    }
}