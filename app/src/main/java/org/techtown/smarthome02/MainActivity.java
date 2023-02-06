package org.techtown.smarthome02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.smarthome02.cctv.cctvActivity;
import org.techtown.smarthome02.talk.TalkActivity;
import org.techtown.smarthome02.visit.VisitActivity;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    //타이틀
    TextView tv_title;

    //실시간 메시지
    TextView tv_rtm;
    Switch sw_rtm;

    // 집 상태
    TextView tv_temp;
    TextView tv_hum;

    //불
    Switch sw_led1;
    Switch sw_led2;
    ImageView iv_led1;
    ImageView iv_led2;

    //에어컨
    Switch sw_air;
    SeekBar sb_air;
    TextView tv_air;

    //난방
    Switch sw_hot;
    SeekBar sb_hot;
    TextView tv_hot;

    //카메라
    ImageButton ibtn_visit;
    ImageButton ibtn_cctv;
    ImageButton ibtn_emer;

    //실시간 토크
    LinearLayout ll_talk;


    //for realtime firebase
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference(); // root


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //필드 초기화
        inite();

        //실시간 탐지 메시지
        addRearTimeEvent();

        // 버튼 이벤트, 새로운 액티비티 출력
        addBtnEvent();

        // 핫 이벤트
        addHotEvent();

        // 모터 이벤트
        addAirEvent();


        //led 이벤트
        addledEevent();


        // 파이어베이스 리스너
        addFireBaseListener();


    }

    private void addAirEvent() {
        sw_air.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("power", "on");
                    myRef.child("moter").updateChildren(state);  // 값 업데이트


                }else{
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("power", "off");
                    myRef.child("moter").updateChildren(state);  // 값 업데이트


                }


            }
        });



        sb_air.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO 시크바를 조작하고 있는 중
                tv_air.setText(String.format("%d °C", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO 시크바를 처음 터치했을 때
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO  시크바 터치가 끝났을 때
                Map<String, Object> state = new HashMap<String, Object>();
                Integer value = new Integer(seekBar.getProgress());
                state.put("setting",value);
                myRef.child("moter").updateChildren(state);  // 값 업데이트



            }
        });





    }

    private void addledEevent() {
        sw_led1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    iv_led1.setImageResource(R.drawable.ledon);
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("led1", "on");
                    myRef.child("LED").updateChildren(state);  // 값 업데이트




                }else{
                    iv_led1.setImageResource(R.drawable.ledoff);
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("led1", "off");
                    myRef.child("LED").updateChildren(state);  // 값 업데이트

                }


            }
        });

        sw_led2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    iv_led2.setImageResource(R.drawable.ledon);
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("led2", "on");
                    myRef.child("LED").updateChildren(state);  // 값 업데이트




                }else{
                    iv_led2.setImageResource(R.drawable.ledoff);
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("led2", "off");
                    myRef.child("LED").updateChildren(state);  // 값 업데이트

                }


            }
        });





    }

    private void addRearTimeEvent() {
        sw_rtm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    tv_rtm.setText("실시간 탐지 메시지 수신 활성화 중입니다.");
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("state", "on");
                    myRef.child("rtMessage").updateChildren(state);  // 값 업데이트




                }else{
                    tv_rtm.setText("실시간 탐지 메시지 수신 비활성화 중입니다.");
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("state", "off");
                    myRef.child("rtMessage").updateChildren(state);  // 값 업데이트

                }


            }
        });

    }

    private void addFireBaseListener() {

        //1회 트리거 후, 해당 자식 변경될 때마다 다시 트리거
        myRef.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HomeState value = snapshot.getValue(HomeState.class);


                double  temp = value.Temperature;
                String t_tmp = Double.toString(temp);
                String temper = t_tmp + "°C";
                tv_temp.setText(temper);



                double  hum = value.Humiduty;
                String t_hum = Double.toString(hum);

                String humi = t_hum + " g/m3";
                tv_hum.setText(humi);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void addHotEvent() {

        sw_hot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("power", "on");
                    myRef.child("hit").updateChildren(state);  // 값 업데이트



                }else{
                    Map<String, Object> state = new HashMap<String, Object>();
                    state.put("power", "off");
                    myRef.child("hit").updateChildren(state);  // 값 업데이트


                }


            }
        });



        sb_hot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO 시크바를 조작하고 있는 중
                tv_hot.setText(String.format("%d °C", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO 시크바를 처음 터치했을 때
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO  시크바 터치가 끝났을 때
                Map<String, Object> state = new HashMap<String, Object>();
                Integer value = new Integer(seekBar.getProgress());
                state.put("setting",value);
                myRef.child("hit").updateChildren(state);  // 값 업데이트



            }
        });

    }

    private void inite()
    {
        sw_rtm = findViewById(R.id.sw_rtm);
        tv_rtm = findViewById(R.id.tv_rtm);


        tv_title = findViewById(R.id.tv_title);

        tv_temp = findViewById(R.id.tv_temp);
        tv_hum = findViewById(R.id.tv_hum);

        sw_led1 = findViewById(R.id.sw_led1);
        sw_led2 = findViewById(R.id.sw_led2);
        iv_led1 = findViewById(R.id.iv_led1);
        iv_led2 = findViewById(R.id.iv_led2);

        sw_air = findViewById(R.id.sw_air);

        sw_hot = findViewById(R.id.sw_hot);
        sb_hot = findViewById(R.id.sb_hot);
        tv_hot = findViewById(R.id.tv_hot);

        sw_air = findViewById(R.id.sw_air);
        sb_air = findViewById(R.id.sb_air);
        tv_air = findViewById(R.id.tv_air);






        ibtn_visit = findViewById(R.id.ibtn_visit);
        ibtn_cctv = findViewById(R.id.ibtn_cctv);
        ibtn_emer = findViewById(R.id.ibtn_emer);

        ll_talk = findViewById(R.id.ll_talk);

    }



    private void addBtnEvent()
    {

        // 방문자 리스트 페이지
        ibtn_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VisitActivity.class);
                startActivity(intent);

            }
        });

        // 긴급 다이얼
        ibtn_emer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ 1119));
                startActivity(intent);

            }
        });


        // cctv 실시간 스트리밍 페이지
        ibtn_cctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cctvActivity.class);
                startActivity(intent);

            }
        });


        ll_talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TalkActivity.class);
                startActivity(intent);
            }
        });



    }


    //액티비티 종료시
    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    //액티비티 onCreate() 직후
    @Override
    protected void onStart() {
        super.onStart();

        // 파이어 베이스에서 smarthome  상태 값 읽어오기
        //1. rtMessage
        myRef.child("rtMessage").child("state").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                 if(value.equals("on"))
                 {
                     sw_rtm.setChecked(true);



                 }else{
                     sw_rtm.setChecked(false);
                 }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        //2. LED
        myRef.child("LED").child("led1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("on"))
                {
                    iv_led1.setImageResource(R.drawable.ledon);
                    sw_led1.setChecked(true);



                }else{
                    iv_led1.setImageResource(R.drawable.ledoff);
                    sw_led2.setChecked(false);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("LED").child("led2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("on"))
                {
                    iv_led2.setImageResource(R.drawable.ledon);

                    sw_led2.setChecked(true);



                }else
                {
                    iv_led2.setImageResource(R.drawable.ledoff);
                    sw_led2.setChecked(false);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //난로
        myRef.child("hit").child("power").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("on"))
                {
                    sw_hot.setChecked(true);



                }else{
                    sw_hot.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("hit").child("setting").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                String to = Integer.toString(value);
                tv_hot.setText(to+"°C");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        //모터
        myRef.child("moter").child("power").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(value.equals("on"))
                {
                    sw_air.setChecked(true);



                }else{
                    sw_air.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        myRef.child("moter").child("setting").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                String to = Integer.toString(value);
                tv_air.setText(to+"°C");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
}