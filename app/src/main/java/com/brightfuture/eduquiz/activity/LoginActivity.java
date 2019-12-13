package com.brightfuture.eduquiz.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private LoginButton facebookLogin;
    private SignInButton googleSignIn;
    public String token;

    public ImageView btnPlayGuest;
    public static FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    public TextView tvPrivacy;
    public ImageView btnGoogle, btnFacebook;
    public ProgressDialog mProgressDialog;

    public LoginActivity() {
        // Required empty public constructor
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.transparentStatusAndNavigation(LoginActivity.this);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        btnPlayGuest = findViewById(R.id.btnPlayGuest);
        facebookLogin = findViewById(R.id.loginFacebook);
        googleSignIn = findViewById(R.id.loginGoogle);
        btnGoogle = findViewById(R.id.imgGmail);
        btnFacebook = findViewById(R.id.imgFacebook);


        if (Session.isLogin(getApplicationContext())) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("type", "default");
            startActivity(intent);
            finish();

        }
        if (!Utils.isNetworkAvailable(LoginActivity.this)) {
            setSnackBar();
        }
        token = Session.getDeviceToken(getApplicationContext());
        if (token == null) {
            token = "token";
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(LoginActivity.this))
                    facebookLogin.performClick();
                else
                    setSnackBar();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(LoginActivity.this))
                    signIn();
                else
                    setSnackBar();
            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        facebookLogin.setReadPermissions("email", "public_profile");

        facebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
                hideProgressDialog();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });
        btnPlayGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    Utils.btnClick(view, LoginActivity.this);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("type", "default");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else
                    setSnackBar();
            }
        });

        tvPrivacy.setClickable(true);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        String message = getString(R.string.term_privacy);
        String s2 = getString(R.string.term);
        String s1 = getString(R.string.privacy);
        final Spannable wordtoSpan = new SpannableString(message);

        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "privacy");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#008fee"));
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "terms");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#008fee"));
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacy.setText(wordtoSpan);
    }

    public void setSnackBar() {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    snackbar.dismiss();
                } else {
                    snackbar.show();
                }
            }
        });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
     //   TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
       // textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void setSnackBarStatus() {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.account_deactivate), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Session.clearUserSession(getApplicationContext());
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                btnFacebook.setEnabled(true);
                btnGoogle.setEnabled(true);

            }
        });

        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        //TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
       // textView.setTextColor(Color.WHITE);
        snackbar.show();
        hideProgressDialog();
        btnFacebook.setEnabled(false);
        btnGoogle.setEnabled(false);

        // Session.setUserData(Session.STATUS,status,LoginActivity.this);
    }

    public void UserSignUpWithSocialMedia(final String name, final String email, final String profile, final String type) {

        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    System.out.println("---login res  " + response);
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        if (!jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                            Session.saveUserDetail(getApplicationContext(),
                                    jsonobj.getString(Constant.userId),
                                    jsonobj.getString(Constant.name),
                                    jsonobj.getString(Constant.email),
                                    jsonobj.getString(Constant.mobile),
                                    jsonobj.getString(Constant.ISPREMIUM),
                                    jsonobj.getString(Constant.PROFILE));

                            User user = new User(jsonobj.getString(Constant.name), jsonobj.getString(Constant.email), "0", false, jsonobj.getString(Constant.PROFILE), "0", token, jsonobj.getString(Constant.userId));
                            FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra("type", "default");
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();

                                }
                            });
                        } else
                            setSnackBarStatus();
                    } else {
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.userSignUp, "1");
                params.put(Constant.email, email);
                params.put(Constant.name, name);
                params.put(Constant.PROFILE, profile);
                params.put(Constant.fcmId, token);
                params.put(Constant.type, type);
                params.put(Constant.mobile, "");
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                params.put(Constant.ipAddress, ip);
                System.out.println("---params social  " + params.toString());
                return params;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(strReq);

    }


    @Override
    public void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        mGoogleApiClient.connect();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    public void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        mGoogleApiClient.disconnect();
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            mGoogleApiClient.connect();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            System.out.println("TASK ::-=-=  "+task.getException());
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                System.out.println("ERROR  ::-=-= "+e+" == "+e.getMessage()+" == "+e.getCause());
            }
        } else {
            showProgressDialog();
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserSignUpWithSocialMedia(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString(), "fb");
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            hideProgressDialog();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String personName = user.getDisplayName();

                            if (personName.contains(" ")) {
                                personName = personName.substring(0, personName.indexOf(" "));
                            }
                            String email = user.getEmail();
                            UserSignUpWithSocialMedia(personName, email, user.getPhotoUrl().toString(), "gmail");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}