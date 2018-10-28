package com.fekrah.driver.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fekrah.driver.R;
import com.fekrah.driver.helper.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafakob.drawme.DrawMeButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.forget_pass)
    DrawMeButton forget;

    @BindView(R.id.new_account)
    DrawMeButton register;

    @BindView(R.id.login)
    DrawMeButton login;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/AdvertisingBold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Utility.hideSoftKeyboard(this);
        dialog = new Dialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.showProgressDialog(dialog, true);
                if ((isValidEmail(email.getText().toString()))) {
                    if (email.getText().toString().equals("") ||
                            email.getText().toString()
                                    .equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                        Utility.showProgressDialog(dialog, false);
                        displayMessage(getString(R.string.email_validation));

                    } else if (password.getText().toString().equals("") ||
                            password.getText().toString()
                                    .equalsIgnoreCase(getString(R.string.password_txt))) {
                        Utility.showProgressDialog(dialog, false);
                        displaySnackbar(getString(R.string.password_validation));

                    } else if (password.length() < 6) {
                        Utility.showProgressDialog(dialog, false);
                        displaySnackbar(getString(R.string.password_size));
                    } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                        Utility.showProgressDialog(dialog, false);

                        displaySnackbar(getString(R.string.password_validation));
                    } else if (password.length() < 6) {
                        Utility.showProgressDialog(dialog, false);
                        displaySnackbar(getString(R.string.password_size));
                    } else {

                        sigin(email.getText().toString(), password.getText().toString());
                    }
                } else {
                    Utility.showProgressDialog(dialog, false);
                    displaySnackbar(getString(R.string.check_email_or_password));
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }


    private void sigin(String email, String password) {

        Utility.showProgressDialog(dialog, true);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("drivers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() == null) {
                                                showSnackBar(getString(R.string.bo_driver));
                                                FirebaseAuth.getInstance().signOut();
                                                Utility.showProgressDialog(dialog, false);

                                            } else {
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        } else {
                            Utility.showProgressDialog(dialog, false);
                            displaySnackbar("check email and password");
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e.equals("ERROR_USER_NOT_FOUND")) {
                            displaySnackbar("yes");
                        }
                    }
                });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
