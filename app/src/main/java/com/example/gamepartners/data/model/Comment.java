package com.example.gamepartners.data.model;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.Interfaces.ILikeable;
import com.example.gamepartners.data.model.Interfaces.IPostable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class Comment implements IPostable, ILikeable {
    private String text;
    private String senderUID;
    private String senderDisplayName;
    private int likes;

    public Comment() {
    }

    public Comment(User sender,String text) {
        this.text = text;
        this.senderUID = sender.getUid();
        this.senderDisplayName = sender.getFirstName()+" "+sender.getLastName();
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void Post(DatabaseReference reference) {
        reference.setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    }
                }
            });
    }

    @Override
    public void Edit() {

    }

    @Override
    public void Delete() {

    }

    @Override
    public void Like() {

    }

    @Override
    public void Dislike() {

    }
}
