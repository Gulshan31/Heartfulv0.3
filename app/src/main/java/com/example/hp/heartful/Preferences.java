package com.example.hp.heartful;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by anshul on 17/9/17.
 */



public class Preferences extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout logout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Preferences");
        logout = (LinearLayout) findViewById(R.id.signout);
        logout.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        if(v==logout){
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            Toast.makeText(Preferences.this,"user has been sign out",Toast.LENGTH_LONG).show();
            finish();

        }

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