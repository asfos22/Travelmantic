package com.andela.travelmantic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private Button  submitButton;

    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private EditText emailEditText, passwordEditText, firstLastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstLastNameEditText = findViewById(R.id.firstLastNameEditText);
        progressBar = findViewById(R.id.progressBar);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = emailEditText.getText().toString().trim();
                String userPassword = passwordEditText.getText().toString().trim();
                String firstLastName = firstLastNameEditText.getText().toString().trim();

                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (! TextUtils.isEmpty(firstLastName)) {

                    progressBar.setVisibility(View.VISIBLE);

                    //Get Firebase auth instance
                    auth = FirebaseAuth.getInstance();

                    //create user
                    auth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(getApplicationContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.authentication_failed) + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        //startActivity(new Intent(getApplicationContext(), CompanyRegistrationActivity.class));
                                        // finish();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(getApplicationContext(), R.string.password_does_not_match,
                            Toast.LENGTH_LONG).show();

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
