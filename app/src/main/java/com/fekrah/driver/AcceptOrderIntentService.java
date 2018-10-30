package com.fekrah.driver;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fekrah.driver.activities.ChatsRoomsActivity;
import com.fekrah.driver.activities.CurrentOrderActivity;
import com.fekrah.driver.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AcceptOrderIntentService extends IntentService {

    public AcceptOrderIntentService() {
        super("AcceptOrderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotifyAccept();
    }

    private void NotifyAccept() {
        FirebaseDatabase.getInstance().getReference().child("drivers_current_order")
                .child(FirebaseAuth.getInstance().getUid()).child("offer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().toString().equals("accept")) {
                                createNotifyAccept(MainActivity.user.getName(), MainActivity.user.getImg(), getString(R.string.accept_offer), "accept_order", 124);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void createNotifyAccept(final String name, String img, final String content, final String id, final int id2) {

        Intent notifyIntent = new Intent(this, ChatsRoomsActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Create the PendingIntent
        final PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(id, "Accept Order", importance);
            channel.setDescription("Notifications for accepted orders ");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        final Bitmap[] theBitmap = {null};
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_dummy_user);
        requestOptions.error(R.drawable.ic_dummy_user);
        Glide.with(this).asBitmap().
                load(img).into(new SimpleTarget<Bitmap>(90, 90) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                theBitmap[0] = resource;
                CharSequence charSequence = (CharSequence) content;
                NotificationCompat.Style d = new NotificationCompat.BigTextStyle()
                        .bigText(charSequence);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AcceptOrderIntentService.this, id)
                        .setSmallIcon(R.drawable.shoppingcart)
                        .setContentTitle(name)
                        .setLargeIcon(resource)
                        .setContentText(content)
                        .setStyle(d)
                        .setColor(ContextCompat.getColor(AcceptOrderIntentService.this, R.color.colorPrimary))
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 100, 1000, 100})
                        .setAutoCancel(true)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_order))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AcceptOrderIntentService.this);

                notificationManager.notify(id2, mBuilder.build());

//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.new_order));
//                    if (!ringtone.isPlaying())
//                        ringtone.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

        });

    }

}
