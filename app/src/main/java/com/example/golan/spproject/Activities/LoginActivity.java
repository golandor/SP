package com.example.golan.spproject.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.golan.spproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonLogin;
    private Button buttonViewSignin;
    private EditText editTextMail;
    private EditText editTextPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //NewsFeeds activity here
            finish();
            startActivity(new Intent(getApplicationContext(),NewsFeeds.class));
        }
        initializeAttributes();

        buttonLogin.setOnClickListener(this);
        buttonViewSignin.setOnClickListener(this);

    }

    //login btn click
    @Override
    public void onClick(View view) {
        if(view == buttonLogin){
            userLogin();
        }
        if(view == buttonViewSignin){
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private void userLogin() {
        String email = editTextMail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // start the newsFeeds activity
                            finish();
                            startActivity(new Intent(getApplicationContext(),NewsFeeds.class));
                        }
                        else{
                            emailOrPassIncorrect();
                        }
                    }
                });
    }
    public void emailOrPassIncorrect(){
        Toast.makeText(this,"E-mail or Password incorrect",Toast.LENGTH_SHORT).show();
    }
    public void initializeAttributes(){
        buttonLogin = (Button) findViewById(R.id.Loginbtn);
        buttonViewSignin = (Button) findViewById(R.id.signupbtnLogin);
        editTextMail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
    }
}
