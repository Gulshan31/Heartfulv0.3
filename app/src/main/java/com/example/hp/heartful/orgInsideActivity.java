package com.example.hp.heartful;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by HP on 06-06-2017.
 */

public class orgInsideActivity extends AppCompatActivity{
    private String post_key;
    private ImageView mNgoLogo;
    private  String mContact,mDonate,mVolunteer;
    private TextView mNgoName,mNgoInfo;
    DatabaseReference mDatabase;
    private String loadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.org_inside_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post_key=getIntent().getExtras().getString("news_id");
        mNgoLogo=(ImageView)findViewById(R.id.backdrop);
        mNgoInfo=(TextView)findViewById(R.id.orgInfo);
        mNgoName=(TextView)findViewById(R.id.orgName);
//        initCollapsingToolbar();
            mDatabase= FirebaseDatabase.getInstance().getReference().child("NgoList");
        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title= (String) dataSnapshot.child("mOrgname").getValue();
                String post_desc=(String)dataSnapshot.child("mOrginfo").getValue();
                String post_image=(String)dataSnapshot.child("mImage").getValue();
                String contact=(String)dataSnapshot.child("mContact").getValue();
                String donate=(String)dataSnapshot.child("mDonate").getValue();
                String volunteer=(String)dataSnapshot.child("mVolunteer").getValue();
                mContact=contact;
                mDonate=donate;
                mVolunteer=volunteer;
                loadImage=post_image;
                mNgoName.setText(post_title);
                mNgoInfo.setText(post_desc);
                Glide
                        .with(orgInsideActivity.this)
                        .load(post_image)
                        .override(450,450)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mNgoLogo);

                Glide.with(getApplicationContext()).load(loadImage)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mNgoLogo);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        findViewById(R.id.contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mContact)) {

                    Log.v("url", mContact);

                    finish();
                    openWebView(mContact);
                }
                else
                    Toast.makeText(orgInsideActivity.this,"Sorry no webPage is available for this ngo",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(orgInsideActivity.this,WebViewContents.class));
            }
        });
        findViewById(R.id.volunteer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mVolunteer)) {

                    Log.v("url", mVolunteer);

                    finish();
                    openWebView(mVolunteer);
                }
                else
                    Toast.makeText(orgInsideActivity.this,"Sorry no webPage is available for this ngo",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(orgInsideActivity.this,WebViewContents.class));
            }
        });
        findViewById(R.id.donate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mDonate)) {

                    Log.v("url", mDonate);

                    finish();
                    openWebView(mDonate);
                }
                else
                    Toast.makeText(orgInsideActivity.this,"Sorry no webPage is available for this ngo",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(orgInsideActivity.this,WebViewContents.class));
            }
        });
    }


//    private void initCollapsingToolbar() {
//        final CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(" ");
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        appBarLayout.setExpanded(true);
//
//        // hiding & showing the txtPostTitle when toolbar expanded & collapsed
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle("Ngo Details");
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbar.setTitle(" ");
//                    isShow = false;
//                }
//            }
//        });
//
//    }
    private void openWebView(String url) {
        Intent intent = new Intent(orgInsideActivity.this, WebViewContents.class);
        intent.putExtra("url", url);
        finish();
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

