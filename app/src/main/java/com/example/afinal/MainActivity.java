package com.example.afinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    //necessary variables
    public static final String MSGKEY = "com.example.afinal.MESSAGE";
    java.lang.Class make = Menu.class;
    java.lang.Class[] makeables = new java.lang.Class[] {Problem1.class, Problem2.class, Problem3.class, Problem4.class,
            Problem5.class, Problem6.class, Problem7.class, Problem8.class, Problem9.class, Problem10.class};
    int maxLevel = makeables.length;
    String savefile = "savedata";
    int permissions = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //enter menu
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMenu();
            }
        });

        //enact the continue function
        Button cont = findViewById(R.id.continueLevel);
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentLevel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //quit if quit is pressed
        Button quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startMenu() {
        //launch menu activity
        Intent intent = new Intent(this, Menu.class);
        this.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void currentLevel() throws IOException {

        //try reading permissions, create new file if it doesn't exist already
        FileInputStream fis = null;
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

        //read in permissions level from file
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

        //adjust permissions to account for potentially too high of permission level
        int attemptedLevel = permissions;
        if (attemptedLevel > maxLevel)
            attemptedLevel = maxLevel;

        //start menu so that back arrows redirect properly
        Intent startup = new Intent(this, make);
        this.startActivity(startup);

        //launch last level unlocked (or last level in game, if permissions too high)
        Intent intent = new Intent(this, makeables[attemptedLevel - 1]);
        intent.putExtra(MSGKEY, permissions);
        this.startActivity(intent);
    }
}