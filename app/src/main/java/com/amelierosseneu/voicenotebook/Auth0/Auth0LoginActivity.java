package com.amelierosseneu.voicenotebook.Auth0;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amelierosseneu.voicenotebook.R;
import com.amelierosseneu.voicenotebook.TokenBroadcastReceiver;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by lbalmaceda on 5/10/17.
 */

public class Auth0LoginActivity extends Activity  {

    private String authPrefix = "auth0:";
    private String firebaseToken;
    private TextView token;
    private TextView idtoken;
    private Auth0 auth0;
    private FirebaseAuth mAuth;
    private String mCustomToken;
    private TokenBroadcastReceiver mTokenReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth0_login);

        token = (TextView) findViewById(R.id.token);
        idtoken = (TextView) findViewById(R.id.text_idtoken_status);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth0_login();
            }
        });
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        mTokenReceiver = new TokenBroadcastReceiver() {
            @Override
            public void onNewToken(String token) {
                Log.d("RONIS", "onNewToken:" + token);
                setCustomToken(token);
            }
        };
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTokenReceiver);
    }



    private void startSignIn(String firebaseToken) {
        Log.d("RONIS", "startSignIn");
        // Initiate sign in with custom token
        // [START sign_in_custom]
        mAuth.signInWithCustomToken(firebaseToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RONIS", "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RONIS", "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(Auth0LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_custom]
    }



    private void Auth0_login() {
        Log.d("RONIS", "Auth0_login");
        token.setText("Not logged in");
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(Auth0LoginActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull final Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Auth0LoginActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String ct = credentials.getAccessToken();
                                firebaseToken = authPrefix + credentials.getAccessToken();
                                token.setText("Logged in: " + ct);
                                idtoken.setText(credentials.getIdToken());
                                setCustomToken (firebaseToken);
                                //String ct = mAuth.createCustomToken(credentials.getAccessToken());
                                //setCustomToken(ct);
                                startSignIn(firebaseToken);
                            }
                        });
                    }
                });
    }
    private void setCustomToken(String token) {
        mCustomToken = token;

        String status;
        if (mCustomToken != null) {
            status = "Token:" + mCustomToken;
        } else {
            status = "Token: null";
        }
        ((TextView) findViewById(R.id.text_token_status)).setText(status);


    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "User ID: " + user.getUid());
        } else {
            ((TextView) findViewById(R.id.text_sign_in_status)).setText(
                    "Error: sign in failed.");
        }
    }
}
