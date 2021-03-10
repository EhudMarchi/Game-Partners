package com.example.gamepartners.controller.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Comment;
import com.example.gamepartners.data.model.Request;
import com.example.gamepartners.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{
    private ArrayList<Request> m_Requests;
    Request m_SelectedRequest = new Request();
    Context m_Context;
    int m_SelectedItemIndex = 0;
    private String groupName;
    Dialog requestDialog;
    TextView requestsTextView;

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView requestText, senderName;
        public ImageView senderImage;
        public ImageView confirm, decline;

        public RequestViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            requestText = itemView.findViewById(R.id.request_text);
            senderName = itemView.findViewById(R.id.requesterName);
            senderImage = itemView.findViewById(R.id.requester_image);
            confirm = itemView.findViewById(R.id.confirm);
            decline = itemView.findViewById(R.id.decline);
        }
    }

    public RequestAdapter(Context context, ArrayList<Request> requestsList , Dialog req, TextView requestsTextView) {
        m_Requests = requestsList;
        this.m_Context = context;
        requestDialog = req;
        this.requestsTextView = requestsTextView;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gamesView = inflater.inflate(R.layout.request_item, parent, false);
        RequestViewHolder viewHolder = new RequestViewHolder(gamesView);

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final RequestViewHolder holder, final int position) {
        final Request currentRequest = m_Requests.get(position);
        holder.senderName.setText(currentRequest.getSenderDisplayName());
        holder.requestText.setText(currentRequest.getRequestText());
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.getValue(User.class).getUid().equals(currentRequest.getSenderID())) {
                        User publisher = user.getValue(User.class);
                        assert publisher != null;
                        if(m_Context!=null) {
                            try {
                                Glide.with(m_Context).load(publisher.getProflieImageURL()).into(holder.senderImage);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRef;
                    if (currentRequest.getType() == Request.eRequestType.JOIN_GROUP) {
                        User addingUser = GamePartnerUtills.GetUser(currentRequest.getSenderID());
                        GamePartnerUtills.AddUserToGroup(addingUser, GamePartnerUtills.GetGroupByID(currentRequest.getTargetID()));
                    }
                    else {
                        mRef = FirebaseDatabase.getInstance().getReference("users").child(currentRequest.getTargetID()).child("userFriends");
                        mRef.child(currentRequest.getSenderID()).setValue(currentRequest.getSenderDisplayName());
                        mRef = FirebaseDatabase.getInstance().getReference("users").child(currentRequest.getSenderID()).child("userFriends");
                        mRef.child(currentRequest.getTargetID()).setValue(GamePartnerUtills.connectedUser.getFirstName()+" "+GamePartnerUtills.connectedUser.getLastName());
                    }
                mRef = FirebaseDatabase.getInstance().getReference().child("users").child(GamePartnerUtills.connectedUser.getUid()).child("requests");
                m_Requests.remove(currentRequest);
                GamePartnerUtills.ChangeUserRequests(GamePartnerUtills.connectedUser.getUid(), m_Requests);
                mRef.setValue(m_Requests).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(m_Context, "Request confirmed", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        requestsTextView.setText(m_Requests.size() + " Requests");
                    }
                });
                updateRequestsAmount();
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(GamePartnerUtills.connectedUser.getUid()).child("requests");
                m_Requests.remove(currentRequest);
                GamePartnerUtills.ChangeUserRequests(GamePartnerUtills.connectedUser.getUid(), m_Requests);
                mRef.setValue(m_Requests).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(m_Context, "Request denied", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        requestsTextView.setText(m_Requests.size() + " Requests");
                    }
                });
                updateRequestsAmount();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_SelectedItemIndex = position;
                m_SelectedRequest = m_Requests.get(m_SelectedItemIndex);
            }
        });
    }

    private void updateRequestsAmount() {
        if (m_Requests.size() == 0) {
            requestDialog.dismiss();
            requestsTextView.setEnabled(false);
            requestsTextView.setAlpha(0.4f);
        } else {
            requestsTextView.setEnabled(true);
            requestsTextView.setAlpha(1f);
        }
    }

    public Request getSelectedComment() {
        return m_SelectedRequest;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return m_Requests.size();
    }

}
