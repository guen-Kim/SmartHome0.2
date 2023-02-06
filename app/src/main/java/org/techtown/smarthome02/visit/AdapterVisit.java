package org.techtown.smarthome02.visit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.techtown.smarthome02.R;

import java.util.ArrayList;

public class AdapterVisit extends RecyclerView.Adapter<AdapterVisit.ViewHolder>{

    static Boolean check = false;

    private Context ctx = null;
    private ArrayList<ItemData> al = null;

    public AdapterVisit(Context context, ArrayList<ItemData> al) {
        this.ctx = context;
        this.al = al;
    }

    @NonNull
    @Override
    public AdapterVisit.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 레이아웃 인프레이트
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 뷰에 바인딩
        AdapterVisit.ViewHolder iviewholder = (AdapterVisit.ViewHolder) holder;

        //방문자 게시판 식별자
        ItemData idata = al.get(position);


        // 1. name 바인딩
        String name = idata.name;
        if(name.equals("INTRUDER"))
        {
            iviewholder.itemName.setText(idata.name);
            iviewholder.itemName.setTextColor(Color.rgb(200,0,0));

            //침입자 확인
            iviewholder.itemContainer.setCardElevation(50);

        }else
        {
            iviewholder.itemName.setText(idata.name);
            iviewholder.itemName.setTextColor(Color.rgb(150,150,150));
        }

        iviewholder.itemName.setText(idata.name);

        // 2. date 바인딩
        iviewholder.itemDate.setText(idata.date);

        // 3. image 바인딩
        Glide.with(ctx)//액티비티 내용
                .load(idata.image.split(",")[0])
                .apply(new RequestOptions())
                .placeholder(R.drawable.visit)// 디폴트 이미지
                .centerCrop()   // 이미지 중앙 중심으로
                .dontTransform() //이미지에 변형 x
                .transition(new DrawableTransitionOptions().crossFade(1000))//fade 효과
                .into(iviewholder.itemImage);


    }

    @Override
    public int getItemCount() {
        return al.size();
    }


        /*뷰 홀더 */
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView itemName = null ;
            TextView itemDate = null ;
            ImageView itemImage = null ;
            CardView itemContainer = null ;



            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.itemName);
                itemDate = itemView.findViewById(R.id.itemDate);
                itemImage = itemView.findViewById(R.id.itemImage);
                itemContainer = itemView.findViewById(R.id.rowContainer);
            }
        }

    public void addItem(ItemData data){
        al.add(0, data); // 최신 글 부터 쌓기
    }


}
