package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.MessageAdapter;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.GroupsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.gamepartners.data.model.Chat.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;
    RecyclerView messagesRecyclerView;
    MessageAdapter messagesAdapter;
    String groupName;
    TextView groupNameView;
    EditText inputMessage;
    ImageButton send, backButton;;
    ArrayList<Message> m_ChatMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();
        groupNameView = findViewById(R.id.chat_groupname);
        inputMessage = findViewById(R.id.chat_text_input);
        send = findViewById(R.id.chat_btn_send);
        messagesRecyclerView = findViewById(R.id.chat_recycler_view);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_ChatMessages= new ArrayList<>();
        groupName= getIntent().getExtras().getString("GroupName");
        groupNameView.setText(groupName);
        backButton = findViewById(R.id.back_chat);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        fetchMessages();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add message
                final Message message = new Message(user.getUid(), user.getDisplayName(), inputMessage.getText().toString());
                if (!message.getText().equals("")) {
                    m_ChatMessages.add(message);

                    reference = FirebaseDatabase.getInstance().getReference("groups").child(groupNameView.getText().toString()).child("chat");
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("senderUID", user.getUid());
                    hashMap.put("senderDisplayName", user.getDisplayName());
                    hashMap.put("text", message.getText());
                    reference.push().setValue(hashMap);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    inputMessage.setText("");
                }
            }
        });

    }
    private void fetchMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                m_ChatMessages.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    //Message message = new Message(user.getUid(),user.getDisplayName(),"hello");
                    m_ChatMessages.add(message);
                }
                messagesAdapter = new MessageAdapter(m_ChatMessages);
                messagesRecyclerView.setAdapter(messagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}