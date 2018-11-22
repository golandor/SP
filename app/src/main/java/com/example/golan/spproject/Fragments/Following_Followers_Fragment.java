package com.example.golan.spproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golan.spproject.Activities.ProfilePageActivity;
import com.example.golan.spproject.Classes.Following_Save;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Following_Followers_Fragment extends Fragment {


    private RecyclerView resultList;
    private DatabaseReference followingRef, followersRef, userDatabase;

    public Following_Followers_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following__followers_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        followingRef = FirebaseDatabase.getInstance().getReference(getString(R.string.following_in_db)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        resultList =  view.findViewById(R.id.followingFollowersResultList);
        userDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.users));

        initializepostRef();
        displayFollowing();

    }

    public void initializepostRef(){

        resultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        resultList.setLayoutManager(linearLayoutManager);
    }

    private void displayFollowing() {

       final FirebaseRecyclerAdapter<Following_Save, UsersViewHolderFollowing> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Following_Save, UsersViewHolderFollowing>
                (
                        Following_Save.class, R.layout.following_followers_result, UsersViewHolderFollowing.class, followingRef
                ) {
            @Override
            protected void populateViewHolder(UsersViewHolderFollowing viewHolder, Following_Save model, int position) {
                final String userKey = getRef(position).getKey();
                viewHolder.setDetailsOnFollowing(model.getFull_name(), model.getEmail(), model.getProfile_image());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(),ProfilePageActivity.class);
                        i.putExtra("user_id",userKey);
                        startActivity(i);
                    }
                });
            }
        };
        resultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolderFollowing extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolderFollowing(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetailsOnFollowing(String fullName, String email, String profilePhoto) {
            TextView full_name = (TextView) mView.findViewById(R.id.fullNameOnFollowing_Followers);
            TextView e_mail = (TextView) mView.findViewById(R.id.emailAdrressFollowing_Followers);
            ImageView Following_profile_photo = (ImageView) mView.findViewById(R.id.profileImageFollowing_Followers);

            full_name.setText(fullName);
            e_mail.setText(email);
            UniversalImageLoader.setImage(profilePhoto, Following_profile_photo, null, "");
        }
    }
}