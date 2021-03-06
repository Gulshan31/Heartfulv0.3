package com.kinitoapps.ngolink;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by anshul on 17/9/17.
 */



public class Preferences extends AppCompatActivity implements View.OnClickListener{
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    View rootLayout;

    private int revealX;
    private int revealY;
    private LinearLayout logout,changeName,changePic,changeDes;
    private final static int GALLERY_REQUEST=1;
    private Uri imageUri=null;
    String m_Text;
    private ProgressDialog progress;
    private StorageReference mstorage;
    private FirebaseUser firebaseUser;
    private DatabaseReference forUsers;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.kinitoapps.ngolink.R.layout.preferences);
        final Intent intent = getIntent();
        rootLayout = findViewById(com.kinitoapps.ngolink.R.id.root_layout);
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }
        Toolbar toolbar = (Toolbar) findViewById(com.kinitoapps.ngolink.R.id.toolbar);
        setSupportActionBar(toolbar);
        progress=new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        forUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser=mAuth.getCurrentUser();
        progress.setMessage("Updating Profile Picture, Please Wait...");
        mstorage= FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Preferences");
        changeName=(LinearLayout)findViewById(com.kinitoapps.ngolink.R.id.change_name);
        changePic=(LinearLayout)findViewById(com.kinitoapps.ngolink.R.id.change_pic);
        changeDes=(LinearLayout)findViewById(com.kinitoapps.ngolink.R.id.change_desc);
        logout = (LinearLayout) findViewById(com.kinitoapps.ngolink.R.id.signout);
        logout.setOnClickListener(this);
        changePic.setOnClickListener(this);
        changeName.setOnClickListener(this);
        changeDes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            Toast.makeText(Preferences.this, "user has been sign out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Preferences.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        if (v == changeName) {
            startActivity(new Intent(Preferences.this, UpdateName.class));
        }
        if (v == changePic){
            selectImage();
        }
        if(v== changeDes){
            startActivity(new Intent(Preferences.this, UpdateDesc.class));
        }
    }
    private void selectImage() {
        Intent gallery=new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,GALLERY_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri=resultUri;
                progress.show();
                StorageReference filePath=mstorage.child("UsersProfilePics").child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") final
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final DatabaseReference imagePost= forUsers.child(firebaseUser.getUid()).child("userInfo");
                        forUsers.child(firebaseUser.getUid()).child("userInfo").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                imagePost.child("profilePicLink").setValue(downloadUrl.toString());
                                progress.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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

    protected void revealActivity(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }
    public void forInputs(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Description");

        View viewInflated = LayoutInflater.from(Preferences.this).inflate(com.kinitoapps.ngolink.R.layout.ngo_inputs, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(com.kinitoapps.ngolink.R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                Log.v("text",m_Text);
                if(TextUtils.isEmpty(m_Text)){
                    // email is empty
                    Toast.makeText(Preferences.this,"please Write Something",Toast.LENGTH_SHORT).show();
                    return;// to stop the function from executation.
                }else {
                    final DatabaseReference desc = forUsers.child(firebaseUser.getUid()).child("userInfo");
                    desc.child("userDesc").setValue(m_Text);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}