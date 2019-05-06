package com.example.mobileninja;

import android.Manifest;
import android.app.ActionBar;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.icu.text.AlphabeticIndex;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Level1 extends AppCompatActivity {

    public ImageView enemy;
    public ImageView player;
    public List<ImageView> enemies = new ArrayList<>();

    MediaRecorder mic = new MediaRecorder();

    boolean gameRunning = true;

    long lastTime = System.nanoTime();
    double TicksPerSecond = 60.0;
    double ns = 1000000000;
    double delta = 0;
    long timer = System.currentTimeMillis();
    int spawnTimer;
    int frames = 0;
    double mEMA;
    float lastAttack = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

      // if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
      //     ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO});
       //}

       mic.setAudioSource(MediaRecorder.AudioSource.MIC);
       mic.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
       mic.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
       mic.setOutputFile("dev/null");
        try {
            mic.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mic.start();


        player = (ImageView) findViewById(R.id.imageView);
        player.setImageResource(R.drawable.ninja);

        enemy = findViewById(R.id.imageView2);
        SpawnEnemy();
        enemies.add(enemy);


        GameLoop();


    }


    public void GameLoop() {

    Thread loop = new Thread(new Runnable() {
    @Override
    public void run() {
        while(gameRunning){


                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;



            if (delta >= 1/TicksPerSecond){
                Tick();
                delta= delta-1/TicksPerSecond;
                frames++;
              //  spawnTimer++;
                lastAttack++;
            }

            if(spawnTimer >= 300) {
                SpawnEnemy();
                spawnTimer = 0;
            }


            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }


        }
    }
});
loop.start();


    }

    public void SpawnEnemy(){
      //  ImageView newEnemy = new ImageView(this);
     //   newEnemy.setImageResource(R.drawable.pirat);
    //    newEnemy.setX(enemy.getX());
   //     newEnemy.setY(enemy.getY());

        enemy.setX(2000);
        spawnTimer = 0;



    }

    public void Tick() {

            for (ImageView enemy:enemies) {
                enemy.setX(enemy.getX()-8);
            }

            Attack();

            if(Math.abs(enemy.getX() - player.getX()) <= 20)
            {
                GameOver();
            }
    }

    public void Attack() {

        if (lastAttack > 60)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setImageResource(R.drawable.ninja);
                }
            });

            //((ImageView)findViewById(R.id.imageView)).setImageResource(R.drawable.attack);
           // player.setImageResource(R.drawable.ninja);
            if(20*Math.log10(GetAmplitudeEMA()/ 0.0000001) >= 215)
            {

                if(Math.abs(enemy.getX() - player.getX()) <= 400) {
                    SpawnEnemy();
                    System.out.println("ATTAACK");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        player.setImageResource(R.drawable.attack);
                    }
                });
                lastAttack = 0;
            }
        }

    }

    public void GameOver()
    {
        System.out.println("GAME OVER");
        startActivity(new Intent(Level1.this, MainActivity.class));
    }


    public double GetAmplitude(){
        if(mic != null)
        {

            return (mic.getMaxAmplitude());
        }
        else{
            return  0;
        }
    }

    public double GetAmplitudeEMA() {
        double amp =  GetAmplitude();
        mEMA = 0.6 * amp + (1.0 - 0.6) * mEMA;
        return mEMA;
    }


}

