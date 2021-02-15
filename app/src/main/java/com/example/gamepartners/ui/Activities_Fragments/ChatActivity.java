package com.example.gamepartners.ui.Activities_Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.Adapters.MessageAdapter;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.controller.Adapters.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.gamepartners.data.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;
    RecyclerView messagesRecyclerView;
    MessageAdapter messagesAdapter;
    String groupName;
    String adminUID;
    TextView groupNameView;
    EditText inputMessage;
    Group group;
    private ArrayList<User> groupFriends = new ArrayList<>();
    private ArrayList<User> users;
    ArrayList<User> selectedUsers;
    ImageButton send, backButton, addParticipants, viewParticipants;
    ArrayList<Message> m_ChatMessages;
    ArrayList<User> selectedFriends = new ArrayList();
    ArrayList<User> userFriends = new ArrayList();
    RecyclerView usersRecyclerView ,groupFriendsRecyclerView;
    LinearLayoutManager recyclerViewLayoutManager,groupFriendsRecyclerViewLayoutManager;
    UserAdapter recyclerViewAdapter;
    CircleImageView groupImage;
    public Uri imageUri;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();
        groupNameView = findViewById(R.id.chat_groupname);
        inputMessage = findViewById(R.id.chat_text_input);
        groupImage = findViewById(R.id.group_img);
        send = findViewById(R.id.chat_btn_send);
        messagesRecyclerView = findViewById(R.id.chat_recycler_view);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m_ChatMessages= new ArrayList<>();
        selectedUsers = new ArrayList<>();
        context = this;
        messagesAdapter = new MessageAdapter(getBaseContext(), m_ChatMessages);
        messagesRecyclerView.setAdapter(messagesAdapter);
        Intent intent= getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            groupName = (String) b.get("GroupName");
            adminUID = (String) b.get("AdminUID");
        }

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
        addParticipants.setAlpha(0.5f);
        addParticipants.setEnabled(false);
        if(GamePartnerUtills.connectedUser.getUid().equals(adminUID)) {
            addParticipants.setEnabled(true);
            addParticipants.setAlpha(1f);
            addParticipants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addParticipants();
                }
            });
        }
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
                reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("chat");
                final Message message = new Message(user.getUid(), user.getDisplayName(), inputMessage.getText().toString(), Message.eMessageType.USER_MESSAGE);
                if (!message.getText().equals("")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(message.getTimeSent());
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);
                    cal.setTime(m_ChatMessages.get(m_ChatMessages.size()-1).getTimeSent());
                    int lastDay = cal.get(Calendar.DAY_OF_MONTH);
                    int lastMonth = cal.get(Calendar.MONTH);
                    int lastYear = cal.get(Calendar.YEAR);
                    if(((day > lastDay)&&(month == lastMonth ))||(month > lastMonth)||(year > lastYear))
                    {
                        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/20yy");
                        String dateString = format.format(new Date());
                        addGroupMessage(dateString);
                    }
                    m_ChatMessages.add(message);
                    reference.push().setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                            }
                        }
                    });
                    inputMessage.setText("");
                }
            }
        });
        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Change Group Picture?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //choosePicture();
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                dialog.show();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
private void addGroupMessage(String i_Message)
{
    final Message message = new Message(user.getUid(), user.getDisplayName(), i_Message, Message.eMessageType.GROUP_MESSAGE);
    if (!message.getText().equals("")) {
        m_ChatMessages.add(message);
        reference = FirebaseDatabase.getInstance().getReference("groups").child(groupNameView.getText().toString()).child("chat");
        reference.push().setValue(message);
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
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1 && data!= null && data.getData()!= null)
//        {
//            imageUri=data.getData();
//            Glide.with(this).load(imageUri.toString()).into(imgViewProfilePic);
//            FirebaseUtills.connedtedUser.setProflieImageURL(imageUri.toString());
//            uploadProfilePic();
//        }
//
//    }
//
//    private void uploadGroupPic() {
//        final ProgressDialog uploadProgress= new ProgressDialog(this);
//        uploadProgress.setTitle("Uploading Image...");
//        uploadProgress.show();
//
//        //final String randomKey = UUID.randomUUID().toString();
//        StorageReference profilePicRef = mStorageRef.child("images/" + mAuth.getCurrentUser().getEmail());
//
//        profilePicRef.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        uploadProgress.dismiss();
//                        Snackbar.make(getView(), "Image Uploaded", Snackbar.LENGTH_LONG).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        uploadProgress.dismiss();
//                        Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                int progressPercent = (int)(100.0 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
//                uploadProgress.setMessage(progressPercent + "%");
//            }
//        });
//    }
    private void viewParticipants() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.group_friends);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        users = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("groupFriends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupFriends.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user !=null;
                    groupFriends.add(user);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setUpGroupFriendsRecyclerView(dialog);
        dialog.show();
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
                selectedUsers = recyclerViewAdapter.getSelectedUsers();
                for (User selectedUser:selectedUsers) {
                    //**NEED TO SET UID**
                    reference = FirebaseDatabase.getInstance().getReference("users").child(selectedUser.getUid()).child("userGroups").child(groupName);
                    HashMap userGroups = new HashMap<>();
                    userGroups.put("adminUID",adminUID);
                    userGroups.put("groupName",groupName);
                    userGroups.put("chat",groupName+" "+adminUID);
                    reference.setValue(userGroups);
                    reference = FirebaseDatabase.getInstance().getReference("groups").child(groupName).child("groupFriends");
                    HashMap<String, String> groupFriendsMap = new HashMap<>();
                    groupFriendsMap.put("uid",selectedUser.getUid());
                    groupFriendsMap.put("firstName",selectedUser.getFirstName());
                    groupFriendsMap.put("lastName",selectedUser.getLastName());
                    groupFriendsMap.put("proflieImageURL",selectedUser.getProflieImageURL());
                    reference.child(selectedUser.getUid()).setValue(groupFriendsMap);
                    addGroupMessage(selectedUser.getFirstName()+" "+selectedUser.getLastName()+" joined");
                }
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
    private void setUpGroupFriendsRecyclerView(Dialog dialog) {
        groupFriendsRecyclerView = dialog.findViewById(R.id.groupFriendsRecyclerView);
        groupFriendsRecyclerView.setHasFixedSize(true);
        groupFriendsRecyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new UserAdapter(this, groupFriends, false);
        groupFriendsRecyclerView.setLayoutManager(groupFriendsRecyclerViewLayoutManager);
        groupFriendsRecyclerView.setAdapter(recyclerViewAdapter);
    }
    private void setUpFriendsRecyclerView(Dialog dialog,SearchView searchView) {
        usersRecyclerView = dialog.findViewById(R.id.friendsSelectionRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new UserAdapter(this, users, true);
        usersRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        usersRecyclerView.setAdapter(recyclerViewAdapter);
        setSearchFilter(searchView, recyclerViewAdapter);
    }
    private void fillFriends() {
        users = new ArrayList<>();
        final FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user !=null;
                    if(!user.getUid().equals(mAuth.getCurrentUser().getUid())) {
                        //**HERE CHECK IF FRIENDS***
                        users.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    m_ChatMessages.add(message);
                }
                messagesAdapter.notifyDataSetChanged();
                messagesRecyclerView.smoothScrollToPosition(messagesRecyclerView.getBottom());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}