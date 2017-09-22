package com.example.hp.heartful;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by HP INDIA on 08-Apr-17.
 */
public class FragmentThree extends Fragment implements View.OnClickListener{

    private boolean shouldRefreshOnResume = false;
    private boolean justRefreshed = false;
    private EditText email_Id;
    private EditText password;
    private SignInButton mgoogleSign;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private EditText User_Name;
    private Button Sign_Up;
    private FirebaseUser firebaseUser;
    private DatabaseReference forUsers;
    private TextView login_Text;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog progress;
    private ProgressDialog fbProgress;
    private static String TAG="FragmentThree";
    private LoginButton mFbLogin;
    private ImageView edit;
    private String profilePicLink;
    private String profileName;
    private TextView userName;
    private CircleImageView profilePic;
    private DatabaseReference userInfo;
    private CallbackManager callbackManager;
    View view;
    View view_pro;
    private ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sign_up_page,container, false);
        view_pro = inflater.inflate(R.layout.profile_layout,container, false);
        fbProgress= new ProgressDialog(getActivity());
        userInfo=FirebaseDatabase.getInstance().getReference().child("Users");
        profilePic=(CircleImageView) view_pro.findViewById(R.id.profile_pic);
        userName=(TextView)view_pro.findViewById(R.id.user_name);
        edit=(ImageView)view_pro.findViewById(R.id.edit);
        fbProgress.setMessage("Connecting to Facebook Account, Please wait...");
        mAuth=FirebaseAuth.getInstance();
        forUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getActivity());
        callbackManager= CallbackManager.Factory.create();
        progressDialog=new ProgressDialog(getActivity());
        progress=new ProgressDialog(getActivity());
        email_Id=(EditText)view.findViewById(R.id.email_id);
        progress.setMessage("Connecting to Google Account, please wait...");
        password=(EditText)view.findViewById(R.id.password);
        User_Name=(EditText)view.findViewById(R.id.User_name);
        Sign_Up=(Button)view.findViewById(R.id.sign_up);
        login_Text=(TextView)view.findViewById(R.id.login_text);
        Sign_Up.setOnClickListener(this);
        edit.setOnClickListener(this);
        mgoogleSign=(SignInButton)view.findViewById(R.id.google_login);
        login_Text.setOnClickListener(this);
        mFbLogin=(LoginButton)view.findViewById(R.id.fb_login);

        if(mAuth.getCurrentUser()!=null) {
            mAuth=FirebaseAuth.getInstance();
            firebaseUser=mAuth.getCurrentUser();
            forUsers.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    profilePicLink = user.getProfilePicLink();
                    profileName = user.getUserName();
                    Glide
                            .with(getActivity())
                            .load(profilePicLink)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profilePic);
                    userName.setText(profileName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                justRefreshed = false;
            }

        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                        .enableAutoManage(getActivity(),new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Toast.makeText(getActivity(),"You get an error", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mgoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                signIn();
            }
        });


        mFbLogin.setFragment(FragmentThree.this);
        mFbLogin.setReadPermissions("public_profile", "email", "user_friends");
        try {
            mFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.v(" FragmentFacebook", "Dekhte hai chal rha hai ki nhi");
                    fbProgress.show();
                    handleFacebookAccessToken(loginResult.getAccessToken());

                    // App code
                    //userProfile();

                }

                @Override
                public void onCancel() {
                    Log.v("FragmentThree 2", "Dekhte hai chal rha hai ki nhi");
                    // App code
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.v("FragmentThree 3", "Dekhte hai chal rha hai ki nhi");
                    Log.v("Exception", String.valueOf(exception));
                    // App code
                }
            });
        }
        catch (Exception e){
            Log.v("FACEBOOK", "Error in the loginButton facebook");
            e.printStackTrace();
        }
        //                }

        if(mAuth.getCurrentUser()==null) {
            Log.v("Sign up run hoga", String.valueOf(mAuth.getCurrentUser()));
            Log.v("main hu"," sign up view");
            return view;
        }
        else {
            Log.v("profile run hoga",String.valueOf(mAuth.getCurrentUser()));
            Log.v("main hu"," profile view");
            return view_pro;
        }
    }



    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mGoogleApiClient.stopAutoManage(getActivity());
//        mGoogleApiClient.disconnect();
//    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //progress.dismiss();
            }

        }

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null) {
            if (visible) {

                FragmentManager fm = getFragmentManager();
                login_dialogbox dialogFragment = new login_dialogbox();
                dialogFragment.show(fm, "LoginPopup");

            }

            super.setMenuVisibility(visible);
        }
    }


    @Override
    public void onClick(View view) {
        if(view==Sign_Up){
            // user will register here

            registerUser();
        }
        if(view==login_Text){
            //    login user
            login();

        }
        if(mAuth.getCurrentUser()!=null) {
            if (view == edit) {
                startActivity(new Intent(getActivity(), Preferences.class));
            }
        }
    }
    //
    private void login(){

        try{
            startActivity(new Intent(getActivity(), loginActivity.class));
        }catch (Exception e) {
            Log.e(TAG,Log.getStackTraceString(e));
        }

    }

    private  void registerUser(){
        String email=email_Id.getText().toString().trim();
        String pass_word=password.getText().toString().trim();
        String user_name=User_Name.getText().toString();
        if(TextUtils.isEmpty(user_name)){
            // email is empty
            Toast.makeText(getActivity(),"please select a name",Toast.LENGTH_SHORT).show();
            return;// to stop the function from executation.
        }


        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(getActivity(),"please enter email",Toast.LENGTH_SHORT).show();
            return;
            // to stop the function from executation.
        }
        if(TextUtils.isEmpty(pass_word)){
            // email is empty
            Toast.makeText(getActivity(),"please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        // here if everything ok the user will be register
        progressDialog.setMessage("Registering User,please wait...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,pass_word)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //show user profile
                            Toast.makeText(getActivity(),"Registerd successfully",Toast.LENGTH_SHORT).show();
                            justRefreshed = false;
                            reLoad();
                            progressDialog.dismiss();
                            //  startActivity(new Intent(getActivity(),userProfileActivity.class));
//                            intent = new Intent(getApplicationContext(),userProfileActivity.class);
//                            intent.putExtra("EdiTtEXTvALUE", User_Name.getText().toString());
//                            startActivity(intent);
                        }else {
                            Toast.makeText(getActivity(),"could not register, pls try again Error is"+ task.getException(),Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });


    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.v("Facebook","Calling");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   userProfile();
                            firebaseUser=mAuth.getCurrentUser();
                            String personName = firebaseUser.getDisplayName();
                            Uri personPhoto = firebaseUser.getPhotoUrl();
                            DatabaseReference userData = forUsers.child(firebaseUser.getUid());
//                            userData.child("userName").setValue(personName);
//                            userData.child("profilePicLink").setValue(personPhoto.toString());
                            Users user =new Users(personName,personPhoto.toString());
                            userData.setValue(user);
                            Log.i(TAG, "Display Name: " + personName);
                            Log.i(TAG, "given  Name: " + personName);
                            Log.i(TAG, "uri: " + personPhoto);
                            Log.v(TAG, "signInWithCredential:success");
                            Log.v(TAG, "signInWithCredential:success");
                            justRefreshed = false;
                            fbProgress.dismiss();
                            reLoad();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.v("Facebookcheking","Dekhte hai ");
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            fbProgress.dismiss();

                        }

                        // ...
                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //      userProfile();
                            firebaseUser=mAuth.getCurrentUser();
                            String personName = firebaseUser.getDisplayName();
                            Uri personPhoto = firebaseUser.getPhotoUrl();
                            DatabaseReference userData = forUsers.child(firebaseUser.getUid());
//                            userData.child("userName").setValue(personName);
//                            userData.child("profilePicLink").setValue(personPhoto.toString());
                            Users user =new Users(personName,personPhoto.toString());
                            userData.setValue(user);
                            Log.i(TAG, "Display Name: " + personName);
                            Log.i(TAG, "given  Name: " + personName);
                            Log.i(TAG, "uri: " + personPhoto);
                            Log.v(TAG, "signInWithCredential:success");
                            justRefreshed = false;
                            reLoad();
                            progress.dismiss();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.v("googlecheking","Dekhte hai ");
                            Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            progress.dismiss();

                        }

                        // ...
                    }
                });
    }
    public void reLoad(){

        if(!justRefreshed) {
            FragmentThree fragment = (FragmentThree)
                    getFragmentManager().getFragments().get(2);
            getFragmentManager().beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
            shouldRefreshOnResume = false;
        }

        justRefreshed = true;
        Log.v("reload done", "value of should refresh"+String.valueOf(shouldRefreshOnResume));

    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
        Log.v("value of should refresh", String.valueOf(shouldRefreshOnResume));
        Log.v("reload", "onStop");


    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            // refresh fragment
            Log.v("value of should refresh", String.valueOf(shouldRefreshOnResume));
            Log.v("reload ing through", "onresume");
            reLoad();
        }
    }


}