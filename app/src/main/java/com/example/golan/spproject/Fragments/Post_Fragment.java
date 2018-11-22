package com.example.golan.spproject.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.golan.spproject.Activities.NewsFeeds;
import com.example.golan.spproject.Classes.FirebaseMethods;
import com.example.golan.spproject.Classes.UserSettings;
import com.example.golan.spproject.Classes.interface_fromPostFragment_to_newsFeed;
import com.example.golan.spproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Post_Fragment extends Fragment {

    private ProgressDialog loadingBar;
    private ImageButton selectPostImage;
    private Button updatePictureBtn;
    private EditText description;
    private String Description;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String saveCurrentTimeToId;
    private String postRandomName;
    private String downloadUrl;
    private String user_id;
    private StorageReference postImageRef;
    private DatabaseReference userRef,postRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods mfirebaseMethods;
    private UserSettings userSettings;
    private static final int Gallery_Pick = 1;
    private Uri imaUri;
    public interface_fromPostFragment_to_newsFeed listener;
    private StorageReference imageRef;


    public Post_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_post_, container, false);
    }
    public void listenetTrigger() {
        this.listener.backFromPostFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeAttributes();
        initFireBase();
        setSelectPostImageListener();
        setUpdatePictureBtnListener();

    }


    private void setSelectPostImageListener(){
        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void setUpdatePictureBtnListener(){
        updatePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateOnPostInfo();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == Activity.RESULT_OK && data != null){
            imaUri = data.getData();
            selectPostImage.setImageURI(imaUri);

        }
    }
    private void validateOnPostInfo() {
        Description = description.getText().toString();
        if(imaUri == null){
            Toast.makeText(this.getContext(),"Please Select Image..",Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(Description)){
            Toast.makeText(this.getContext(),"Please Write Description..",Toast.LENGTH_SHORT).show();
        }
        if((imaUri != null && !TextUtils.isEmpty(Description))){
            loadingBar.setTitle("Add Post");
            loadingBar.setMessage("Uploading Post...");
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

        postRandomName = saveCurrentDate + saveCurrentTimeToId;

        StorageReference filePath = imageRef.child("Post Images").child(imaUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(imaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(getContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    savingPostInfoToFirebase();
                }
                else{
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savingPostInfoToFirebase() {
        userRef.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.exists()){

                        String fullName = dataSnapshot.child("fullName").getValue().toString();

                        String userProfileImage = dataSnapshot.child("profile_photo").getValue().toString();
                        HashMap postMap = new HashMap();
                        postMap.put("user_id",user_id);
                        postMap.put("date",saveCurrentDate);
                        postMap.put("time",saveCurrentTime);
                        postMap.put("description",Description);
                        postMap.put("post_image",downloadUrl);
                        postMap.put("profile_image",userProfileImage);
                        postMap.put("full_name",fullName);

                    postRef.child(user_id + " " + postRandomName).updateChildren(postMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        Intent backToNewsFeed = new Intent(getContext(), NewsFeeds.class);
                                        startActivity(backToNewsFeed);
                                        listenetTrigger();
                                        Toast.makeText(getContext(),"Post Uploaded Successfully",Toast.LENGTH_SHORT).show();
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

    public void initializeAttributes(){
        selectPostImage = (ImageButton) getView().findViewById(R.id.select_post_image);
        updatePictureBtn = (Button) getView().findViewById(R.id.update_picture_btn);
        description = (EditText) getView().findViewById(R.id.description);
        loadingBar = new ProgressDialog(getContext());
    }
    public void initFireBase(){
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        imageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mfirebaseMethods = new FirebaseMethods(getContext());
    }
}
