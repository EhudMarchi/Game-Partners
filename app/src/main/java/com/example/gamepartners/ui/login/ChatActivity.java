package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.Message;
import com.example.gamepartners.data.model.Chat.MessageAdapter;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.GroupsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView messagesRecyclerView;
    ArrayList<Message> messagesArrayList = new ArrayList<>();
    MessageAdapter messagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messagesRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(layoutManager);
        FloatingActionButton fab =findViewById(R.id.fab);
        messagesAdapter = new MessageAdapter(this, messagesArrayList);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }
}