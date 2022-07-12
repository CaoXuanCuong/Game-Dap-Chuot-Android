package com.example.hi.gamedapchuot;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;


import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.push.HmsMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Begin extends AppCompatActivity {
    MediaPlayer mp = null;
    ImageView btnPlay, btnHightScore, btnAbout, btn_sound_controll, btnweapon, btnHightScore2;
    Dialog dialog;
    List<ScoreOBJS> list;
    Adapter_Score adapter_score;
    GridView grid;
    TextView txtGold;
    ListView lv;
    Button btnEasy, btnNormal, btnHard;
    ImageView img1,img2;
    int position;
    private static boolean isPlaySound = true;
    private static boolean isSet = false;
    private Adapter_User mUserAdapter;
    private List<User> mListUser;

    String[] name = {
            "Búa Sắt",
            "Búa Gỗ",
    } ;
    int[] imageId = {
            R.drawable.thor2,
            R.drawable.buago2,
    };
    int k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        /*boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;*/



        //khởi động nhạc nền
        //gọi đến service

        startService(new Intent(Begin.this, servicerunmp3.class));
        btn_sound_controll = findViewById(R.id.btn_sound_controll);
        btnPlay = findViewById(R.id.btn_play);
        btnHightScore = findViewById(R.id.btn_HightScore);
        btnHightScore2=findViewById(R.id.btn_HightScore2);
        btnAbout = findViewById(R.id.btn_About);
        btnweapon=findViewById(R.id.btn_weapon);
        grid=findViewById(R.id.grid);
        lv=(ListView) findViewById(R.id.listView);
        img1=findViewById(R.id.img_vk);
        img2=findViewById(R.id.img_vk2);
        list = new ArrayList<>();
        mListUser=new ArrayList<>();
        adapter_score = new Adapter_Score(list);
        mUserAdapter=new Adapter_User(mListUser);
        //btnEasy = findViewById(R.id.btnEasy);
        //btnNormal = findViewById(R.id.btnNormal);
        //btnHard = findViewById(R.id.btnHard);
        //txtGold.setText(HightScoreController.getGoldUser() + "");
        btn_sound_controll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaySound) {
                    stopService(new Intent(Begin.this, servicerunmp3.class));
                    btn_sound_controll.setImageResource(R.drawable.turnoff_sound);
                    isPlaySound=false;
                } else {
                    startService(new Intent(Begin.this, servicerunmp3.class));
                    btn_sound_controll.setImageResource(R.drawable.btn_turnon_sound);
                    isPlaySound=true;
                }
            }
        });

        btnPlay.setOnClickListener((View v) -> {
            Intent intent=new Intent(Begin.this,MainActivity.class);
            Bundle bundle=new Bundle();
            bundle.putInt("number",k);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        //hiện postup
        btnHightScore.setOnClickListener((View v) -> {
            View view = getLayoutInflater().inflate(R.layout.dialog_hight_score, null);
            ImageView imageViewClose = view.findViewById(R.id.btn_close);
            RecyclerView recyclerView = view.findViewById(R.id.recycler_higtScore);

            //cấu hình của recyclerview
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Begin.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter_score);
            //lấy dữ liệu đổ ra recyclerview
            list.clear();
            list.addAll(HightScoreController.getHightScore());
            //cấu hình cho dialog
            dialog = new Dialog(Begin.this);
            //set view cho dialog
            dialog.setContentView(view);
            //cho phép đóng dialog khi ấn ra ngoài dialog
            dialog.setCancelable(true);
            //hiển thị dialog
            dialog.show();
            for (int i = 0; i < 10; i++) {
                //refresh layout
                adapter_score.notifyItemChanged(i);
            }
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        });

        btnHightScore2.setOnClickListener((View v) -> {
            View view = getLayoutInflater().inflate(R.layout.dialog_world, null);
            ImageView imageViewClose = view.findViewById(R.id.btn_close1);
            TextView txtScoreYou=view.findViewById(R.id.txt_scoreyou);
            txtScoreYou.setText(HightScoreController.getMaximumScore()+"");
            RecyclerView recyclerView1 = view.findViewById(R.id.recycler_higtScore1);
            recyclerView1.setAdapter(mUserAdapter);
            //cấu hình của recyclerview
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(Begin.this);
            linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView1.setLayoutManager(linearLayoutManager1);
            getdb();
            mListUser.clear();
            dialog = new Dialog(Begin.this);
            //set view cho dialog
            dialog.setContentView(view);
            //cho phép đóng dialog khi ấn ra ngoài dialog
            dialog.setCancelable(true);
            //hiển thị dialog
            dialog.show();

            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        });

        btnAbout.setOnClickListener((View v) -> {
            View view = getLayoutInflater().inflate(R.layout.dialog_about, null);
            ImageView imageViewClose = view.findViewById(R.id.btn_close);
            dialog = new Dialog(Begin.this);
            dialog.setContentView(view);
            dialog.setCancelable(true);
            dialog.show();
            imageViewClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        });


        btnweapon.setOnClickListener((View v) -> {
            if(!isSet){
                grid.setVisibility(View.VISIBLE);
                isSet=true;

            }
            else{
                grid.setVisibility(View.GONE);
                isSet=false;
            }
        });

        CustomGrid adapter = new CustomGrid(Begin.this, name, imageId);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(Begin.this, "Bạn chọn vũ khí " +name[+position], Toast.LENGTH_SHORT).show();
            grid.setVisibility(View.GONE);
            isSet = false;
            k=position;
        }
        });
    }
    DatabaseReference dataa=FirebaseDatabase.getInstance().getReference();
    Query query=dataa.orderByChild("score");
    private void getdb(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mListUser!=null){
                    mListUser.clear();
                }
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    mListUser.add(0,user);
                }
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
