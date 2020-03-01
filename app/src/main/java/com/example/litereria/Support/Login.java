package com.example.litereria.Support;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.litereria.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.litereria.R;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    int RC_SIGN_IN=1;
    ConstraintLayout constraintLayout;
    ImageView imageView,image;
    private ProgressDialog mProgress;
    private CallbackManager mCallbackManager;
    Button signInButton;
    Button facebook_sign_in;
    FirebaseAuth.AuthStateListener mAuthstateListner;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            sendToMainActicity();
        }
    }
    private void sendToMainActicity() {
        mProgress.dismiss();
        constraintLayout=findViewById(R.id.login);
        constraintLayout.setVisibility(View.GONE);

        startActivity(new Intent(Login.this, ProfilePhoto.class));




    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        image=findViewById(R.id.imageView);


        imageView=findViewById(R.id.img1);
        imageView.setAlpha(0f);
        image.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        image.animate().rotationBy(360f).setDuration(500).setInterpolator(new LinearInterpolator()).start();

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(image, "scaleX", 0.7f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(image, "scaleY", 0.7f);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(image, "translationY", -100);
        moveUpY.setDuration(500);

        AnimatorSet scaleDown = new AnimatorSet();
        AnimatorSet moveUp = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        moveUp.play(moveUpY);

        scaleDown.start();
        moveUp.start();


        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Logging in...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(true);
      //  mProgress.setInverseBackgroundForced(setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY));

        mProgress.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;


        mProgress.setIndeterminate(true);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        signInButton=findViewById(R.id.signin);
        facebook_sign_in=findViewById(R.id.login_button);
   /*     facebook_sign_in.setVisibility(View.VISIBLE);
        ObjectAnimator scaleDownX1 = ObjectAnimator.ofFloat(facebook_sign_in, "scaleX", 0.7f);
        ObjectAnimator scaleDownY1 = ObjectAnimator.ofFloat(facebook_sign_in, "scaleY", 0.7f);
        scaleDownX1.setDuration(1500);
        scaleDownY1.setDuration(1500);

        ObjectAnimator moveUpY1 = ObjectAnimator.ofFloat(facebook_sign_in, "translationY", -100);
        moveUpY1.setDuration(1500);

        AnimatorSet scaleDown1 = new AnimatorSet();
        AnimatorSet moveUp1 = new AnimatorSet();

        scaleDown1.play(scaleDownX1).with(scaleDownY1);
        moveUp1.play(moveUpY1);

        scaleDown1.start();
        moveUp1.start();*/

        mCallbackManager = CallbackManager.Factory.create();
        facebook_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.show();
                LoginManager.getInstance().logInWithReadPermissions(Login.this,
                        Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("VIVZ", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("VIVZ", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("VIVZ", "facebook:onError", error);
                    }
                });
            }
        });






// ...
// Initialize Firebase Auth


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                signIn();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("235609262275-q9ck8djinagdlisntsjd2usepcgdvkn0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        Log.e("ak47","on Starting");
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("ak47", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ak27", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendToMainActicity();
                            //updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ak47", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed."+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode,resultCode,data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ak47", "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ak47", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ak47", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendToMainActicity();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ak47", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this,"Something wrong",Toast.LENGTH_LONG).show();
                           // updateUI(null);
                        }


                    }
                });
    }

}
