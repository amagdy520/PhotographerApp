package com.stylist.stylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    //FIREBASE AUTHENTICATION FIELDS
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private String g = null;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //FIREBASE AUTHENTICATION INSTANCES
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //CHECKING USER PRESENCE
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if((user != null))
                {
                    if(user.getEmail().equals("admin@admin.com"))
                    {
                        Intent moveToHome1 = new Intent(LoginActivity.this, MainActivity.class);
                        moveToHome1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        moveToHome1.putExtra("1","Admin");
                        startActivity(moveToHome1);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    else{
                        Intent moveToHome2 = new Intent(LoginActivity.this, MainActivity.class);
                        moveToHome2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        moveToHome2.putExtra("1","User");
                        startActivity(moveToHome2);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(true);


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString().trim();
        final String password = _passwordText.getText().toString().trim();
        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                        {

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if( task.isSuccessful())
                                    {
                                        String m = mAuth.getCurrentUser().getEmail();
                                        if(m.equals("admin@admin.com"))
                                        {
                                            Intent moveToHome3 = new Intent(LoginActivity.this, MainActivity.class);
                                            moveToHome3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            moveToHome3.putExtra("1","Admin");
                                            startActivity(moveToHome3);
                                            progressDialog.dismiss();
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        }
                                        else{
                                            Intent moveToHome4 = new Intent(LoginActivity.this, MainActivity.class);
                                            moveToHome4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            moveToHome4.putExtra("1","User");
                                            startActivity(moveToHome4);
                                            progressDialog.dismiss();
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        }

                                    }else
                                    {
                                        Toast.makeText(LoginActivity.this, "Unable to login user", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }else
                        {
                            Toast.makeText(LoginActivity.this, "Please enter user email and password", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                }, 1500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}