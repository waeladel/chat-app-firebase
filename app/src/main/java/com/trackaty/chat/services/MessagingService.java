package com.trackaty.chat.services;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trackaty.chat.R;

import java.util.HashSet;
import java.util.Set;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import static com.trackaty.chat.App.LIKES_CHANNEL_ID;
import static com.trackaty.chat.App.MESSAGES_CHANNEL_ID;
import static com.trackaty.chat.App.PICK_UPS_CHANNEL_ID;
import static com.trackaty.chat.App.REQUESTS_CHANNEL_ID;

public class MessagingService extends FirebaseMessagingService {

    private final static String TAG = MessagingService.class.getSimpleName();
    private NotificationManagerCompat notificationManager;

    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId; //get current to get uid

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private StorageReference mStorageRef;

    private static final String NOTIFICATION_TYPE_PICK_UP = "Pickup";
    private static final String NOTIFICATION_TYPE_MESSAGE = "Message";
    private static final String NOTIFICATION_TYPE_LIKE = "Like"; // if other user liked me. I did't liked him
    private static final String NOTIFICATION_TYPE_LIKE_BACK = "LikeBack"; // if other user liked me after i liked him
    private static final String NOTIFICATION_TYPE_REQUESTS_SENT = "RequestSent";
    private static final String NOTIFICATION_TYPE_REQUESTS_APPROVED = "RequestApproved";

    private static final int LIKES_NOTIFICATION_ID = 1;
    private static final int PICK_UPS_NOTIFICATION_ID = 2;
    private static final int MESSAGES_NOTIFICATION_ID = 3;
    private static final int REQUESTS_SENT_NOTIFICATION_ID = 4;
    private static final int REQUESTS_APPROVED_NOTIFICATION_ID = 5;

    private Target mTarget;
    private ImageView mAvatarImageView;
    private PendingIntent pendingIntent;
    private Bundle bundle;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";

    final Set<Target> protectedFromGarbageCollectorTargets = new HashSet<>();


    public MessagingService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MessagingService onCreate");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;
        notificationManager = NotificationManagerCompat.from(this);

        mAvatarImageView = new ImageView(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived. remoteMessage= " + remoteMessage);

        //String notificationTitle = remoteMessage.getNotification().
        //RemoteMessage.DatabaseNotification notification =  remoteMessage.getNotification();
        Log.d(TAG, "onMessageReceived. remoteMessage From= " + remoteMessage.getFrom());
        Log.d(TAG, "onMessageReceived. remoteMessage MessageId= " + remoteMessage.getMessageId());
        Log.d(TAG, "onMessageReceived. remoteMessage MessageType= " + remoteMessage.getMessageType());
        Log.d(TAG, "onMessageReceived. remoteMessage To= " + remoteMessage.getTo());
        Log.d(TAG, "onMessageReceived. remoteMessage SentTime= " + remoteMessage.getSentTime());
        //Log.d(TAG, "onMessageReceived. remoteMessage ClickAction= " + notification.getClickAction());

        // Get notification data
        //Map<String, String> data = remoteMessage.getData();

        String type = remoteMessage.getData().get("type");
        String senderId = remoteMessage.getData().get("senderId");
        String notificationId = remoteMessage.getData().get("notificationId");
        String destinationId = remoteMessage.getData().get("destinationId");
        String name = remoteMessage.getData().get("name");
        String avatar = remoteMessage.getData().get("avatar");

        Log.d(TAG, "onMessageReceived. remoteMessage type= " + type);
        Log.d(TAG, "onMessageReceived. remoteMessage senderId= " + senderId);
        Log.d(TAG, "onMessageReceived. remoteMessage notificationId= " + notificationId);
        Log.d(TAG, "onMessageReceived. remoteMessage destinationId= " + destinationId);
        Log.d(TAG, "onMessageReceived. remoteMessage name= " + name);
        Log.d(TAG, "onMessageReceived. remoteMessage avatar= " + avatar);

        bundle = new Bundle();

        if (remoteMessage.getData().size()>0 && type != null) {
            String messageTitle;
            String messageBody;
            switch (type){
                case NOTIFICATION_TYPE_LIKE:
                    Log.d(TAG, "Notification type= = " + NOTIFICATION_TYPE_LIKE);
                    messageTitle = getString(R.string.notification_like_title);
                    messageBody = getString(R.string.notification_like_body, name);

                    bundle.clear();
                    bundle.putString("userId", destinationId);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.profileFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type ,notificationId, pendingIntent, LIKES_CHANNEL_ID);
                    break;
                case NOTIFICATION_TYPE_LIKE_BACK:
                    Log.d(TAG, "Notification type= = " + NOTIFICATION_TYPE_LIKE_BACK);
                    messageTitle = getString(R.string.notification_like_title);
                    messageBody = getString(R.string.notification_like_back_body, name);

                    bundle.clear();
                    bundle.putString("userId", destinationId);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.profileFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type ,notificationId, pendingIntent, LIKES_CHANNEL_ID);
                    break;
                case NOTIFICATION_TYPE_PICK_UP:
                    Log.d(TAG, "Notification type = " + NOTIFICATION_TYPE_PICK_UP);
                    messageTitle = getString(R.string.notification_pick_up_title);
                    messageBody = getString(R.string.notification_pick_up_body, name);

                    bundle.clear();
                    bundle.putString("chatId", destinationId);
                    bundle.putString("chatUserId", senderId);
                    bundle.putBoolean("isGroup", false);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.messagesFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type , notificationId,pendingIntent, PICK_UPS_CHANNEL_ID);
                    break;
                case NOTIFICATION_TYPE_MESSAGE:
                    Log.d(TAG, "Notification type = " + NOTIFICATION_TYPE_MESSAGE);
                    messageTitle = getString(R.string.notification_message_title);
                    messageBody = getString(R.string.notification_message_body, name);

                    bundle.clear();
                    bundle.putString("chatId", destinationId);
                    bundle.putString("chatUserId", senderId);
                    bundle.putBoolean("isGroup", false);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.messagesFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type, notificationId, pendingIntent, MESSAGES_CHANNEL_ID);
                    break;
                case NOTIFICATION_TYPE_REQUESTS_SENT:
                    Log.d(TAG, "Notification type = " + NOTIFICATION_TYPE_REQUESTS_SENT);
                    messageTitle = getString(R.string.notification_request_sent_title);
                    messageBody = getString(R.string.notification_request_sent_body, name);

                    bundle.clear();
                    bundle.putString("userId", destinationId);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.profileFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type, notificationId,pendingIntent, REQUESTS_CHANNEL_ID);
                    break;
                case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                    Log.d(TAG, "Notification type = " + NOTIFICATION_TYPE_REQUESTS_APPROVED);
                    messageTitle = getString(R.string.notification_request_approved_title);
                    messageBody = getString(R.string.notification_request_approved_body, name);

                    bundle.clear();
                    bundle.putString("userId", destinationId);
                    pendingIntent = new NavDeepLinkBuilder(this)
                            .setGraph(R.navigation.nav_graph)
                            .setDestination(R.id.profileFragment)
                            .setArguments(bundle)
                            .createPendingIntent();

                    sendNotification(messageTitle, messageBody, avatar, type, notificationId,pendingIntent, REQUESTS_CHANNEL_ID);
                    break;

            }

        }
    }

    private void sendNotification(String messageTitle, String messageBody, String avatar, String type, String notificationId, PendingIntent pendingIntent, String channelId) {

        // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, channelId)
                    //.setLargeIcon(bitmap)
                    //.setSmallIcon(R.mipmap.ic_launcher)
                    .setSmallIcon(R.drawable.album_ic_back_white)
                    .setColor(getResources().getColor(R.color.album_ColorPrimary))
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            // if Avater is not null, set it as a large icon
        if(avatar != null){
            final Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Call from here
                    Picasso.get()
                            .load(avatar)
                            .resize(100, 100)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
                                    builder.setLargeIcon(bitmap); // set avatar as a large icon
                                    //notificationId is a unique int for each notification that you must define
                                    //notificationManager.notify(11, builder.build());
                                    //notificationManager.notify("keystring+type", 11, builder.build());
                                    // change notification's tag and Id's according to notification's types
                                    switch (type){
                                        case NOTIFICATION_TYPE_LIKE:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_LIKE);
                                            notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_LIKE_BACK:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_LIKE_BACK);
                                            notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_PICK_UP:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_PICK_UP);
                                            notificationManager.notify(notificationId, PICK_UPS_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_MESSAGE:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_MESSAGE);
                                            notificationManager.notify(notificationId, MESSAGES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_REQUESTS_SENT:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_REQUESTS_SENT);
                                            notificationManager.notify(notificationId, REQUESTS_SENT_NOTIFICATION_ID, builder.build());

                                            break;
                                        case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                                            Log.d(TAG, "onBitmapLoaded Notification type = " + NOTIFICATION_TYPE_REQUESTS_APPROVED);
                                            notificationManager.notify(notificationId, REQUESTS_APPROVED_NOTIFICATION_ID, builder.build());
                                            break;

                                    }
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    //notificationId is a unique int for each notification that you must define
                                    switch (type){
                                        case NOTIFICATION_TYPE_LIKE:
                                            notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_LIKE_BACK:
                                            notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_PICK_UP:
                                            notificationManager.notify(notificationId, PICK_UPS_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_MESSAGE:
                                            notificationManager.notify(notificationId, MESSAGES_NOTIFICATION_ID, builder.build());
                                            break;
                                        case NOTIFICATION_TYPE_REQUESTS_SENT:
                                            notificationManager.notify(notificationId, REQUESTS_SENT_NOTIFICATION_ID, builder.build());

                                            break;
                                        case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                                            notificationManager.notify(notificationId, REQUESTS_APPROVED_NOTIFICATION_ID, builder.build());
                                            break;

                                    }
                                }

                                @Override
                                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                    // Do nothing
                                }
                            });
                }
            });
        }else{// avatar is null. create a notification with no large icon

            switch (type){
                case NOTIFICATION_TYPE_LIKE:
                    notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                    break;
                case NOTIFICATION_TYPE_LIKE_BACK:
                    notificationManager.notify(notificationId, LIKES_NOTIFICATION_ID, builder.build());
                    break;
                case NOTIFICATION_TYPE_PICK_UP:
                    notificationManager.notify(notificationId, PICK_UPS_NOTIFICATION_ID, builder.build());
                    break;
                case NOTIFICATION_TYPE_MESSAGE:
                    notificationManager.notify(notificationId, MESSAGES_NOTIFICATION_ID, builder.build());
                    break;
                case NOTIFICATION_TYPE_REQUESTS_SENT:
                    notificationManager.notify(notificationId, REQUESTS_SENT_NOTIFICATION_ID, builder.build());

                    break;
                case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                    notificationManager.notify(notificationId, REQUESTS_APPROVED_NOTIFICATION_ID, builder.build());
                    break;

            }
        }


        /*final long ONE_MEGABYTE = 1024 * 1024;
        mStorageRef.child("images/"+senderId+"/"+ AVATAR_THUMBNAIL_NAME).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                // Bitmap is loaded, use image here
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, channelId)
                        .setLargeIcon(bitmap)
                        //.setSmallIcon(R.mipmap.ic_launcher)
                        .setSmallIcon(R.drawable.album_ic_back_white)
                        .setColor(getResources().getColor(R.color.album_ColorPrimary))
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                //notificationId is a unique int for each notification that you must define
                notificationManager.notify(11, builder.build());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/

        // get person avatar bitmap
        /*mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Bitmap is loaded, use image here
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, channelId)
                        //.setSmallIcon(R.mipmap.ic_launcher)
                        .setSmallIcon(R.drawable.album_ic_back_white)
                        .setLargeIcon(bitmap)
                        .setColor(getResources().getColor(R.color.album_ColorPrimary))
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Send Notification without a person avatar
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, channelId)
                        //.setSmallIcon(R.mipmap.ic_launcher)
                        .setSmallIcon(R.drawable.album_ic_back_white)
                        //.setLargeIcon(bitmap)
                        .setColor(getResources().getColor(R.color.album_ColorPrimary))
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(11, builder.build());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };

        Picasso.get().load(avatar).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "onBitmapLoaded. bitmap From= " + from);

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d(TAG, "onBitmapFailed. error= " + errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/

    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        if(mCurrentUserId != null){
            //creates a new node of user's token and set its value to true.
            mUserRef = mDatabaseRef.child("users").child(mCurrentUserId);
            mUserRef.child("tokens").child(token).setValue(true);
        }

        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                    }
                });*/
    }

}
