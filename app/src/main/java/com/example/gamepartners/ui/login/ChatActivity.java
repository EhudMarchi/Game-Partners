package com.example.gamepartners.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.data.model.Chat.MessageAdapter;
import com.example.gamepartners.data.model.FirebaseUtills;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.GroupsAdapter;
import com.example.gamepartners.data.model.User;
import com.example.gamepartners.data.model.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.gamepartners.data.model.Chat.Message;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.text.DateFormat;
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
        User sender = new User();
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
        if(FirebaseUtills.connedtedUser.getUid().equals(adminUID)) {
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
                final Message message = new Message(user.getUid(), user.getDisplayName(), inputMessage.getText().toString());
                if (!message.getText().equals("")) {
                    m_ChatMessages.add(message);

                    reference = FirebaseDatabase.getInstance().getReference("groups").child(groupNameView.getText().toString()).child("chat");
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("senderUID", FirebaseUtills.connedtedUser.getUid());
                    hashMap.put("senderDisplayName", FirebaseUtills.connedtedUser.getFirstName()+" "+FirebaseUtills.connedtedUser.getLastName());
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