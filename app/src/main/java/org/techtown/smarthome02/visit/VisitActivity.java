package org.techtown.smarthome02.visit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.smarthome02.R;

import java.util.ArrayList;

public class VisitActivity extends AppCompatActivity {
    AdapterVisit adapter;

    // for 파이어베이스
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference(); // root
    FirebaseStorage store = FirebaseStorage.getInstance();
    StorageReference storageRef = store.getReference();  // root

    // DAO
    ArrayList<ItemData> al = new ArrayList<ItemData>();
    RecyclerView mainScrollView;

    TextView tv_titlebar;
    ImageButton ibtn_search;
    EditText et_date;


    private SwipeRefreshLayout rfScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        //필드 초기화
        inite();

        //데이터 베이스 리스너
        addfireBaseListener();
/*        ItemData a = new ItemData("222","333","444","555");
        ItemData b = new ItemData("222","333","444","555");
        ItemData c = new ItemData("222","333","444","555");
        ItemData d = new ItemData("222","333","444","555");


        adapter.addItem(a);
        adapter.addItem(b);

        adapter.notifyDataSetChanged();*/
        //기본적인 리싸이클러뷰 로직 다시 보셈!
        //검색
        addSearch();



    }

    private void addSearch() {


        ibtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(VisitActivity.this) {
                    @Override protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };

                //입력된 날짜 받아오기
                String inputDate = et_date.getText().toString();

                if(inputDate != "")
                {

                    for(int i = 0 ; i < al.size(); i++)
                    {
                        String date = al.get(i).date.split("/")[0]; // yyyy-mm-dd

                        if(inputDate.equals(date)) // 문자열 비교
                        {
                            smoothScroller.setTargetPosition(i); //itemPosition - 이동시키고자 하는 Item의 Position
                            mainScrollView.getLayoutManager().startSmoothScroll(smoothScroller);
                            return; //  날짜 중 가장 첫번째 방문자
                        }



                    }


                }
            }
        });


    }

    private void addfireBaseListener() {
        // 한 번 만 받아옴.
        myRef.child("visitor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot oneSnapshot: snapshot.getChildren()){
                    ItemData data = oneSnapshot.getValue(ItemData.class);
                    adapter.addItem(data);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            } });



    }


    private void inite() {
        tv_titlebar = findViewById(R.id.tv_titlebar);
        tv_titlebar.setText(" Smart Home Visior List ");
        ibtn_search = findViewById(R.id.ibtn_search);
        et_date = findViewById(R.id.et_date);

        adapter = new AdapterVisit(this, al);///sss
        mainScrollView = (RecyclerView) findViewById(R.id.mainScrollView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mainScrollView.setLayoutManager(llm);
        mainScrollView.setAdapter(adapter);


        // 위로 스와이프
        rfScroll = (SwipeRefreshLayout) findViewById(R.id.rfScroll);
        rfScroll.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 새로고침
                //dataRefresh();
                rfScroll.setRefreshing(false); //로딩중인 화면 제거
            }
        });

    }
}