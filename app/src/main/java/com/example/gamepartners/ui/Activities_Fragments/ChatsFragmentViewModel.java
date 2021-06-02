package com.example.gamepartners.ui.Activities_Fragments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gamepartners.data.model.Group;

import java.util.ArrayList;

public class ChatsFragmentViewModel extends ViewModel {
    private ArrayList<Group> chatsArrayList = new ArrayList<>();
    private MutableLiveData<ArrayList<Group>> chatsArrayListData = new MutableLiveData<>();

    public void setChats(ArrayList<Group> chats)
    {
        chatsArrayList = chats;
        this.chatsArrayListData.setValue(chatsArrayList);
    }
    public MutableLiveData<ArrayList<Group>> getChats() {
        return chatsArrayListData;
    }
}
