package com.example.hp.heartful;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


/**
 * Created by HP INDIA on 08-Apr-17.
 */

public class FragmentTwo extends Fragment {
    public FragmentTwo(){}
  android.support.design.widget.FloatingActionButton button;
    ImageView imageView;
    boolean canPost;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference forUsers;
    private RecyclerView mNewsLists;
    private View view;
    private DatabaseReference mDatabase;
//    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        view =inflater.inflate(R.layout.tab_two, container, false);
        super.onCreate(savedInstanceState);
        imageView = (ImageView) view.findViewById(R.id.news_images);
        button=(android.support.design.widget.FloatingActionButton) view.findViewById(R.id.button);
        button.bringToFront();
        firebaseAuth=FirebaseAuth.getInstance();
        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    Log.v("can u post2", String.valueOf(canPost));
                     forUsers= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                    forUsers.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users user = dataSnapshot.getValue(Users.class);
                            canPost = user.isCanPost();
                            Log.v("can u post", String.valueOf(canPost));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }

        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canPost){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        presentActivity(v);
                        else
                startActivity(new Intent(getActivity(),NewsPost.class));

            }
            else {
                    Log.v("canPost", String.valueOf(canPost));
                    Toast.makeText(getActivity(),"If NGO ,login to post news", Toast.LENGTH_LONG).show();
                }
            }

        });

        mDatabase= FirebaseDatabase.getInstance().getReference().child("News");
        mDatabase.keepSynced(true);
        mNewsLists=(RecyclerView)view.findViewById(R.id.new_lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        //mNewsLists.setHasFixedSize(true);
        mNewsLists.setItemViewCacheSize(20);
        mNewsLists.setDrawingCacheEnabled(true);
        mNewsLists.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        mNewsLists.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerAdapter<News,NewsViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<News, NewsViewHolder>(News.class,
                R.layout.news_list_sample,
                NewsViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, News model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setDateAndTime(model.getDateAndTime());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity().getApplicationContext(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                Intent intent=new Intent(getActivity(),SingleNewsDetail.class);
                        intent.putExtra("news_id",post_key);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Transition transition_two = TransitionInflater.from(getActivity()).inflateTransition(R.transition.transition_two);
                            getActivity().getWindow().setSharedElementEnterTransition(transition_two);
                            ImageView transitionView = (ImageView) v.findViewById(R.id.news_images);
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(getActivity(), transitionView, getString(R.string.fragmenttwo_image_trans));
                            startActivity(intent, options.toBundle());
                        }
                        else
                        startActivity(intent);
                    }
                });
            }
        };
        mNewsLists.setAdapter(firebaseRecyclerAdapter);
        return view;
          }
    @Override
    public void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public NewsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }


        public void setTitle(String title) {
            TextView post_title=(TextView)mView.findViewById(R.id.news_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_title=(TextView)mView.findViewById(R.id.news_description);
            post_title.setText(desc);
        }
        public void setDateAndTime(String desc) {
            TextView post_time=(TextView)mView.findViewById(R.id.date_time);
            post_time.setText(desc);
        }
        public void setImage(Context applicationContext, String image) {
            final ImageView post_image=(ImageView)mView.findViewById(R.id.news_images);
            Glide
                    .with(applicationContext)
                    .load(image)
                    .override(1000,1000)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(post_image);

        }
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(getActivity(), NewsPost.class);
        intent.putExtra(NewsPost.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(NewsPost.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }


}



