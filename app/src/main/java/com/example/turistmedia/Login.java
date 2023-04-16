package com.example.turistmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    EditText emailEt,passwordEt;
    TextView nothaveAcoount,passwordRecover;
    Button loginbtn;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        nothaveAcoount = findViewById(R.id.nothave_accountTv);
        loginbtn = findViewById(R.id.loginbtn);
        passwordRecover = findViewById(R.id.recoverPassTV);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        nothaveAcoount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });

        passwordRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswodDilog();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEt.setError("Invalid Email!!");
                    emailEt.setFocusable(true);
                }else{
                    loginUser(email,password);
                }
            }
        });
    }

    private void showRecoverPasswodDilog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);

        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        emailEt.setMaxEms(16);


        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = emailEt.getText().toString().trim();
                begainRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.dismiss();
            }
        });

        builder.create().show();


    }

    private void begainRecovery(String email) {
        progressDialog.setMessage("Email Sending.....");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Email Sent",Toast.LENGTH_SHORT).show();

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Failed...",Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        progressDialog.setMessage("Login.....");

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, (task)-> {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();


                            startActivity(new Intent(Login.this, DashBoardActivity.class));
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Authentication Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                }).addOnFailureListener((e)-> {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });

    }
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
    }

    private  void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,(task)->{
                    if (task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();


                        if(task.getResult().getAdditionalUserInfo().isNewUser()){
                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("Uid",uid);
                            hashMap.put("name","");
                            hashMap.put("phone","");
                            hashMap.put("image","");
                            hashMap.put("cover","");

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference reference = database.getReference("Users");

                            reference.child(uid).setValue(hashMap);
                        }




                        Toast.makeText(Login.this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, DashBoardActivity.class));
                        finish();
                    }else{
                     Toast.makeText(Login.this,"Login Failed....",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener((e)->{
                    Toast.makeText(Login.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
    }
}