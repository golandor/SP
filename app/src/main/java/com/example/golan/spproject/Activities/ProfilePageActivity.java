package com.example.golan.spproject.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golan.spproject.Classes.FirebaseMethods;
import com.example.golan.spproject.Classes.Following_Save;
import com.example.golan.spproject.Classes.Posts;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.Classes.User;
import com.example.golan.spproject.Classes.UserAccountSettings;
import com.example.golan.spproject.Classes.UserSettings;
import com.example.golan.spproject.Fragments.News_Feeds_Fragment;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;




public class ProfilePageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference mStorageReference;
    private DatabaseReference myRef, postRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private FirebaseMethods mfirebaseMethods;
    private String user_id;
    private String userIdFromIntent;
    private UserAccountSettings settings;
    private TextView textViewUserEmail;
    private TextView textViewUserName;
    private TextView textViewFollowing;
    private TextView textViewFollowers;
    private TextView textViewdescription;
    private ImageView profileImageView;
    private Uri imageUri;
    private UserSettings userSettings;
    private int followers;
    private int following;
    private Button editProfileImage;
    private Button followBtn;
    private Button unFollowBtn;
    private RecyclerView postList;



    private static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        getMyIntent();
        initFireBase();
        addListenerTomyRef();
        initializeAttributes();
        isFollowing();
        getFollowings();
        getFollowers();
        initialize(); // handle emailTextView and ActionBar
        initImageLoader();

        setEditProfileImageClickListenter();
        setFollowBtnClickListener();
        setUnFollowBtnClickListener();
        displayMyPosts();
    }

    private void getMyIntent() {
        Intent i = getIntent();
        userIdFromIntent = i.getStringExtra("user_id").split(" ")[0];
    }

    public void addListenerTomyRef() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userSettings = mfirebaseMethods.getUserAccountSettings(dataSnapshot,user_id);
                setProfileWidgets(userSettings);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setProfileWidgets(UserSettings userSettings){
        String fullName;
        User user = userSettings.getUser();
        settings = userSettings.getSettings();

        fullName = user.getFullName();

        UniversalImageLoader.setImage(settings.getProfile_photo(),profileImageView,null,"");

        textViewUserName.setText(fullName);
        textViewUserEmail.setText(user.getEmail());
        textViewdescription.setText(user.getDescription());
        textViewFollowing.setText(String.valueOf(settings.getFollowing()));
        textViewFollowers.setText(String.valueOf(settings.getFollowers()));

    }

    private void isFollowing(){
        unFollow();
        Query query = myRef.child(getString(R.string.following_in_db)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.user_id)).equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    follow();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void displayMyPosts(){
        Query query = postRef.orderByChild(getString(R.string.user_id)).equalTo(user_id);

        final FirebaseRecyclerAdapter<Posts, News_Feeds_Fragment.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, News_Feeds_Fragment.PostViewHolder>
                (
                        Posts.class, R.layout.posts_layout, News_Feeds_Fragment.PostViewHolder.class, query
                ) {

            @Override
            protected void populateViewHolder(News_Feeds_Fragment.PostViewHolder viewHolder, Posts model, int position) {

                viewHolder.setFull_name(model.getFull_name());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPost_image(ProfilePageActivity.this, model.getPost_image());
                viewHolder.setProfile_image( model.getProfile_image());
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);

}

    private void setFollowBtnClickListener() {
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Following_Save following_save = new Following_Save
                        (user_id,userSettings.getUser().getProfile_photo(),userSettings.getUser().getFullName(),userSettings.getUser().getEmail());
                myRef.child(getString(R.string.following_in_db)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user_id).setValue(following_save);

                myRef.child(getString(R.string.followers_in_db)).child(user_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.user_id)).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                follow();
            }
        });
    }

    private void setUnFollowBtnClickListener() {
        unFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child(getString(R.string.following_in_db)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(user_id).removeValue();

                myRef.child(getString(R.string.followers_in_db)).child(user_id)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
                unFollow();
            }
        });
    }

    private void setEditProfileImageClickListenter(){
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra("type","profile");
                startActivityForResult(galleryIntent,PICK_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK && requestCode==PICK_IMAGE && data != null){
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profileImageView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
            uploadImage();
        }
    }

    private void follow(){
        if(!user_id.equals(FirebaseAuth.getInstance().getUid())) {
            followBtn.setVisibility(View.GONE);
            unFollowBtn.setVisibility(View.VISIBLE);
        }
    }
    private void unFollow() {
        if(!user_id.equals(FirebaseAuth.getInstance().getUid())) {
            followBtn.setVisibility(View.VISIBLE);
            unFollowBtn.setVisibility(View.GONE);
        }
    }

    private void getFollowings(){
        following = 0;

        Query query = myRef.child(getString(R.string.following_in_db)).child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    following++;
                }
                textViewFollowing.setText(String.valueOf(following));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        followers  = 0;

        Query query = myRef.child(getString(R.string.followers_in_db)).child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    followers++;
                }
                textViewFollowers.setText(String.valueOf(followers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void editProfilePicture(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        startActivityForResult(Intent.createChooser(galleryIntent,"Profile Images"),PICK_IMAGE);

    }


    private void uploadImage() {
        if(imageUri != null){
            profileImageView.setDrawingCacheEnabled(true);
            profileImageView.buildDrawingCache();
            Bitmap bitmap = profileImageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            profileImageView.setDrawingCacheEnabled(false);
            byte[] data = baos.toByteArray();
            String path = "profile_picture/" + UUID.randomUUID() +".png";
            StorageReference ref = storage.getReference(path);

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(ProfilePageActivity.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUri = taskSnapshot.getDownloadUrl();
                    mfirebaseMethods.updateProfileImage(userSettings,imageUri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    public void initFireBase(){
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        user = firebaseAuth.getCurrentUser();
        if(userIdFromIntent!=null) {
            user_id = userIdFromIntent;
        }
        else{
            user_id = user.getUid();
        }
        postRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.posts));
        storage = FirebaseStorage.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference  = FirebaseStorage.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebaseMethods = new FirebaseMethods(this);

        postList = findViewById(R.id.profilePost);

        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfilePageActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
    }

    public void initializeAttributes(){
        followers = 0;
        following = 0;
        textViewUserName = (TextView) findViewById(R.id.fullName);
        textViewUserEmail = (TextView) findViewById(R.id.emailAdrress);
        profileImageView = (ImageView) findViewById(R.id.profileImage);
        textViewFollowing = (TextView) findViewById(R.id.followingNumber);
        textViewFollowers = (TextView) findViewById(R.id.followersNumber);
        textViewdescription = (TextView) findViewById(R.id.descriptionInProfile) ;
        editProfileImage = (Button) findViewById(R.id.addProfilePic);
        followBtn = (Button) findViewById(R.id.follow_id);
        unFollowBtn = (Button) findViewById(R.id.unfollow_id);
        followBtn.setVisibility(View.GONE);
        unFollowBtn.setVisibility(View.GONE);
        if(!user_id.equals(user.getUid())){
            editProfileImage.setVisibility(View.INVISIBLE);
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    public void initialize(){
        if (textViewUserEmail != null) {
            textViewUserEmail.setText(user.getEmail());
        }
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToNewsFeed(View view) {
        finish();
        startActivity(new Intent(this, NewsFeeds.class));
    }
}

