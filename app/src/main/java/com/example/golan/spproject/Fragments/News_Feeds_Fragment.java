package com.example.golan.spproject.Fragments;

import android.content.Context;
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
import com.example.golan.spproject.Classes.Posts;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.Classes.interface_fromPostFragment_to_newsFeed;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class News_Feeds_Fragment extends Fragment {


    public interface_fromPostFragment_to_newsFeed listener;
    private DatabaseReference postRef, followingRef;
    private RecyclerView postList;

    public News_Feeds_Fragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.posts));;
        postList = view.findViewById(R.id.users_post_list);
        followingRef = FirebaseDatabase.getInstance().getReference("following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        initializepostRef();
        displayAllPosts();
        initImageLoader();


    }

    private void displayAllPosts(){


        final FirebaseRecyclerAdapter<Posts, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>
                (
                        Posts.class, R.layout.posts_layout, PostViewHolder.class, postRef
                ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Posts model, int position) {
              final String postKey = getRef(position).getKey();

                viewHolder.setFull_name(model.getFull_name());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setPost_image(getContext(), model.getPost_image());
                viewHolder.setProfile_image( model.getProfile_image());

                viewHolder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(),ProfilePageActivity.class);
                        i.putExtra("user_id",postKey);
                        startActivity(i);
                    }
                });
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public void initializepostRef(){

        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView userName;

        public PostViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

            userName = (TextView) mView.findViewById(R.id.post_userName);

        }

        public void setFull_name(String full_name) {
            userName.setText(full_name);
        }

        public void setProfile_image(String profile_image){
            ImageView image = (ImageView) mView.findViewById(R.id.profileImagePost);
            UniversalImageLoader.setImage(profile_image,image,null,"");

        }
        public void setPost_image(Context ctx, String post_image) {
            ImageView Post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(post_image).into(Post_image);
        }

        public void setTime(String time) {
            TextView time_on_post = (TextView) mView.findViewById(R.id.time_on_post);
            time_on_post.setText("   " + time);
        }

        public void setDate(String date) {
            TextView date_on_post = (TextView) mView.findViewById(R.id.date_on_post);
            date_on_post.setText("   " + date);
        }

        public void setDescription(String description) {
            TextView Description = (TextView) mView.findViewById(R.id.description_on_post);
            Description.setText(description);
        }
    }
}

