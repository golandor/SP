package com.example.golan.spproject.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.golan.spproject.Classes.FirebaseMethods;
import com.example.golan.spproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private EditText et_fullName , et_userName, et_password, et_passwordConfirm, et_email, et_emailConfirm, et_description;
    private String fullName, userName, password, passwordConfirm, email,emailConfirm, description;
    private Button regbtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mfirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setFirebase();
        initializeAttributes();
        regBtnSetOnClickListenet();
    }

    private void regBtnSetOnClickListenet(){
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void register(){

        initialize();

        if(!validate()){
            Toast.makeText(this, "Sign up has Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            onSignUpSuccess();
        }
    }
    public void onSignUpSuccess(){
        progressDialog.setMessage("Registration User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String user_id = firebaseAuth.getCurrentUser().getUid();
                            mfirebaseMethods.addNewUser(email,fullName,user_id ,description,"","");
                            //signOut to go to the login activity
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }else{
                            Toast.makeText(SignInActivity.this,"Registration Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public boolean validate(){
        boolean valid = true;
        if(fullName.isEmpty() || fullName.length()>20){
            et_fullName.setError("Please Enter Valid Name");
            valid = false;
        }
        if(password.isEmpty() || password.contains(" ") || password.length()>9 || password.length()<6){
            et_password.setError("Please Enter Valid Password, 6-9 chars");
            valid = false;
        }
        if(!passwordConfirm.equals(password)){
            et_password.setError("Password Confirm Does Not Match Password");
            valid = false;
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Please Enter Valid Email Address");
            valid = false;
        }
        if(!emailConfirm.equals(email)){
            et_emailConfirm.setError("Email Confirm Does Not Match Email Address");
            valid = false;
        }
        return valid;
    }

    //initializing firebase auth object
    public void setFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        mfirebaseMethods = new FirebaseMethods(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    public void initializeAttributes(){
        et_fullName  = (EditText) findViewById(R.id.fullName);
        et_userName  = (EditText) findViewById(R.id.userNameSignin);
        et_password  = (EditText) findViewById(R.id.editTextPassword);
        et_passwordConfirm  = (EditText) findViewById(R.id.passwordConfirmSignin);
        et_email  = (EditText) findViewById(R.id.editTextEmail);
        et_emailConfirm  = (EditText) findViewById(R.id.emailConfirm);
        et_description = (EditText) findViewById(R.id.writeDescriptoin);
        regbtn = (Button) findViewById(R.id.signupbtnSignin);
        progressDialog = new ProgressDialog(this);
    }

    public void initialize(){
        password = et_password.getText().toString().trim();
        email = et_email.getText().toString().trim();
        fullName = et_fullName.getText().toString().trim();
        userName = et_userName.getText().toString().trim();
        passwordConfirm = et_passwordConfirm.getText().toString().trim();
        emailConfirm = et_emailConfirm.getText().toString().trim();
        description = et_description.getText().toString().trim();
    }

}
