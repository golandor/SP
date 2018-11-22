package com.example.golan.spproject.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.golan.spproject.Classes.Event_Save;
import com.example.golan.spproject.Classes.UniversalImageLoader;
import com.example.golan.spproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Events_Display_Fragment extends Fragment {

    private RecyclerView resultListForAllEvents;
    private DatabaseReference eventRef, userRef;
    private String user_id;

    public Events_Display_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventRef = FirebaseDatabase.getInstance().getReference(getString(R.string.events_in_events_display_fragment));

        resultListForAllEvents = view.findViewById(R.id.result_All_Events);
        userRef = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initializepostRef();
        displayAllEvents();

    }


    private void displayAllEvents() {

        final FirebaseRecyclerAdapter<Event_Save, UsersViewHolderEvents> firebaseRecyclerAdapterForAllEvents = new FirebaseRecyclerAdapter<Event_Save, UsersViewHolderEvents>
                (
                        Event_Save.class, R.layout.all_events_results, UsersViewHolderEvents.class, eventRef
                ) {
            @Override
            protected void populateViewHolder(UsersViewHolderEvents viewHolder, Event_Save model, int position) {
                final String userKey = getRef(position).getKey();

                viewHolder.setFull_name(model.getFull_name());
                viewHolder.set_Time(model.getTime());
                viewHolder.set_Date(model.getDate());
                viewHolder.set_Description(model.getEvent_description());
                viewHolder.setProfile_image(model.getProfile_image());


                viewHolder.registerToEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = new display_Event_Users_Fragment();
                        Bundle bundle=new Bundle();
                        bundle.putString(getString(R.string.event_id), userKey) ;
                        fragment.setArguments(bundle);

                        if(fragment!=null){
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.screen_area,fragment);
                            ft.commit();
                        }
                    }
                });
            }
        };
        resultListForAllEvents.setAdapter(firebaseRecyclerAdapterForAllEvents);
    }

    public void initializepostRef() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        resultListForAllEvents.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        resultListForAllEvents.setLayoutManager(linearLayoutManager);
    }

    public static class UsersViewHolderEvents extends RecyclerView.ViewHolder {
        View mView;
        TextView registerToEvent;

        public UsersViewHolderEvents(View itemView) {
            super(itemView);
            mView = itemView;
            registerToEvent = (TextView) mView.findViewById(R.id.registerToSpecificEventBtn);
        }


        public void setFull_name(String fullName) {
            TextView full_name = (TextView) mView.findViewById(R.id.fullNameOnEvent);
            full_name.setText(fullName);

            registerToEvent.setText("Register");
        }
        public void set_Time(String Time) {
            TextView time = (TextView) mView.findViewById(R.id.time_on_event);
            time.setText("  " + Time);
        }
        public void set_Date(String Date) {
            TextView date = (TextView) mView.findViewById(R.id.date_on_event);
            date.setText("  " + Date);
        }
        public void set_Description(String Description) {
            System.out.println("Des ->> " + Description);
            TextView description = (TextView) mView.findViewById(R.id.event_description);
            //description.setText(Description); TODO IMPORTENTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
        }

        public void setProfile_image(String profileImage){
            ImageView Event_profile_photo = (ImageView) mView.findViewById(R.id.profileImageEvent);
            UniversalImageLoader.setImage(profileImage, Event_profile_photo, null, "");
        }
    }
}
