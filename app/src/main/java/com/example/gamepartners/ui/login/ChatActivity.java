package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.MessageAdapter;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.GroupsAdapter;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.data.model.UserAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.gamepartners.data.model.Chat.Message;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

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
    private ArrayList<User> users;
    ImageButton send, backButton, addParticipants, viewParticipants;
    ArrayList<Message> m_ChatMessages;
    ArrayList<User> selectedFriends = new ArrayList();
    ArrayList<User> userFriends = new ArrayList();
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
        fillFriends();
        groupName= getIntent().getExtras().getString("GroupName");
        groupNameView.setText(groupName);
        backButton = findViewById(R.id.back_chat);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        addParticipants = findViewById(R.id.add);
        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParticipants();
            }
        });
        viewParticipants = findViewById(R.id.participants);
        viewParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewParticipants();
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

    private void viewParticipants() {

    }

    private void addParticipants() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_choose_friends);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button addSelected = dialog.findViewById(R.id.addSelected);
        addSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Add Selected", Snackbar.LENGTH_LONG).show();
            }
        });
        SearchView searchView = (SearchView)dialog.findViewById(R.id.search);
        setUpFriendsRecyclerView(dialog, searchView);
        dialog.show();
    }
    private void setSearchFilter(SearchView searchView, final UserAdapter recyclerViewAdapter) {
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    private void setUpFriendsRecyclerView(Dialog dialog,SearchView searchView) {
        RecyclerView usersRecyclerView = dialog.findViewById(R.id.friendsSelectionRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        UserAdapter recyclerViewAdapter = new UserAdapter(this, users);
        usersRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        usersRecyclerView.setAdapter(recyclerViewAdapter);
        setSearchFilter(searchView, recyclerViewAdapter);
    }
    private void fillFriends() {
        users = new ArrayList<>();
        users.add(new User("Leo", "Messi", "leomessi@gmail.com", "123456"));
        users.add(new User("Adam", "Cohen", "adam515@gmail.com", "1234511016"));
        users.add(new User("David", "Williams", "diwi11@gmail.com", "0025edd6"));
        users.add(new User("Shalom", "Israel", "shaloooom@gmail.com", "1@#$23456"));
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
    public class AddParticipantsDialogFragment extends DialogFragment {
        @SuppressLint("ResourceType")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            selectedFriends = new ArrayList();  // Where we track the selected items
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle("Add Participants")
                    .setMultiChoiceItems(R.layout.friend_item, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        selectedFriends.add(userFriends.get(which));
                                    } else if (selectedFriends.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        selectedFriends.remove(Integer.valueOf(which));
                                    }
                                }
                            });

            return builder.create();
        }
    }
}