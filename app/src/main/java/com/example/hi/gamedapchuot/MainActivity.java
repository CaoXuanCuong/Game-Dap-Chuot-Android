package com.example.hi.gamedapchuot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements Runnable {
    //luồng chính
    private Thread gameThread;
    //đối tượng chính
    private SurfaceHolder ourHolder1;
    private SurfaceHolder ourHolder2;
    private SurfaceHolder ourHolder3;
    private SurfaceHolder ourHolder4;
    //điều kiện chạy luồng
    //nếu =false luồng  dừng
    private volatile boolean playing;
    //vẽ hình ảnh in game
    private Canvas canvas1;
    private Canvas canvasBackground;
    private Canvas canvas2;
    private Canvas canvas3;
    private Canvas canvas4;
    private Canvas canvas5;
    //hình ảnh nv in game
    private Bitmap bitmapRunningMan1;
    private Bitmap bitmapRunningMan2;
    private Bitmap bitmapRunningMan3;
    private Bitmap bitmapRunningMan4;
    private Bitmap bitmapRunningMan5;
    //biến trạng thại kiểm soát di chuyển của nhân vật
    private boolean isMoving;
    private boolean freezeMouse;
    //tốc độ chậy càng thấp càng chậm
    private float runSpeedPerSecond1 = 75;
    private float runSpeedPerSecond2 = 75;
    private float runSpeedPerSecond3 = 75;
    private float runSpeedPerSecond4 = 75;
    // tọa độ của nhân vật
    private float manXPos1 = 10, manYPos1 = 0;
    private float manXPos2 = new Random().nextInt(100) + 100, manYPos2 = 0;
    private float manXPos3 = new Random().nextInt(100) + 150, manYPos3 = 0;
    private float boomX=30,boomY=0;
    //kích thước nhân vật
    private int frameWidth1 = 130, frameHeight1 = 170;
    private int frameWidth2 = 130, frameHeight2 = 170;
    private int frameWidth3 = 130, frameHeight3 = 130;
    private int frameWidth4 = 260, frameHeight4 = 300;
    //hiểu ứng cho nhân vật
    // =1 là không hiệu ứng
    private int frameCount = 1;
    //vị trí ban đầu của frame
    //> không thấy trên màn hình nữa
    private int currentFrame = 1;
    private long fps;
    private long timeThisFrame;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMillisecond = 500;
    private Rect frameBackGound = new Rect(0, 0, frameWidth1, frameHeight1);
    private Rect frameToDraw1 = new Rect(0, 0, frameWidth2, frameHeight1);
    private Rect frameToDraw2 = new Rect(0, 0, frameWidth2, frameHeight2);
    private Rect frameToDraw3 = new Rect(0, 0, frameWidth3, frameHeight3);
    private Rect frameToDraw4 = new Rect(0, 0, frameWidth4, frameHeight4);
    private RectF whereToDraw1 = new RectF(manXPos1, manYPos1,
            manXPos1 + frameWidth1, frameHeight1);
    private RectF whereToDraw2 = new RectF(manXPos2, manYPos2,
            manXPos2 + frameWidth2, frameHeight2);
    private RectF whereToDraw3 = new RectF(boomX, boomY,
            boomX + frameWidth3, frameHeight3);
    private RectF whereToDraw4 = new RectF(manXPos3, manYPos3,
            manXPos3 + frameWidth4, frameHeight4);
    private SurfaceView surfaceView1, surfaceView2, surfaceView3, surfaceView4;
    private TextView txt_score, btnControl;
    private static int score = 0;
    private static boolean beginning = false;
    private static final String ACTION_END_GAME = "ACTION_END_GAME";
    private static int countItem = 0;
    private static boolean isBoss = false;
    private static boolean bossDead;
    private static int timer;
    private static int TotalTime = 60;
    private static int TimeCount = 0;
    private static int dem = 0;
    private static int k;
    private Timer CoundownTimer;
    private TextView txtTimer;
    private Dialog dialog;
    private AlertDialog alertDialog;
    private static boolean isSet = false;
    private Paint paint;
    private Button btnclear;
    private TextView txtx1;
    private ImageView img_vk,img_vk2;
    private RelativeLayout rlt;
    MediaPlayer mediaPlayer;
    final Handler handler1 = new Handler();
    final Handler handler2 = new Handler();
    final Handler handler3 = new Handler();
    final Handler handler4 = new Handler();
    final Handler handler5 = new Handler();
    DatabaseReference dataa;
    private String simpleFileName = "note.txt";
    private int countClickItem = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        randombackground();
        dataa=FirebaseDatabase.getInstance().getReference();
        isSet = false;
        img_vk=findViewById(R.id.img_vk);
        img_vk2=findViewById(R.id.img_vk2);
        rlt=findViewById(R.id.rlt);
        surfaceView1 = findViewById(R.id.surfaceView);
        surfaceView2 = findViewById(R.id.surfaceView2);
        surfaceView3 = findViewById(R.id.surfaceView3);
        surfaceView4 = findViewById(R.id.surfaceView4);
        txt_score = findViewById(R.id.txt_score);
        btnControl = findViewById(R.id.btn_control);
        txtTimer = findViewById(R.id.txt_Timer);
        txt_score.setText("00");
        runSpeedPerSecond1 = new Random().nextInt(100) + runSpeedPerSecond1;
        runSpeedPerSecond2 = new Random().nextInt(100) + runSpeedPerSecond2;
        runSpeedPerSecond3 = new Random().nextInt(100) + runSpeedPerSecond3;
        runSpeedPerSecond4 = new Random().nextInt(100) + runSpeedPerSecond4;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mouse1);

        surfaceView1.setZOrderOnTop(true);
        surfaceView2.setZOrderOnTop(true);
        surfaceView3.setZOrderOnTop(true);
        surfaceView4.setZOrderOnTop(true);
        ourHolder1 = surfaceView1.getHolder();
        //làm cho nền holder trong suốt
        ourHolder1.setFormat(PixelFormat.TRANSPARENT);
        ourHolder2 = surfaceView2.getHolder();
        ourHolder2.setFormat(PixelFormat.TRANSPARENT);
        ourHolder3 = surfaceView3.getHolder();
        ourHolder3.setFormat(PixelFormat.TRANSPARENT);
        ourHolder4 = surfaceView4.getHolder();
        ourHolder4.setFormat(PixelFormat.TRANSPARENT);
        //hình ảnh vật thể tại điểm bắt đầu
        bitmapRunningMan1 =
                BitmapFactory.decodeResource(getResources(),
                        R.drawable.mouselv1);

        //hình ảnh vật thể khí di chuyển
        //khi di chuyển hàm này sẽ được thay đổi liên tục được truyền lại thuộc tính ở bên dưới
        bitmapRunningMan1 = Bitmap.createScaledBitmap(bitmapRunningMan1,
                frameWidth1 * frameCount, frameHeight1, false);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();



        int randomMouse=new Random().nextInt(3);
        if(randomMouse==0)
            bitmapRunningMan2 =
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.mouselv2);
        else if(randomMouse==1) bitmapRunningMan2 =
                BitmapFactory.decodeResource(getResources(),
                        R.drawable.mouselv1);
        else bitmapRunningMan2 =
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.mouselv3);
        bitmapRunningMan2 = Bitmap.createScaledBitmap(bitmapRunningMan2,
                frameWidth2, frameHeight2, false);


        int randomItem=new Random().nextInt(4);
        if(randomItem==0)
            bitmapRunningMan3 =
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.time);
        else if(randomItem==1)
            bitmapRunningMan3 =
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.freeze);
        else bitmapRunningMan3 =
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.boom);
        bitmapRunningMan3 = Bitmap.createScaledBitmap(bitmapRunningMan3,
                frameWidth3, frameHeight3, false);

        // bắt sự kiện click vào surfaceview
        surfaceView1.setOnTouchListener(new View.OnTouchListener() {
            int x=randomMouse;
            int k=randomItem;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //nếu chưa bắt đầu click vào bất kỳ đâu trong màn hình để bắt đầu game
                        if (!beginning) {
                            dem=0;
                            countItem = 3;
                            TotalTime = 60;
                            TimeCount =0;
                            score = 0;
                            timer = 60;
                            startTimer();
                            int p1 = new Random().nextInt(surfaceView1.getWidth());
                            manXPos1 = p1 > frameWidth1 ? p1 - frameWidth1 : p1 + frameWidth1;
                            manYPos1 = 0;
                            int p2 = new Random().nextInt(surfaceView1.getWidth());
                            manXPos2 = p2 > frameWidth1 ? p2 - frameWidth2 : p2 + frameWidth2;
                            manYPos2 = 0;
                            int p3 = new Random().nextInt(surfaceView3.getWidth());
                            boomX = p3 > frameWidth3 ? p3 - frameWidth3 : p3 + frameWidth3;
                            boomY=0;
                            beginning = true;
                            isMoving = true;
                            btnControl.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                        }
                        //Boss xuất hiện
                        /*if(score == 3 && !isBoss){
                            isBoss = true;
                            bitmapRunningMan4 =
                                    BitmapFactory.decodeResource(getResources(),
                                            R.drawable.mouselv1);
                            bitmapRunningMan4 = Bitmap.createScaledBitmap(bitmapRunningMan4,
                                    frameWidth4 * frameCount, frameHeight4, false);
                            int p4 = new Random().nextInt(surfaceView4.getWidth());
                            manXPos3 = p4 > frameWidth4 ? p4 - frameWidth4 : p4 + frameWidth4;
                            manYPos3 = 0;
                        }*/
                        //set vị trí cho vũ khí
                        float clickX=event.getX()*3;
                        float clickY=event.getY()*3;
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(pxToDp(clickX-img_vk.getWidth()),pxToDp(clickY-img_vk.getHeight()),0,0);
                        img_vk.setLayoutParams(params);
                        img_vk2.setLayoutParams(params);
                        img_vk.setVisibility(View.VISIBLE);

                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                img_vk.setVisibility(View.GONE);
                                img_vk2.setVisibility(View.VISIBLE);
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        img_vk2.setVisibility(View.GONE);
                                    }
                                }, 100);
                            }
                        }, 70);

                        //nếu click vào tọa độ trong khoảng tự tọa độ gốc của vật thể + chiểu dài và rộng của vật thể
                        //thì vật thể quay lại tọa độ y(theo chiều dọc màn hình) là 0
                        //và vị trí thé X(theo chiều ngang màn hình) được tạo ngẫu nhiên
                        if((event.getX() >= manXPos2 && event.getX() <= (manXPos2 + frameWidth2)) &&
                                (event.getY() >= manYPos2 && event.getY() <= manYPos2 + frameHeight2) && isMoving && x==2) {
                            new Handler().post(()->{
                                mediaPlayer.start();
                            });
                            bitmapRunningMan2=BitmapFactory.decodeResource(getResources(),
                                    R.drawable.mouselv2);
                            bitmapRunningMan2 = Bitmap.createScaledBitmap(bitmapRunningMan2,
                                    frameWidth2, frameHeight2, false);
                            x=0;
                        }
                        else if((event.getX() >= manXPos2 && event.getX() <= (manXPos2 + frameWidth2)) &&
                                (event.getY() >= manYPos2 && event.getY() <= manYPos2 + frameHeight2) && isMoving && x==0) {
                            new Handler().post(()->{
                                mediaPlayer.start();
                            });
                            bitmapRunningMan2=BitmapFactory.decodeResource(getResources(),
                                    R.drawable.mouselv1);
                            bitmapRunningMan2 = Bitmap.createScaledBitmap(bitmapRunningMan2,
                                    frameWidth2, frameHeight2, false);
                            x=1;
                        }

                        else if ((event.getX() >= manXPos2 && event.getX() <= (manXPos2 + frameWidth2)) &&
                                (event.getY() >= manYPos2 && event.getY() <= manYPos2 + frameHeight2) && isMoving && x==1) {
                            score += 1;
                            txt_score.setText(score + "");
                            //cham vào chuột thì sẽ phát ra tiêng s chít chít
                            new Handler().post(()->{
                                mediaPlayer.start();
                            });
                            bitmapRunningMan2 =
                                    BitmapFactory.decodeResource(getResources(),
                                            R.drawable.imgkilled);
                            bitmapRunningMan2 = Bitmap.createScaledBitmap(bitmapRunningMan2,
                                    frameWidth2 * frameCount, frameHeight2, false);
                            runSpeedPerSecond2=0;

                            handler5.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int random3=new Random().nextInt(3);
                                    if(random3==0)
                                        bitmapRunningMan2 =
                                                BitmapFactory.decodeResource(getResources(),
                                                        R.drawable.mouselv2);
                                        else if(random3==1) bitmapRunningMan2 =
                                                BitmapFactory.decodeResource(getResources(),
                                                        R.drawable.mouselv1);
                                        else bitmapRunningMan2 =
                                                    BitmapFactory.decodeResource(getResources(),
                                                            R.drawable.mouselv3);
                                        bitmapRunningMan2 = Bitmap.createScaledBitmap(bitmapRunningMan2,
                                                frameWidth2 , frameHeight2, false);
                                        // draw1();
                                        x=random3;
                                        runSpeedPerSecond2 = 75+5*(TimeCount);
                                        manXPos2 = new Random().nextInt(surfaceView2.getWidth() - frameWidth2);
                                        manYPos2 = 0;
                                }
                            }, 200);



                        }
                        if ((event.getX() >= manXPos1 && event.getX() <= (manXPos1 + frameWidth1)) &&
                                (event.getY() >= manYPos1 && event.getY() <= manYPos1 + frameHeight1) && isMoving) {
                            score +=1;
                            //Tạo hiệu ứng chuột bị đập
                            bitmapRunningMan1 =
                                    BitmapFactory.decodeResource(getResources(),
                                            R.drawable.imgkilled);
                            bitmapRunningMan1 = Bitmap.createScaledBitmap(bitmapRunningMan1,
                                    frameWidth1 * frameCount, frameHeight1, false);
                            runSpeedPerSecond1=0;

                            handler3.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bitmapRunningMan1 =
                                            BitmapFactory.decodeResource(getResources(),
                                                    R.drawable.mouselv1);
                                    bitmapRunningMan1 = Bitmap.createScaledBitmap(bitmapRunningMan1,
                                            frameWidth1 * frameCount, frameHeight1, false);
                                    runSpeedPerSecond1 = 75+5*(TimeCount);
                                    int prandom = new Random().nextInt(surfaceView1.getWidth()-frameWidth1);
                                    manXPos1 = prandom;
                                    manYPos1 = 0;
                                }
                            }, 100);
                            txt_score.setText(score + "");
                            new Handler().post(()->{
                                mediaPlayer.start();
                            });
                            //book selected
                        }
                        if ((event.getX() >= manXPos3 && event.getX() <= (manXPos3 + frameWidth4)) &&
                                (event.getY() >= manYPos3 && event.getY() <= manYPos3 + frameHeight4) && isMoving) {

                            dem++;
                            if(dem==10){
                                isBoss=false;
                                dem=0;
                                score += 10;
                                bitmapRunningMan4.eraseColor(Color.TRANSPARENT);
                                txt_score.setText(score + "");
                                new Handler().post(()->{
                                    mediaPlayer.start();
                                });
                            }
                        }

                        if(boomY >= surfaceView3.getHeight() - frameHeight3){
                            int random6=new Random().nextInt(4);
                            if(random6==0){
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.time);
                                k=0;
                            }
                            else if(random6==1){
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.freeze);
                                k=1;
                            }
                            else{
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.boom);
                                k=2;
                            }
                            bitmapRunningMan3 = Bitmap.createScaledBitmap(bitmapRunningMan3,
                                    frameWidth3 * frameCount, frameHeight3, false);
                            boomX = new Random().nextInt(surfaceView3.getWidth() - frameWidth3);
                            boomY = 0;
                        }

                        if ((event.getX() >= boomX && event.getX() <= (boomX + frameWidth3)) &&
                                (event.getY() >= boomY && event.getY() <= boomY + frameHeight3) && isMoving &&(k==2||k==3))
                            endGame();
                        else if((event.getX() >= boomX && event.getX() <= (boomX + frameWidth3)) &&
                                (event.getY() >= boomY && event.getY() <= boomY + frameHeight3) && isMoving &&(k==0||k==1)){
                            if(k==1){
                                runSpeedPerSecond1 = 0;
                                runSpeedPerSecond2 = 0;
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        runSpeedPerSecond1 = 75+5*(TimeCount);
                                        runSpeedPerSecond2 = 75+5*(TimeCount);
                                    }
                                }, 3000);
                            }

                            else{
                                timer+=5;
                            }
                            int random7=new Random().nextInt(4);
                            if(random7==0){
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.time);
                                k=0;
                            }
                            else if(random7==1){
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.freeze);
                                k=1;
                            }
                            else{
                                bitmapRunningMan3 =
                                        BitmapFactory.decodeResource(getResources(),
                                                R.drawable.boom);
                                k=2;
                            }
                            bitmapRunningMan3 = Bitmap.createScaledBitmap(bitmapRunningMan3,
                                    frameWidth3 * frameCount, frameHeight3, false);
                            boomX = new Random().nextInt(surfaceView3.getWidth() - frameWidth3);
                            boomY = 0;
                        }
                        break;
                }
                return true;
            }
        });
        //button pasue hoặc tiếp tục game
        btnControl.setOnClickListener((View v) -> {
            if (isMoving) {
                CoundownTimer.cancel();
                CoundownTimer.purge();
                btnControl.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                isMoving = false;
                pause();
            } else {
                startTimer();
                resume();
                isMoving = true;
                btnControl.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            }
        });
        btnclear=findViewById(R.id.btnclear);
        txtx1=findViewById(R.id.x1);
        btnclear.setOnClickListener((View v)->{
            score += 2;
            manXPos1 = new Random().nextInt(surfaceView1.getWidth() - frameWidth1);
            manYPos1 = 0;
            manXPos2 = new Random().nextInt(surfaceView2.getWidth() - frameWidth2);
            manYPos2 = 0;
            btnclear.setVisibility(View.GONE);
            txtx1.setVisibility(View.GONE);
        });
    }

    public void startTimer() {
        CoundownTimer = new Timer();
        CoundownTimer.schedule(new timer(), 1000, 1000);
    }

    public void endGame() {
        CoundownTimer.cancel();
        CoundownTimer = null;
        isMoving = false;
        Intent intent = new Intent();
        intent.setAction(ACTION_END_GAME);
        sendBroadcast(intent);
        pause();

    }

    public void showDialogStatus() {
        Rect displayRectangle = new Rect();
        View view = getLayoutInflater().inflate(R.layout.dialog_endgame, null, false);
        view.setMinimumWidth((int) (displayRectangle.width() * 0.5f));
        view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
        ImageView btnPlay = view.findViewById(R.id.btnPlay);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView txtScore = view.findViewById(R.id.txt_score);
        TextView txtStatus = view.findViewById(R.id.txt_status_dialog);
        LinearLayout linearLayoutControll = view.findViewById(R.id.layout_controll);
        LinearLayout linearLayoutWriteName = view.findViewById(R.id.layout_write_name);
        EditText edtWriteName = view.findViewById(R.id.edt_write_name);
        ImageView btnSkip = view.findViewById(R.id.btn_skip);
        ImageView btnOK = view.findViewById(R.id.btn_Ok);

        if (score > HightScoreController.getMinimumScore()) {
            linearLayoutWriteName.setVisibility(View.VISIBLE);
            linearLayoutControll.setVisibility(View.GONE);
        }

        txtScore.setText(score + "");
        //chơi lại- bắt đầu lại game
        btnPlay.setOnClickListener((View v) -> {
            runSpeedPerSecond1 = 50;
            runSpeedPerSecond2 = 50;
            runSpeedPerSecond3 = 50;
            score = 0;
            timer = 60;
            resume();
            startTimer();
            int p1 = new Random().nextInt(surfaceView1.getWidth());
            manXPos1 = p1 > frameWidth1 ? p1 - frameWidth1 : p1 + frameWidth1;
            manYPos1 = 0;
            int p2 = new Random().nextInt(surfaceView2.getWidth());
            manXPos2 = p2 > frameWidth2 ? p2 - frameWidth2 : p2 + frameWidth2;
            manYPos2 = 0;
            int p3 = new Random().nextInt(surfaceView3.getWidth());
            boomX = p3 > frameWidth3 ? p3 - frameWidth3 : p3 + frameWidth3;
            boomY = 0;
            beginning = true;
            randombackground();
            btnControl.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            isMoving = true;
            dialog.dismiss();
            btnclear.setVisibility(View.VISIBLE);
            txtx1.setVisibility(View.VISIBLE);
        });

        //thoát ra màn hình ngoài
        btnBack.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        //lưu điểm lại
        btnOK.setOnClickListener((View v) -> {
            ScoreOBJS scoreOBJS = new ScoreOBJS();
            scoreOBJS.setScore(score);
            String name = edtWriteName.getText().toString().equals("") ? "No Name" : edtWriteName.getText().toString();
            scoreOBJS.setName(name);
            HightScoreController.addScore(scoreOBJS);
            if(score==HightScoreController.getMaximumScore()){
                String keyID = dataa.push().getKey();
                //ghi file (đã ghi được)
                /*try {
                    // Open Stream to write file.
                    FileOutputStream out = this.openFileOutput(simpleFileName, MODE_PRIVATE);
                    // Ghi dữ liệu.
                    out.write(keyID.getBytes());
                    out.close();
                    Toast.makeText(this,"File saved!",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                }*/
                ScoreFirebase scoreFirebase=new ScoreFirebase(name, score);
                dataa.child(keyID).setValue(scoreFirebase);
            }

            linearLayoutWriteName.setVisibility(View.GONE);
            linearLayoutControll.setVisibility(View.VISIBLE);
        });

        btnSkip.setOnClickListener((View v) -> {
            linearLayoutWriteName.setVisibility(View.GONE);
            linearLayoutControll.setVisibility(View.VISIBLE);
        });

        alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false).setView(view).create();
        dialog = alertDialog;
        dialog.show();
    }


    //hàm cập nhật thời gian
    public class timer extends TimerTask {
        @Override
        public void run() {
            timer -= 1;
            TimeCount += 1;
            runSpeedPerSecond1+=5;
            runSpeedPerSecond2+=5;
            runSpeedPerSecond3+=10;
            runSpeedPerSecond4+=5;
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            txtTimer.setText(timer + "");
            if (timer == 0) {
                endGame();
            }
            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        resume();
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_END_GAME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }
    }

    //kiểm tra vị trí của nhân vật theo trục Y trong thời gian chơi
    public void update() {
        if (isMoving) {
            manYPos1 = manYPos1 + runSpeedPerSecond1 / fps;
            manYPos2 = manYPos2 + runSpeedPerSecond2 / fps;
            boomY = boomY + runSpeedPerSecond3 / fps;
            manYPos3 = manYPos3 + runSpeedPerSecond4 / fps;
            //nếu chạy hết màn hình end game
            if (manYPos1 >= surfaceView1.getHeight() - frameHeight1 || manYPos2 >= surfaceView2.getHeight() - frameHeight2)
                endGame();

        }
    }
    //liên tục tăng - đây chính là phần tạo cho vật thể chuyển động
    public void manageCurrentFrame() {
        long time = System.currentTimeMillis();

        if (isMoving) {
            if (time > lastFrameChangeTime + frameLengthInMillisecond) {
                lastFrameChangeTime = time;
                //biến quyêt định chuột đang ở đâu trên màn hình
                currentFrame++;

                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                }
            }
        }

        frameToDraw2.left = currentFrame * frameWidth2;
        frameToDraw2.right = frameToDraw2.left + frameWidth2;

        frameToDraw1.left = currentFrame * frameWidth1;
        frameToDraw1.right = frameToDraw1.left + frameWidth1;

        frameToDraw3.left = currentFrame * frameWidth3;
        frameToDraw3.right = frameToDraw3.left + frameWidth3;

        frameToDraw4.left = currentFrame * frameWidth4;
        frameToDraw4.right = frameToDraw4.left + frameWidth4;
    }

    //hiện vị trí của chuột theo thời gian thực
    // thẻ hiện bằng tọa độ theo trục Y và X
    public void draw() {
        if (ourHolder1.getSurface().isValid()) {
            canvas1 = ourHolder1.lockCanvas();
            canvas1.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            whereToDraw1.set((int) manXPos1, (int) manYPos1, (int) manXPos1
                    + frameWidth1, (int) manYPos1 + frameHeight1);
            canvas1.drawBitmap(bitmapRunningMan1, frameToDraw1, whereToDraw1, null);
            ourHolder1.unlockCanvasAndPost(canvas1);

            canvas2 = ourHolder2.lockCanvas();
            canvas2.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            whereToDraw2.set((int) manXPos2, (int) manYPos2, (int) manXPos2
                    + frameWidth2, (int) manYPos2 + frameHeight2);
            canvas2.drawBitmap(bitmapRunningMan2, frameToDraw2, whereToDraw2, null);
            ourHolder2.unlockCanvasAndPost(canvas2);

            canvas3 = ourHolder3.lockCanvas();
            canvas3.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            whereToDraw3.set((int) boomX, (int) boomY, (int) boomX
                    + frameWidth3, (int) boomY + frameHeight3);
            canvas3.drawBitmap(bitmapRunningMan3, frameToDraw3, whereToDraw3, null);
            ourHolder3.unlockCanvasAndPost(canvas3);

            if(isBoss){
                canvas4 = ourHolder4.lockCanvas();
                canvas4.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                whereToDraw4.set((int) manXPos3, (int) manYPos3, (int) manXPos3
                        + frameWidth4, (int) manYPos3 + frameHeight4);
                canvas4.drawBitmap(bitmapRunningMan4, frameToDraw4, whereToDraw4, null);
                ourHolder4.unlockCanvasAndPost(canvas4);

            }
            manageCurrentFrame();
        }
    }

    public void draw1(){

    }

    //tạm dừng game
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("ERR", "Joining Thread");
        }
    }

    //sẵn sàng chơi game
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //    cập nhật lại giao diện
    public void updateView() {
        beginning = false;
        showDialogStatus();
        btnControl.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    //bắt kết quả end game và cập nhật lại giao diện
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateView();
        }
    };

    public void randombackground() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.linear1);
        int random = new Random().nextInt(4);
        switch (random) {
            case 0:
                layout.setBackgroundResource(R.drawable.backgroundgame);
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.backgroundgame2);
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.backgroundgame3);
                break;
            case 3:
                layout.setBackgroundResource(R.drawable.backgroundgame4);
                break;
            default:
                break;
        }
    }
    public static int pxToDp(float px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
