package com.fekrah.driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.fekrah.driver.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends BaseActivity {

    private Handler handleCheckStatus;
    LinearLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handleCheckStatus = new Handler();

        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }

        }, 3000);
    }
}
