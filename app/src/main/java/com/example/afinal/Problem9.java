package com.example.afinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Problem9 extends AppCompatActivity {

    //info - set
    int[] keyid = new int[] {R.id.key1, R.id.key2, R.id.key3, R.id.key4};
    int[] targetid = new int[] {R.id.target1, R.id.target2, R.id.target3, R.id.target4, R.id.target5, R.id.target6, R.id.target7, R.id.target8};
    int totalKeys = 4;
    int totalTargets = 8;
    String savefile = "savedata";
    int permissions;
    int levelnum = 9;

    //objects - get on create
    ImageView[] keys = new ImageView[totalKeys];
    ImageView[] targets = new ImageView[totalTargets];
    ImageView completed;
    Button next, submit;
    int[] flags = new int[] { -1, -1, -1, -1 };

    //dimensions and durations
    int windowwidth;
    int windowheight;
    int endheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem9);

        //get permissions
        Intent intent = getIntent();
        permissions = intent.getIntExtra(Menu.MSGKEY, 0);

        //get display info
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windowheight = displayMetrics.heightPixels;
        windowwidth = displayMetrics.widthPixels;
        endheight = windowheight - (windowheight / 8);

        //get objects
        completed = findViewById(R.id.pass);
        next = findViewById(R.id.next);
        submit = findViewById(R.id.submit);
        for (int i = 0; i < totalKeys; i++) {
            keys[i] = findViewById(keyid[i]);
            targets[i] = findViewById(targetid[i]);
            targets[i + (totalTargets - totalKeys)] = findViewById(targetid[i + (totalTargets - totalKeys)]);
        }

        //properly set next button
        RelativeLayout.LayoutParams set = (RelativeLayout.LayoutParams) next.getLayoutParams();
        set.topMargin = (int) ((float)windowheight * (3.0 / 5.0));
        next.setLayoutParams(set);

        //setup button listener
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if (permissions < levelnum + 1)
                    writePerm(levelnum + 1);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkOn(flags))
                    fadeIn();
            }
        });

        //set listeners for movables
        for (int i = 0; i < totalKeys; i++) {
            setMovingListener(i);
        }
    }

    //function pass for listener creation
    private void setMovingListener(final int num) {
        //set listener values
        View.OnTouchListener listen = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int range = 80;
                RelativeLayout.LayoutParams set = (RelativeLayout.LayoutParams) keys[num].getLayoutParams();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //on mouse release, check for target
                    for (int i = 0; i < totalTargets; i++) {
                        RelativeLayout.LayoutParams aim = (RelativeLayout.LayoutParams) targets[i].getLayoutParams();
                        if (Math.abs(set.leftMargin - aim.leftMargin) < range && Math.abs(set.topMargin - aim.topMargin) < range) {
                            set.leftMargin = aim.leftMargin;
                            set.topMargin = aim.topMargin;
                            keys[num].setLayoutParams(set);
                            flags[num] = i;
                            break;
                        }
                    }
                } else {
                    //de-set completed status
                    flags[num] = -1;
                    //get dimensions of box (square, only need 1)
                    int boxh = keys[num].getHeight();
                    //get pixelheight of status bar
                    Resources r = getResources();
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, r.getDisplayMetrics());
                    //do movement
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //get mouse values
                            int x_cord = (int) event.getRawX();
                            int y_cord = (int) event.getRawY();
                            //set borders (navigation bar size on edges, large bottom cutoff)
                            if (x_cord > windowwidth - (boxh + 5))
                                x_cord = windowwidth - (boxh + 5);
                            if (x_cord < 5)
                                x_cord = 5;
                            if (y_cord > endheight - boxh)
                                y_cord = endheight - boxh;
                            if (y_cord < px + 5)
                                y_cord = (int) (px + 5);
                            //change position
                            set.leftMargin = x_cord;
                            set.topMargin = y_cord - ((int) px);
                            keys[num].setLayoutParams(set);
                            break;
                    }
                }
                return true;
            }
        };
        //set listener to passed object
        keys[num].setOnTouchListener(listen);
    }

    private boolean checkOn(int[] arr) {
        boolean[] checks = new boolean[8];
        checks[1] = true; checks[2] = true; checks[3] = true; checks[4] = true;
        for (int i = 0; i < 4; i++) {
            if (flags[i] == -1)
                return false;
            else
                checks[flags[i]] = true;
        }
        if (alltrue(checks))
            return true;
        return false;
    }

    private boolean alltrue(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i])
                return false;
        }
        return true;
    }

    private void fadeIn() {
        //set visibility/interactive nature of objects to GONE
        int fadeDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        for (int i = 0; i < keys.length; i++)
            keys[i].setVisibility(View.GONE);
        for (int i = 0; i < targets.length; i++)
            targets[i].setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        //animate in completed screen
        completed.setVisibility(View.VISIBLE);
        completed.setAlpha(0f);
        completed.animate().alpha(1f).setDuration(fadeDuration).setListener(new AnimatorListenerAdapter() {
            //one second after completed screen appears, display 'next' button
            @Override
            public void onAnimationEnd(Animator animation) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        next.setVisibility(View.VISIBLE);
                    }
                }, 1000);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void writePerm(int permLevel) {
        String fileContents = Integer.toString(permLevel);
        try (FileOutputStream fos = this.openFileOutput(savefile, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
