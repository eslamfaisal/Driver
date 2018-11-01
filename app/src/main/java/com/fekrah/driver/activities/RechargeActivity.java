package com.fekrah.driver.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fekrah.driver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends AppCompatActivity {

    @BindView(R.id.codeEdt)
    EditText codEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.recharge)
    public void chrge() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(new ProgressBar(this));
        dialog.show();
        FirebaseDatabase.getInstance().getReference().child("codes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.getValue()!=null){
                        if (dataSnapshot.hasChild(codEdt.getText().toString())) {
                            Toast.makeText(RechargeActivity.this, "This code recharged before ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("codes").child(codEdt.getText().toString())
                                    .setValue(codEdt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("drivers")
                                                .child(FirebaseAuth.getInstance().getUid()).child("available_balance")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() != null) {
                                                            String balance = dataSnapshot.getValue().toString();
                                                            int count = Integer.valueOf(balance) + 5;
                                                            FirebaseDatabase.getInstance().getReference().child("drivers")
                                                                    .child(FirebaseAuth.getInstance().getUid()).child("available_balance")
                                                                    .setValue(count).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    dialog.dismiss();
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }
                            });


                        }
                        //}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
