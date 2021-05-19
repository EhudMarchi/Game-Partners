package com.example.gamepartners.ui.Activities_Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.Adapters.TabAccessorAdapter;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.example.gamepartners.data.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccessorAdapter tabAccessorAdapter;
    Animation requestsAnimation;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume(){
        super.onResume();
       // viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GamePartnerUtills.GetInstance();
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabAccessorAdapter = new TabAccessorAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(tabAccessorAdapter);
        FirebaseAuth mAuth =FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GamePartnerUtills.connectedUser = snapshot.getValue(User.class);
                if(GamePartnerUtills.connectedUser.getRequests()!= null) {
                    if (GamePartnerUtills.connectedUser.getRequests().size() > 0) {
                        requestsAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fragment_fade_enter);
                        requestsAnimation.setRepeatCount(Animation.INFINITE);
                        requestsAnimation.setDuration(2500);
                        tabLayout.getTabAt(3).view.setAnimation(requestsAnimation);
                        tabLayout.getTabAt(3).view.animate();
                        tabLayout.getTabAt(3).view.getTab().setText("● Profile");
                    }
                    else
                    {
                        tabLayout.getTabAt(3).view.clearAnimation();
                        tabLayout.getTabAt(3).view.getTab().setText("Profile");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}