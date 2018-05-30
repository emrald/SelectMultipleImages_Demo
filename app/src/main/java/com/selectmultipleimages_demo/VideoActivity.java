package com.selectmultipleimages_demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;


//import com.googlecode.javacv.FrameRecorder;

//import com.googlecode.javacv.FFmpegFrameRecorder;

/**
 * Created by TI A1 on 25-05-2018.
 */

public class VideoActivity extends AppCompatActivity {

    ImageView img;
    Bundle bn;
    ArrayList<String> images_list;
    AnimationDrawable animation;
    CountDownTimer timer = null;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    Button btnstop, btnstart, btnpause;
    boolean isStart;
    boolean looping;
    // private AnimationDrawable sphereAnimation;
    private AnimationDrawable sphereResume;
    private AnimationDrawable activeAnimation;
    private Drawable currentFrame;
    private Drawable checkFrame;
    private int frameIndex;
    boolean pause_flag = false;
    ProgressBar simpleProgressBar;
    String uri = "";
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        img = (ImageView) findViewById(R.id.img);
        btnstop = (Button) findViewById(R.id.btnstop);
        btnstart = (Button) findViewById(R.id.btnstart);
        btnpause = (Button) findViewById(R.id.btnpause);

        bn = getIntent().getExtras();
        if (bn != null) {
            images_list = bn.getStringArrayList("images_list");
            Log.e("images_list", images_list.size() + "");
            uri = bn.getString("uri");
            Log.e("uri", uri + "");
        }
        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar); // initiate the progress bar
        simpleProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        Log.e("Value...", images_list.size() * 1000 + "");
        final Bitmap bmp = BitmapFactory.decodeFile(images_list.get(0));
        img.setImageBitmap(bmp);
        //Set ProgressBar maximum value
        //ProgressBar range (0 to maximum value)

        //Display the CountDownTimer initial value
        //  isStart = true;
        /*new CountDownTimer(images_list.size() * 5000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Another one second passed
                //Each second ProgressBar progress counter added one
                if(isStart)
                {
                    progressStatus += 1;
                }

                Log.e("progressStatus", progressStatus + "");
                simpleProgressBar.setProgress(progressStatus);
            }

            public void onFinish() {
                //Do something when count down end.
                progressStatus += 1;
                Log.e("progressStatus finish", progressStatus + "");
                simpleProgressBar.setProgress(progressStatus);
            }
        }.start();*/


        /* for (int i = 0; i < images_list.size(); i++) {

            animation.addFrame(Drawable.createFromPath(images_list.get(i)), 1000);
            //your code here
        }
        animation.setOneShot(true);
        img.setBackgroundDrawable(animation);
       // img.post(new Starter());
        animation.start();*/

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // animation.stop();
               /* ((AnimationDrawable)(img.getBackground())).stop();
                img.setBackgroundDrawable(null);
             //   img.setBackgroundResource(animation);
                isStart = false;*/

                btnstart.setEnabled(true);
                btnpause.setEnabled(false);
                img.setImageBitmap(bmp);
                stop();
                isStart = false;
                if(mp!=null)
                {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            }
        });
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
                btnstart.setEnabled(false);
                btnpause.setEnabled(true);
                img.setImageBitmap(null);
                simpleProgressBar.setMax(images_list.size());
                progressStatus = 0;
                //   animation.start();
               /* ((AnimationDrawable)(img.getBackground())).start();
                img.setBackgroundDrawable(animation);
                isStart = true;*/
                isStart = true;
                new CountDownTimer(images_list.size() * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        //Another one second passed
                        //Each second ProgressBar progress counter added one
                        if (isStart) {
                            progressStatus += 1;
                        }

                        Log.e("progressStatus", progressStatus + "");
                        simpleProgressBar.setProgress(progressStatus);
                    }

                    public void onFinish() {
                        //Do something when count down end.
                        progressStatus += 1;
                        Log.e("progressStatus finish", progressStatus + "");
                        simpleProgressBar.setProgress(progressStatus);
                        btnstart.setEnabled(true);
                        btnpause.setEnabled(false);
                        if(mp!=null)
                        {
                            mp.stop();
                            mp.release();
                            mp = null;
                        }
                    }
                }.start();


                try {
                    mp = new MediaPlayer();
                    mp.setDataSource(VideoActivity.this, Uri.parse(uri));
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Message..", e.getMessage() + "...");
                }

               /* player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        player.start();
                    }
                });

                player.prepareAsync();*/
            }
        });
        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                btnstart.setEnabled(true);
                btnpause.setEnabled(false);
                img.setImageBitmap(null);
                isStart = false;
                if(mp.isPlaying() && mp!=null)
                {
                    mp.pause();
                }
            }
        });
    }

    /*private void pause()
    {
        looping = false;
        sphereResume = new AnimationDrawable();
       // sphereAnimation = animation;
        animation.stop();
        currentFrame = animation.getCurrent();

        frameLoop:
        for(int i = 0; i < sphereAnimation.getNumberOfFrames(); i++)
        {
            checkFrame = animation.getFrame(i);

            if(checkFrame == currentFrame)
            {
                frameIndex = i;
                for(int k = frameIndex; k < animation.getNumberOfFrames(); k++)
                {
                    Drawable frame = animation.getFrame(k);
                    sphereResume.addFrame(frame, 1000);
                }
                for(int k = 0; k < frameIndex; k++)
                {
                    Drawable frame = animation.getFrame(k);
                    sphereResume.addFrame(frame, 1000);
                }
                animation = sphereResume;
                img.setImageDrawable(animation);
                img.invalidate();
                break frameLoop;
            }
        }
    }*/
    private void pause() {
        pause_flag = true;
        looping = false;
        sphereResume = new AnimationDrawable();
        currentFrame = activeAnimation.getCurrent();
        activeAnimation.stop();
        Log.e("currentFrame", currentFrame + "");
        Log.e("activeAnimation size", activeAnimation.getNumberOfFrames() + "");

        frameLoop:
        for (int i = 0; i < activeAnimation.getNumberOfFrames(); i++) {
            checkFrame = activeAnimation.getFrame(i);
            Log.e("current active frame", currentFrame + "");

            if (checkFrame == currentFrame) {
                frameIndex = i;
                for (int k = frameIndex; k < activeAnimation.getNumberOfFrames(); k++) {
                    Drawable frame = activeAnimation.getFrame(k);
                    sphereResume.addFrame(frame, 50);
                }
               /* for (int k = 0; k < frameIndex; k++) {
                    Drawable frame = activeAnimation.getFrame(k);
                    sphereResume.addFrame(frame, 50);
                }*/
                activeAnimation = sphereResume;
                img.setBackgroundDrawable(activeAnimation);
                img.invalidate();
                break frameLoop;
            }
        }
    }

    private void play() {
        // activeAnimation.setOneShot(true);
        //  activeAnimation.start();

        looping = false;

        activeAnimation = new AnimationDrawable();
        if (pause_flag == true) {
            for (int i = frameIndex; i < images_list.size(); i++) {

                activeAnimation.addFrame(Drawable.createFromPath(images_list.get(i)), 1000);
                //your code here
            }
            pause_flag = false;
        } else {
            for (int i = 0; i < images_list.size(); i++) {

                activeAnimation.addFrame(Drawable.createFromPath(images_list.get(i)), 1000);
                //your code here
            }
        }
        img.setBackgroundDrawable(activeAnimation);

        activeAnimation.setOneShot(true);

        // img.post(new Starter());
        activeAnimation.start();
        //  animation = activeAnimation;
    }

    private void stop() {
        progressStatus = 0;
        simpleProgressBar.setMax(0);
        simpleProgressBar.setProgress(progressStatus);
        pause_flag = false;
        looping = false;
        activeAnimation.stop();
        activeAnimation = animation;
        img.setBackgroundDrawable(activeAnimation);
    }

    private void loop() {
        looping = true;
        //      stopSoundEffect();
        activeAnimation.setOneShot(false);
        activeAnimation.start();
    }

    /* private void play()
     {
         animation = new AnimationDrawable();
         looping = false;
         for (int i = 0; i < images_list.size(); i++) {

             animation.addFrame(Drawable.createFromPath(images_list.get(i)), 1000);
             //your code here
         }
         animation.setOneShot(true);
         img.setBackgroundDrawable(animation);
         // img.post(new Starter());
         animation.start();
     }
     private void stop()
     {
         looping = false;
         animation.stop();
         animation = sphereAnimation;
         img.setImageDrawable(animation);
     }

     private void loop()
     {
         looping = true;
     //    stopSoundEffect();
         animation.setOneShot(false);
         animation.start();
     }*/
    public static boolean canCancelAnimation() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    class Starter implements Runnable {
        public void run() {
            animation.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VideoActivity.this, GalleryDemo.class);
        startActivity(intent);
        finish();
    }
}
