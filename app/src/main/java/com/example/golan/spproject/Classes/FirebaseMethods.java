package com.example.golan.spproject.Classes;

import android.content.Context;

import com.example.golan.spproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by golan on 03/03/2018.
 */

public class FirebaseMethods {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String user_id;


    public FirebaseMethods(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        this.mContext = mContext;

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
        }
    }


    //Add User To The Firebase
    public void addNewUser(String email, String fullName, String user_id,String description, String profile_photo,String cover_photo){
        User user = new User(fullName, email, user_id, description, profile_photo,cover_photo);
        myRef.child(mContext.getString(R.string.users)).child(user_id).setValue(user);
        UserAccountSettings userSettings = new UserAccountSettings(user_id,0,0,profile_photo,cover_photo);
        myRef.child(mContext.getString(R.string.user_account_settings)).child(user_id).setValue(userSettings);
    }

    public void updateProfileImage(UserSettings userSettings, String profile_photo){
        userSettings.getUser().setProfile_photo(profile_photo);
        userSettings.getSettings().setProfile_photo(profile_photo);
        myRef.child(mContext.getString(R.string.users)).child(user_id).child("profile_photo").setValue(profile_photo);
        myRef.child(mContext.getString(R.string.user_account_settings)).child(user_id).child("profile_photo").setValue(profile_photo);
    }



    public UserSettings getUserAccountSettings(DataSnapshot dataSnapshot,String userId) {
        if(userId!=null) {
            if (!userId.equals(user_id)) {
                user_id = userId;
            }
        }
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.user_account_settings))) {
                try {
                    settings.setFollowing(
                            ds.child(user_id).getValue(UserAccountSettings.class).getFollowing()
                    );
                    settings.setFollowers(
                            ds.child(user_id).getValue(UserAccountSettings.class).getFollowers()
                    );
                    settings.setProfile_photo(
                            ds.child(user_id).getValue(UserAccountSettings.class).getProfile_photo()
                    );
                    settings.setUser_id(
                            ds.child(user_id).getValue(UserAccountSettings.class).getUser_id()
                    );
                    settings.setCover_photo(
                            ds.child(user_id).getValue(UserAccountSettings.class).getCover_photo()
                    );

                } catch (NullPointerException e) {
                }
            }
            if (ds.getKey().equals(mContext.getString(R.string.users))) {
                try {
                    user.setUser_id(
                            ds.child(user_id).getValue(User.class).getUser_id()
                    );
                    user.setFullName(
                            ds.child(user_id).getValue(User.class).getFullName()
                    );
                    user.setEmail(
                            ds.child(user_id).getValue(User.class).getEmail()
                    );
                    user.setProfile_photo(
                            ds.child(user_id).getValue(User.class).getProfile_photo()
                    );
                    user.setCover_photo(
                            ds.child(user_id).getValue(User.class).getCover_photo()
                    );
                    user.setDescription(
                            ds.child(user_id).getValue(User.class).getDescription()
                    );
                } catch (NullPointerException e) {
                }
            }
        }
        return new UserSettings(user,settings);
    }
}
