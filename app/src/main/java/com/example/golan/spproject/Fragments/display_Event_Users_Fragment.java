package com.example.golan.spproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.golan.spproject.Classes.Event_Save;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class display_Event_Users_Fragment extends Fragment {

    private RecyclerView resultList;
    private DatabaseReference eventRegisterRef, userRef;
    private String user_id;

    private String event_id;

    public display_Event_Users_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        event_id = getArguments().getString(getString(R.string.event_id));
        View view = inflater.inflate(R.layout.fragment_display__event__users_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        resultList = view.findViewById(R.id.result_list_events);
        initializepostRef();
        userRef = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventRegisterRef = FirebaseDatabase.getInstance().getReference(getString(R.string.events_in_events_display_fragment));
        saveRegisterDetails();

    }

    private void saveRegisterDetails() {
        userRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profile_photo").getValue().toString();
                    HashMap eventMap = new HashMap();
                    eventMap.put("user_id", user_id);
                    eventMap.put("profile_image", userProfileImage);
                    eventMap.put("full_name", fullName);
                    eventMap.put("email", email);

                    eventRegisterRef.child(event_id).child("registered").child(user_id).updateChildren(eventMap).addOnCompleteListener
                            (new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    displayEventUsers();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayEventUsers() {

        final FirebaseRecyclerAdapter<Event_Save, UsersViewHolderRegisteredEvents> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event_Save, UsersViewHolderRegisteredEvents>
                (
                        Event_Save.class, R.layout.event_result,UsersViewHolderRegisteredEvents.class,
                        eventRegisterRef.child(event_id).child(getString(R.string.registered))

                ) {
            @Override
            protected void populateViewHolder(UsersViewHolderRegisteredEvents viewHolder, Event_Save model, int position) {
                final String userKey = getRef(position).getKey();

                viewHolder.setFull_name(model.getFull_name());
                viewHolder.setProfile_image(model.getProfile_image());
                viewHolder.set_Email(model.getEmail());

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
    public void initializepostRef() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(linearLayoutManager);

    }

    public static class UsersViewHolderRegisteredEvents extends RecyclerView.ViewHolder {
        View mView;


        public UsersViewHolderRegisteredEvents(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFull_name(String fullName) {
            TextView full_name = (TextView) mView.findViewById(R.id.fullNameOnEventRegistered);
            full_name.setText(fullName);
        }

        public void set_Email(String Email) {
            TextView email = (TextView) mView.findViewById(R.id.emailAdrressOnEventRegistered);
            email.setText(Email);
        }
        public void setProfile_image(String profileImage){
            ImageView Event_profile_photo = (ImageView) mView.findViewById(R.id.profileImageEventRegistered);
            UniversalImageLoader.setImage(profileImage, Event_profile_photo, null, "");
        }
    }
}
