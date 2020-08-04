package com.example.afinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Problem10 extends AppCompatActivity {

    String savefile = "savedata";
    ImageView completed, problem;
    EditText entry;
    Button next, submit;
    int permissions;
    int levelnum = 10;
    String answer = "5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem10);

        //get intent info
        Intent intent = getIntent();
        permissions = intent.getIntExtra(Menu.MSGKEY, 0);

        //get objects to kill afterwards
        next = findViewById(R.id.next);
        completed = findViewById(R.id.pass);
        problem = findViewById(R.id.imageView);

        //properly set next button
        ConstraintLayout.LayoutParams set = (ConstraintLayout.LayoutParams) next.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float windowheight = (float) displayMetrics.heightPixels;
        set.topMargin = (int) (windowheight * (3.0 / 5.0));
        next.setLayoutParams(set);

        //get useful objects
        entry = findViewById(R.id.input);
        submit = findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = entry.getText().toString();
                if (temp.equals(answer))
                    fadeIn();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if (permissions < levelnum + 1)
                    writePerm(levelnum + 1);
                finish();
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

    private void fadeIn() {
        //set visibility/interactive nature of objects to GONE
        int fadeDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        submit.setVisibility(View.GONE);
        entry.setVisibility(View.GONE);
        problem.setVisibility(View.GONE);
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
}
