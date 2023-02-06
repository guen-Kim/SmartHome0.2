package org.techtown.smarthome02.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.techtown.smarthome02.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TalkActivity extends AppCompatActivity {



    //톡
    EditText et_msg;
    ImageButton btn_send;
    ListView lv_rely;
    ReplyAdapter adapter;


    //for realtime firebase
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference(); // root

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents_reply);



        //필드초기화
        inite();


        //
        setReplyListener();

    }

    private void setReplyListener()
    {


        // 메시지 보내기. 파이어 베이스 데이터 저장
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ReplyData newData = new ReplyData();
                String write_time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String reg_date = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US).format(new Date());


                //필드초기화
                newData.content = et_msg.getText().toString(); //입력 내용
                newData.idx = write_time;
                newData.reg_date = reg_date;
                newData.isMe = false; // when client

                // 메시지 내용 파이어베이스에 저장
                myRef.child("talk").push().setValue(newData);
                et_msg.setText(""); // 입력 상자 초기화


                adapter = new ReplyAdapter(TalkActivity.this, new ArrayList<ReplyData>());
                lv_rely.setAdapter(adapter);
                setReplyListen();

            }
        });

    }

    private void setReplyListen() {
        // 파이어베이스 데이터 가져오기, 변경 시 마다 트리거
        myRef.child("talk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                adapter.removeMessage(); //  아래코드에서 새로 갱신된 메시지 포함한 전체 데이터를 로드하여 데이터 다시 지움.
                if(snapshot.hasChildren())
                {
                    for(DataSnapshot d  :snapshot.getChildren() )
                    {
                        ReplyData newData  =  d.getValue(ReplyData.class);
                        //필드초기화
                        adapter.add(newData);
                    }
                    adapter.notifyDataSetChanged();
                    lv_rely.setSelection(lv_rely.getCount() - 1); // 스크롤 항상 맨 마지막

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void inite() {
        et_msg = findViewById(R.id.etMsg);
        btn_send = findViewById(R.id.btnSend);
        lv_rely = findViewById(R.id.lvRely);
        adapter = new ReplyAdapter(TalkActivity.this, new ArrayList<ReplyData>());
        lv_rely.setAdapter(adapter); // 리스트 뷰 어텝터 바인딩



    }
}