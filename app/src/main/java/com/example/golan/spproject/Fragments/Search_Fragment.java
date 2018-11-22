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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golan.spproject.Activities.ProfilePageActivity;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.Classes.User;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Search_Fragment extends Fragment {

    private EditText searchNames;
    private ImageButton searchBtn;
    private RecyclerView resultList;
    private DatabaseReference userDatabase;


    public Search_Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userDatabase = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        searchNames = (EditText) view.findViewById(R.id.search_Names);
        searchBtn = (ImageButton) view.findViewById(R.id.search_btn);
        resultList = (RecyclerView) view.findViewById(R.id.result_list);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchNames.getText().toString();

                UserSearch(searchText);
            }
        });
    }

    private void UserSearch(String searchText) {
        Query firebaseSearchQuery = userDatabase.orderByChild("fullName").startAt(searchText).endAt(searchText + "\uf8ff");


        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>
                (
                        User.class, R.layout.search_result, UsersViewHolder.class, firebaseSearchQuery
                ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, User model, int position) {
                final String postKey = getRef(position).getKey();
                viewHolder.setDetailsOnSearch(model.getFullName(), model.getEmail(), model.getProfile_photo());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(),ProfilePageActivity.class);
                        i.putExtra("user_id",postKey);
                        startActivity(i);
                    }
                });
            }
        };
            resultList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }
        public void setDetailsOnSearch(String fullName, String email, String searchImageView){
            TextView full_name = (TextView) mView.findViewById(R.id.fullNameOnSearch);
            TextView e_mail = (TextView) mView.findViewById(R.id.emailAdrressSearch);
            ImageView search_image_view = (ImageView) mView.findViewById(R.id.profileImageSearch);

            full_name.setText(fullName);
            e_mail.setText(email);
            UniversalImageLoader.setImage(searchImageView,search_image_view,null,"");

        }
    }
}
