package com.example.test1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AutoScalingAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<AutoscalingData> listData = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    APIcall_Autoscaling apIcall_autoscaling = new APIcall_Autoscaling();
    Handler handler = new Handler(Looper.getMainLooper());

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_autoscaling, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);
        messageViewHolder.onBind(listData.get(position), position);

        Button ch = ((MessageViewHolder) holder).change;
        final TextView tmpv = ((MessageViewHolder) holder).name;
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final EditText tmp = (EditText) ((MessageViewHolder) holder).changevalue;
                final String name = tmpv.getText().toString();
                final String value = tmp.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String s = apIcall_autoscaling.updateDesiredCapacity(name, value);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(v.getContext(), s, Toast.LENGTH_LONG).show();
                                    tmp.setText("");
                                    //VM 수가 변경 되면 변경 해야됨 - Background Thread
                                }
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (NoSuchAlgorithmException ex) {
                            ex.printStackTrace();
                        } catch (InvalidKeyException ex) {
                            ex.printStackTrace();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(AutoscalingData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // API로 받아올 값들
        private ImageView imageView;
        private TextView name;
        private TextView state;
        private TextView zonename;
        private TextView curVM;
        private TextView tarVM;
        private TextView minVM;
        private TextView maxVM;
        private TextView down; // 쿨다운
        private TextView time; // 헬스체크 유예시간
        private AutoscalingData atData;
        public EditText changevalue;
        public Button change;

        // API 여부와 관계없이 고정된 뷰들
        private ConstraintLayout item;


        // 포지션
        private int position;

        public MessageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_service_auto);
            name = view.findViewById(R.id.txt_auto_service_name);
            state = view.findViewById(R.id.txt_service_auto_state);
            zonename = view.findViewById(R.id.txt_service_auto_zone);
            curVM = view.findViewById(R.id.txt_service_auto_curVM);
            tarVM = view.findViewById(R.id.txt_service_auto_tarVM);
            minVM = view.findViewById(R.id.txt_service_auto_minVM);
            maxVM = view.findViewById(R.id.txt_service_auto_maxVM);
            item = view.findViewById(R.id.lay_service_auto_item);
            changevalue = view.findViewById(R.id.txt_auto_tarVM_change);
            down = view.findViewById(R.id.txt_service_auto_down);
            time = view.findViewById(R.id.txt_service_auto_time);
            change = view.findViewById(R.id.btn_auto_tarVM_change);
        }

        void onBind(AutoscalingData data, int position) {
            this.atData = data;
            this.position = position;

            imageView.setImageResource(data.getResId());
            name.setText(data.getName());
            state.setText(data.getState());
            zonename.setText(data.getZoneName());
            curVM.setText(data.getCurVM());
            tarVM.setText(data.getTarVM());
            minVM.setText(data.getMinVm());
            maxVM.setText(data.getMaxVm());
            down.setText(data.getDown());
            time.setText(data.getTime());

            changeVisibility(selectedItems.get(position));

            imageView.setOnClickListener(this);
            name.setOnClickListener(this);
            state.setOnClickListener(this);
            zonename.setOnClickListener(this);
            curVM.setOnClickListener(this);
            tarVM.setOnClickListener(this);
            minVM.setOnClickListener(this);
            maxVM.setOnClickListener(this);
        }

        //list click해서 접고 펼치기
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_auto_service_name:
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
            int dpValue = 270;
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
        }
    }
}