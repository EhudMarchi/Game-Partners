package com.example.gamepartners.data.model.Interfaces;

import com.google.firebase.database.DatabaseReference;

public interface IPostable {
    public void Post(DatabaseReference reference);
    public void Edit();
    public void Delete();
}
