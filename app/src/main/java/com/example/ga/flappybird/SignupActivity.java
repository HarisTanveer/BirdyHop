package com.example.ga.flappybird;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.ga.flappybird.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    String name;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        inputName = (EditText) findViewById(R.id.name);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                name = inputName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    phoneAuth();
                                }
                            }
                        });

            }
        });
    }

    private void phoneAuth() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.phone_authenticate, null);
        dialogBuilder.setView(dialogView);

        final EditText editNumber = (EditText) dialogView.findViewById(R.id.phone);
        final Button btnVerify = (Button) dialogView.findViewById(R.id.btn_verify);
        final ProgressBar progressBar1 = (ProgressBar) dialogView.findViewById(R.id.progressBar);

        //dialogBuilder.setTitle("Send Photos");
        final AlertDialog dialog = dialogBuilder.create();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String num = editNumber.getText().toString().trim();
                String number = num.substring(1);

                if (TextUtils.isDigitsOnly(number)) {
                    verifyOTP(number);

                }

                else {
                    Toast.makeText(getApplication(), "Enter your registered Phone number with country code", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        dialog.show();
    }

    private void verifyOTP(String number) {
        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_verify_mobile, null);
        dialogBuilder1.setView(dialogView);

        final EditText editNumber = (EditText) dialogView.findViewById(R.id.code1);
        final Button btnVerify = (Button) dialogView.findViewById(R.id.btn_verify);
        final ProgressBar progressBar1 = (ProgressBar) dialogView.findViewById(R.id.progressBar);

        //dialogBuilder.setTitle("Send Photos");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 "+" +number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        final AlertDialog dialog = dialogBuilder1.create();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String number = editNumber.getText().toString().trim();

                verifyVerificationCode(number);


            }
        });
        dialog.show();
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            EditText field = findViewById(R.id.code1);
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
//                field.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            database = FirebaseDatabase.getInstance().getReference();
                            //get current user
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // get the Firebase user
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                            // get the FCM token
                            String token = FirebaseInstanceId.getInstance().getToken();
                            // name = getIntent().getStringExtra("nameUsr");
                            // save the user info in the database to users/UID/
                            // we'll use the UID as part of the path

                            User users = new User(fbUser.getUid(), fbUser.getEmail(), token, name);
                            database.child("users").child(users.uid).setValue(users);
                            Intent i = new Intent(SignupActivity.this, Home.class);
                            startActivity(i);
                            finish();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }


                        }
                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}