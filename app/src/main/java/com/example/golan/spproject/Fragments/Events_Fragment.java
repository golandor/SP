package com.example.golan.spproject.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.golan.spproject.Activities.NewsFeeds;
import com.example.golan.spproject.Classes.FirebaseMethods;
import com.example.golan.spproject.Classes.UserSettings;
import com.example.golan.spproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Events_Fragment extends Fragment {

    private ProgressDialog loadingBar;
    private Button updateEventBtn;
    private EditText description;
    private String Description;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String saveCurrentTimeToId;
    private String eventRandomName;
    private String user_id;
    private DatabaseReference userRef, eventRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods mfirebaseMethods;
    private UserSettings userSettings;



    public Events_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.create_event, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeAttributes();
        initFireBase();
        setUpdatePictureBtnListener();

    }

    public void initializeAttributes() {
        updateEventBtn = (Button) getView().findViewById(R.id.update_event);
        description = (EditText) getView().findViewById(R.id.event_description);
        loadingBar = new ProgressDialog(getContext());
    }

    public void initFireBase() {
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        eventRef = FirebaseDatabase.getInstance().getReference().child("events");
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mfirebaseMethods = new FirebaseMethods(getContext());
    }


    private void setUpdatePictureBtnListener() {
        updateEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateOnEventInfo();
            }
        });
    }

    private void validateOnEventInfo() {
        Description = description.getText().toString();

        if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this.getContext(), "Please Write Description..", Toast.LENGTH_SHORT).show();
        }
        if ((!TextUtils.isEmpty(Description))) {
            loadingBar.setTitle("Add Event");
            loadingBar.setMessage("Uploading Event...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            uploadToFirebase();
        }
    }

    private void uploadToFirebase() {

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTimeToId = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTimeToId = currentTimeToId.format(calendarTime.getTime());
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        eventRandomName = saveCurrentDate + saveCurrentTimeToId;

        savingEventInfoToFirebase();
    }

    private void savingEventInfoToFirebase() {
        userRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String fullName = dataSnapshot.child("fullName").getValue().toString();

                    String userProfileImage = dataSnapshot.child("profile_photo").getValue().toString();
                    HashMap eventMap = new HashMap();
                    eventMap.put("user_id", user_id);
                    eventMap.put("date", saveCurrentDate);
                    eventMap.put("time", saveCurrentTime);
                    eventMap.put("description", Description);
                    eventMap.put("profile_image", userProfileImage);
                    eventMap.put("full_name", fullName);

                    eventRef.child(user_id + " " + eventRandomName).updateChildren(eventMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Intent backToNewsFeed = new Intent(getContext(), NewsFeeds.class);
                                        startActivity(backToNewsFeed);
                                        Toast.makeText(getContext(),"Event Uploaded Successfully",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else{
                                        Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
