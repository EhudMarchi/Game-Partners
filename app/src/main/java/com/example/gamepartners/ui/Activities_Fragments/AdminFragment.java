package com.example.gamepartners.ui.Activities_Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminFragment extends Fragment {

    private DatabaseReference myRef;
    public Chip reality, pc, playstation, xbox;
    Button add, delete;
    public boolean realityCheck = false, pcCheck = false, playstationCheck = false, xboxCheck = false, isExist = false;
    EditText addGameName, gameImageURL, deleteGameName;
    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addGameName = getActivity().findViewById(R.id.newGameName);
        gameImageURL = getActivity().findViewById(R.id.newGameImageURL);
        deleteGameName = getActivity().findViewById(R.id.deleteGameName);
        reality = getActivity().findViewById(R.id.reality);
        pc = getActivity().findViewById(R.id.pc);
        playstation = getActivity().findViewById(R.id.playstation);
        xbox = getActivity().findViewById(R.id.xbox);
        add = getActivity().findViewById(R.id.addGame);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGame();
            }
        });
        delete = getActivity().findViewById(R.id.deleteGame);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGame();
            }
        });
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

    public void addGame() {
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
                    Toast.makeText(getActivity().getBaseContext(), addGameName.getText().toString() + " Added Successfully!",
                            Toast.LENGTH_LONG).show();
                    addGameName.setText("");
                    gameImageURL.setText("");
                }
            });
        }
        else
        {
            Toast.makeText(getActivity().getBaseContext(), "Please Enter Game Name!",
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

    public void deleteGame() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                    Toast.makeText(getActivity().getBaseContext(), deleteGameName.getText().toString() + " Removed Successfully!",
                            Toast.LENGTH_LONG).show();
                    deleteGameName.setText("");
                }
            });
        }
        else
        {
            Toast.makeText(getActivity().getBaseContext(), deleteGameName.getText().toString() + " Do Not Exist!",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }
}