package com.example.test1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.concurrent.atomic.AtomicBoolean;


public class ServerAdapter extends RecyclerView.Adapter {

    private AtomicBoolean run;
    private Context mContext;
    private List<ServerData> listData = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    APIcall_server api_server = new APIcall_server();
    Handler handler = new Handler(Looper.getMainLooper());

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_server, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

        final String[] tmp = new String[1];
        messageViewHolder.onBind(listData.get(position), position);
        TextView txt=((MessageViewHolder) holder).state;
        final String state = txt.getText().toString();
        Button bs = ((MessageViewHolder) holder).buttonStop;
        Button bst = ((MessageViewHolder) holder).buttonStart;
        Button brs = ((MessageViewHolder) holder).buttonRestart;
        final String id=((MessageViewHolder) holder).id;

        bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(!state.equals("Stopped")) api_server.stopServer(id);
                            else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(v.getContext(), "정지 불가능", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            };
        });

        bst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(!state.equals("Running")) api_server.startServer(position);
                            else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(v.getContext(), "시작 불가능", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            };
        });
        brs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            api_server.rebootServer(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            };
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(ServerData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // API로 받아올 값들
        private ImageView imageView;
        private TextView name;
        private TextView state;
        private TextView created;
        private TextView zonename;
        private TextView osname;
        private String id;
        private ServerData sData;

        // API 여부와 관계없이 고정된 뷰들
        private ConstraintLayout item;
        public Button buttonStop, buttonStart, buttonRestart;

        // 포지션
        private int position;


        public MessageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_service_server);
            name = view.findViewById(R.id.txt_server_service_name);
            state = view.findViewById(R.id.txt_service_server_state);
            created = view.findViewById(R.id.txt_service_server_date);
            zonename = view.findViewById(R.id.txt_service_server_zone);
            osname = view.findViewById(R.id.txt_service_server_os);
            item = view.findViewById(R.id.lay_service_server_item);

            buttonStop = view.findViewById(R.id.btn_service_server_stop_txt);
            buttonStart = view.findViewById(R.id.btn_service_server_start_txt);
            buttonRestart = view.findViewById(R.id.btn_service_server_reStart_txt);
        }

        void onBind(ServerData data, int position) {
            this.sData = data;
            this.position = position;


            imageView.setImageResource(data.getResId());
            name.setText(data.getName());
            state.setText(data.getState());
            osname.setText(data.getOsname());
            zonename.setText(data.getZonename());
            created.setText(data.getCreated());
            id = data.getId();

            changeVisibility(selectedItems.get(position));

            imageView.setOnClickListener(this);
            name.setOnClickListener(this);
            state.setOnClickListener(this);
            created.setOnClickListener(this);
            zonename.setOnClickListener(this);
            osname.setOnClickListener(this);
        }

        //list click해서 접고 펼치기
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_server_service_name:
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
         *
         * @param isExpanded Item을 펼칠 것인지 여부
         */
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 214;
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