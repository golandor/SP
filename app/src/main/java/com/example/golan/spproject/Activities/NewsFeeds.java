package com.example.golan.spproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.golan.spproject.Classes.FirebaseMethods;
import com.example.golan.spproject.Classes.User;
import com.example.golan.spproject.Classes.UserAccountSettings;
import com.example.golan.spproject.Classes.UserSettings;
import com.example.golan.spproject.Classes.interface_fromPostFragment_to_newsFeed;
import com.example.golan.spproject.Fragments.Events_Display_Fragment;
import com.example.golan.spproject.Fragments.Events_Fragment;
import com.example.golan.spproject.Fragments.Following_Followers_Fragment;
import com.example.golan.spproject.Fragments.News_Feeds_Fragment;
import com.example.golan.spproject.Fragments.Post_Fragment;
import com.example.golan.spproject.Fragments.Search_Fragment;
import com.example.golan.spproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewsFeeds extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , interface_fromPostFragment_to_newsFeed {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private TextView textViewUserName;
    private FirebaseMethods mfirebaseMethods;
    private UserSettings userSettings;
    private DatabaseReference myRef;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feeds);

        initFireBase();
        initialize(); //init toolbar,DrawerLayout,NavigationView
        addListenerTomyRef();
        displayAllPosts();

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

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        String fullName = user.getFullName();
        textViewUserName.setText(fullName);
        textViewUserEmail.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news_feeds, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        // Handle OptionsItem view item clicks.
        int id = item.getItemId();
        switch (id) {

            case R.id.search:
                fragment = new Search_Fragment();
               break;

            case R.id.addEvent:
                fragment = new Events_Fragment();
                break;

            case R.id.addPicturePost:
                fragment = new Post_Fragment();
                ((Post_Fragment)fragment).listener=this;

                break;

            case R.id.upScreen:
                fragment =new News_Feeds_Fragment();
                break;

            default:

        }
        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area,fragment);
            ft.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        // Handle navigation view item clicks.
        int id = item.getItemId();
        switch (id) {
            case R.id.myProfile:
                Intent i = new Intent(this,ProfilePageActivity.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
                break;

            case R.id.followers_following:
                    fragment = new Following_Followers_Fragment();
                break;

            case R.id.events:
                fragment = new Events_Display_Fragment();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:

        }
        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void backFromPostFragment() {
        displayAllPosts();
    }

    private void displayAllPosts()
    {
        Fragment fragment = new News_Feeds_Fragment();
        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.screen_area,fragment);
            ft.commit();
        }
    }


    public void initFireBase(){
        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        user_id = user.getUid();
        if (textViewUserEmail != null) {
            textViewUserEmail.setText(user.getEmail());
        }
        mfirebaseMethods = new FirebaseMethods(this);
    }

    public void initialize(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        textViewUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUserNameInHeader);
        textViewUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUserEmailInHeader);
    }
}
