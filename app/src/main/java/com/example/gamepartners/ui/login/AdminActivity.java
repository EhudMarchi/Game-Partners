package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    public Chip reality, pc, playstation, xbox;
    public boolean realityCheck = false, pcCheck = false, playstationCheck = false, xboxCheck = false;
    EditText addGameName, gameImageURL, deleteGameName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        addGameName = findViewById(R.id.newGameName);
        gameImageURL = findViewById(R.id.newGameImageURL);
        deleteGameName = findViewById(R.id.deleteGameName);
        reality = findViewById(R.id.reality);
        pc = findViewById(R.id.pc);
        playstation = findViewById(R.id.playstation);
        xbox = findViewById(R.id.xbox);
        setPlatformsManagement();
    }
    private void setPlatformsManagement() {
        reality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realityCheck = !realityCheck;
                if(realityCheck == true) {
                    reality.setAlpha(1f);
                }
                else
                {
                    reality.setAlpha(0.4f);
                }
            }
        });
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pcCheck = !pcCheck;
                if(pcCheck == true) {
                    pc.setAlpha(1f);
                }
                else
                {
                    pc.setAlpha(0.4f);
                }
            }
        });
        playstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playstationCheck = !playstationCheck;
                if(playstationCheck == true) {
                    playstation.setAlpha(1f);
                }
                else
                {
                    playstation.setAlpha(0.4f);
                }
            }
        });
        xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xboxCheck = !xboxCheck;
                if(xboxCheck == true) {
                    xbox.setAlpha(1f);
                }
                else
                {
                    xbox.setAlpha(0.4f);
                }
            }
        });
    }

    public void addGame(View view) {
        if(validateGame()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference("games").child(addGameName.getText().toString());
            Game newGame = new Game(addGameName.getText().toString(), gameImageURL.getText().toString(), Game.ePlatform.PC);
            myRef.setValue(newGame).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getBaseContext(), addGameName.getText().toString() + " Added Successfully!",
                            Toast.LENGTH_LONG).show();
                    addGameName.setText("");
                    gameImageURL.setText("");
                }
            });
        }
        else
        {
            Toast.makeText(getBaseContext(), "Please Enter Game Name!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateGame() {
        boolean isValid = false;
        if(!addGameName.getText().toString().equals(""))
        {
            isValid = true;
        }
        return isValid;
    }

    public void deleteGame(View view) {
    }
}