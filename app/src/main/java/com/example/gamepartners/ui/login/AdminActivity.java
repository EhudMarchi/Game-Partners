package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game.Game;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    public Chip reality, pc, playstation, xbox;
    public boolean realityCheck = false, pcCheck = false, playstationCheck = false, xboxCheck = false, isExist = false;
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
            ArrayList<Game.ePlatform> platforms = new ArrayList<>();
            if(realityCheck)
            {
                platforms.add(Game.ePlatform.REALITY);
            }
            else
            {
                if(pcCheck)
                {
                    platforms.add(Game.ePlatform.PC);
                }
                if(playstationCheck)
                {
                    platforms.add(Game.ePlatform.PLAYSTATION);
                }
                if(xboxCheck)
                {
                    platforms.add(Game.ePlatform.XBOX);
                }
            }
            Game newGame = new Game(addGameName.getText().toString(), gameImageURL.getText().toString(), platforms);
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i =0;
                for (DataSnapshot game :dataSnapshot.getChildren()) {
                    if(game.getValue(Game.class).getGameName().equals(deleteGameName.getText().toString()))
                    {
                        isExist = true;
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        if(isExist) {
            myRef = database.getReference("games").child(deleteGameName.getText().toString());
            myRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getBaseContext(), deleteGameName.getText().toString() + " Removed Successfully!",
                            Toast.LENGTH_LONG).show();
                    deleteGameName.setText("");
                }
            });
        }
        else
        {
            Toast.makeText(getBaseContext(), deleteGameName.getText().toString() + " Do Not Exist!",
                    Toast.LENGTH_LONG).show();
        }
    }
}