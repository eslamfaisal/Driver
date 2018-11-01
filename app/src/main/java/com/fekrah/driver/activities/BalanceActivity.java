package com.fekrah.driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fekrah.driver.R;
import com.fekrah.driver.models.Driver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BalanceActivity extends AppCompatActivity {

//    @BindViews(R.id.recharge)
//    DrawMeButton rechargBtn;

    @BindView(R.id.available_balance)
    TextView available;

    @BindView(R.id.taken_balance)
    TextView taken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ButterKnife.bind(this);

        FirebaseDatabase.getInstance().getReference().child("drivers").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Driver driver = dataSnapshot.getValue(Driver.class);
                            if (driver != null) {

                                taken.setText(String.valueOf(driver.getTaken_balance()));
                                available.setText(String.valueOf(driver.getAvailable_balance()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//        rechargBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @OnClick(R.id.recharge)
    public void goRecharge() {
        startActivity(new Intent(this, RechargeActivity.class));
    }
}
