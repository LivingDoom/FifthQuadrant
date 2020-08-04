package com.example.afinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Menu extends AppCompatActivity {

    public static final String MSGKEY = "com.example.afinal.MESSAGE";
    int[] rows = new int[] {R.id.row1, R.id.row2, R.id.row3};
    java.lang.Class[] make = new java.lang.Class[] {Problem1.class, Problem2.class, Problem3.class, Problem4.class,
            Problem5.class, Problem6.class, Problem7.class, Problem8.class, Problem9.class, Problem10.class};
    int[] ProblemOrder = new int[] {R.id.p1, R.id.p2, R.id.p3, R.id.p4, R.id.p5, R.id.p6, R.id.p7, R.id.p8, R.id.p9, R.id.p10};
    int totalProblems;
    public int permissions;
    String savefile = "savedata";
    FileInputStream fis = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //modify local storage
        //try read, create file if it does not exist
        try { fis = this.openFileInput(savefile); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        //create file if not found
        if (fis == null) {
            File temp = new File(this.getFilesDir(), savefile);
            FileWriter writer = null;
            try {
                //set initial level permission to 1
                writer = new FileWriter(temp);
                writer.append("1");
                writer.flush();
                writer.close();
            } catch (IOException e) { e.printStackTrace(); }
            try {
                fis = this.openFileInput(savefile);
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }

        //get permissions
        try {
            readPerm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < permissions && i < ProblemOrder.length; i++) {
            Button mod = findViewById(ProblemOrder[i]);
            mod.setBackgroundColor(Color.parseColor("#000000"));
        }

        //set variables
        totalProblems = ProblemOrder.length;

        //get and set display
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int len = displayMetrics.widthPixels;
        for (int i = 0; i < rows.length; i++) {
            TableRow change = findViewById(rows[i]);
            change.setMinimumHeight(len / 4);
        }

        //set buttons
        for (int i = 0; i < totalProblems; i++) {
            //get each problem button and set it to enter its problem
            Button button = findViewById(ProblemOrder[i]);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterProblem(finalI);
                }
            });
        }
        //reset button
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePerm(1);
                finish();
                startActivity(getIntent());
            }
        });
    }

    public void enterProblem(int num) {
        if (num < permissions) {
            Intent intent = new Intent(this, make[num]);
            intent.putExtra(MSGKEY, permissions);
            this.startActivity(intent);
        }
    }

    //writing a new permissions level
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void readPerm() throws IOException {
        //setup variables for reading
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            char item = (char) reader.read();
            while ((int) item > 47 && (int) item < 58) {
                stringBuilder.append(item);
                item = (char) reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            String contents = stringBuilder.toString();
            if (contents != "")
                permissions = Integer.parseInt(contents);
            inputStreamReader.close();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}