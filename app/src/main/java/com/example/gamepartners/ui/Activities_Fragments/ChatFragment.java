package com.example.gamepartners.ui.Activities_Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.gamepartners.controller.Adapters.GroupsAdapter;
import com.example.gamepartners.controller.Adapters.MessageAdapter;
import com.example.gamepartners.controller.Adapters.UserAdapter;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Group;
import com.example.gamepartners.data.model.Message;
import com.example.gamepartners.data.model.Update;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatFragment extends Fragment {
    FirebaseUser user;
    DatabaseReference reference;
    RecyclerView messagesRecyclerView;
    MessageAdapter messagesAdapter;
    String  groupID, adminUID, groupImageURL;
    TextView groupNameView;
    EditText inputMessage;
    Group group;
    private ArrayList<User> groupFriends = new ArrayList<>();
    private ArrayList<User> users;
    ArrayList<User> selectedUsers;
    ImageButton send, backButton, addParticipants, viewParticipants;
    ArrayList<Message> m_ChatMessages;
    RecyclerView usersRecyclerView ,groupFriendsRecyclerView;
    LinearLayoutManager recyclerViewLayoutManager,groupFriendsRecyclerViewLayoutManager;
    UserAdapter recyclerViewAdapter;
    ImageView groupImage;
    public Uri imageUri;
    Context context;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef= mStorage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        groupNameView = getActivity().findViewById(R.id.chat_groupname);
        inputMessage = getActivity().findViewById(R.id.chat_text_input);
        groupImage = getActivity().findViewById(R.id.group_img);
        send = getActivity().findViewById(R.id.chat_btn_send);
        messagesRecyclerView = getActivity().findViewById(R.id.chat_recycler_view);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        m_ChatMessages= new ArrayList<>();
        selectedUsers = new ArrayList<>();
        context = getContext();
        messagesAdapter = new MessageAdapter(getContext(), m_ChatMessages);
        messagesRecyclerView.setAdapter(messagesAdapter);
        Intent intent= getActivity().getIntent();
        Bundle b = intent.getExtras();
        group = GroupsAdapter.getCurrentGroup();
        adminUID =  group.getAdminUID();
        groupID =  group.getGroupID();
        groupImageURL =  group.getGroupImageURL();
        Glide.with(this).load(groupImageURL).into(groupImage);
        fetchParticipants();
        fillFriends();
        groupNameView.setText(group.getGroupName());
        backButton = getActivity().findViewById(R.id.back_chat);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigation.findNavController(v).navigate(R.id.action_chatFragment_to_homeFragment);
                getActivity().onBackPressed();
            }
        });
        addParticipants = getActivity().findViewById(R.id.add);
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
        viewParticipants = getView().findViewById(R.id.participants);
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
                reference = FirebaseDatabase.getInstance().getReference("groups").child(groupID).child("chat");
                final Message message = new Message(user.getUid(), GamePartnerUtills.getUserDisplayName(user.getUid()), inputMessage.getText().toString(), Message.eMessageType.USER_MESSAGE);
                if (!message.getText().equals("")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(message.getTimeSent());
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);
                    int year = cal.get(Calendar.YEAR);
                    if(m_ChatMessages.size()>0)
                    {
                        cal.setTime(m_ChatMessages.get(m_ChatMessages.size()-1).getTimeSent());
                    }
                    int lastDay = cal.get(Calendar.DAY_OF_MONTH);
                    int lastMonth = cal.get(Calendar.MONTH);
                    int lastYear = cal.get(Calendar.YEAR);
                    if((((day > lastDay)&&(month == lastMonth ))||(month > lastMonth)||(year > lastYear))||(m_ChatMessages.size()==0))
                    {
                        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/20yy");
                        String dateString = format.format(new Date());
                        GamePartnerUtills.AddGroupMessage(GamePartnerUtills.connectedUser, group.getGroupID() , dateString, m_ChatMessages);

                    }
                    m_ChatMessages.add(message);
                    for (User user:groupFriends) {
                        if(user.getUid() != GamePartnerUtills.connectedUser.getUid()) {
                            Update messageUpdate = new Update(Update.eUpdateType.MESSAGE, GamePartnerUtills.connectedUser.getUid(), GamePartnerUtills.getUserDisplayName(GamePartnerUtills.connectedUser.getUid()), user.getUid(), group.getGroupName(),groupID);
                            GamePartnerUtills.sendMessage(messageUpdate, context);
                        }
                    }
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
                                choosePicture();
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                dialog.show();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri.toString()).into(groupImage);
            group.setGroupImageURL(imageUri.toString());
            uploadGroupPic();
        }
    }
    private void uploadGroupPic() {
        final ProgressDialog uploadProgress = new ProgressDialog(context);
        uploadProgress.setTitle("Uploading Image...");
        uploadProgress.show();

        final StorageReference profilePicRef = mStorageRef.child("images/" + groupID);
        profilePicRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            uploadProgress.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded!", Toast.LENGTH_LONG).show();
                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef = database.getReference("groups").child(groupID).child("groupImageURL");
                                    myRef.setValue(uri.toString());
                                }
                            });

                        } else {
                            uploadProgress.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                int progressPercent = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                uploadProgress.setMessage(progressPercent + "%");
            }
        });
    }
    private void viewParticipants() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.group_friends);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fetchParticipants();
        setUpGroupFriendsRecyclerView(dialog);
        dialog.show();
        ImageButton back = dialog.findViewById(R.id.back_to_chat);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void fetchParticipants() {
        groupFriends.clear();
        for (String uid:group.getGroupFriends()) {
            groupFriends.add(GamePartnerUtills.GetUser(uid));
        }
    }

    private void addParticipants() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_choose_friends);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fillFriends();
        Button addSelected = dialog.findViewById(R.id.addSelected);
        addSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Add Selected", Snackbar.LENGTH_LONG).show();
                selectedUsers = recyclerViewAdapter.getSelectedUsers();
                for (User selectedUser:selectedUsers) {
                    GamePartnerUtills.AddUserToGroup(selectedUser,group.getGroupID());
                    GamePartnerUtills.AddGroupMessage(selectedUser, group.getGroupID(), selectedUser.getFirstName()+" "+selectedUser.getLastName()+" joined",m_ChatMessages);
                }
                dialog.dismiss();
            }
        });
        SearchView searchView = (SearchView)dialog.findViewById(R.id.searchUsers);
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
        groupFriendsRecyclerViewLayoutManager = new LinearLayoutManager(context);
        recyclerViewAdapter = new UserAdapter(context, groupFriends, false);
        groupFriendsRecyclerView.setLayoutManager(groupFriendsRecyclerViewLayoutManager);
        groupFriendsRecyclerView.setAdapter(recyclerViewAdapter);
    }
    private void setUpFriendsRecyclerView(Dialog dialog,SearchView searchView) {
        usersRecyclerView = dialog.findViewById(R.id.friendsSelectionRecyclerView);
        usersRecyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(context);
        recyclerViewAdapter = new UserAdapter(context, users, true);
        usersRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        usersRecyclerView.setAdapter(recyclerViewAdapter);
        setSearchFilter(searchView, recyclerViewAdapter);
    }
    private void fillFriends() {
        users = new ArrayList<>();
        for (String uid:GamePartnerUtills.connectedUser.getUserFriends().keySet()) {
            users.add(GamePartnerUtills.GetUser(uid));
        }
    }

    private void fetchMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("groups").child(groupID).child("chat");
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