package com.trackaty.chat.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Message;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends PagedListAdapter<Message, RecyclerView.ViewHolder> {

    private final static String TAG = MessagesAdapter.class.getSimpleName();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser != null ? currentUser.getUid() : null;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private StorageReference mStorageRef;

    public MessagesAdapter() {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType){
            case VIEW_TYPE_MESSAGE_RECEIVED:
                //// If some other user sent the message
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_item, parent, false);
                return new ReceivedMessageHolder(view);
            case VIEW_TYPE_MESSAGE_SENT:
                // // If the current user is the sender of the message;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_item , parent, false);
                return new SentMessageHolder(view);
            default:
                //// If some other user sent the message
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_item, parent, false);
                return new ReceivedMessageHolder(view);

        }
        // default
        /*view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_item, parent, false);
        return new SentMessageHolder(view);*/
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final Message message = getItem(position);

        if (holder instanceof ReceivedMessageHolder){
            final ReceivedMessageHolder ReceivedHolder = (ReceivedMessageHolder) holder;

            if (message != null) {
                //holder.bindTo(user);
                //Log.d(TAG, "mama  onBindViewHolder. users key"  +  user.getCreatedLong()+ "name: "+user.getName());
                // click listener using interface
                // user name text value
                if (null != message.getMessage()) {
                    ReceivedHolder.mMessage.setText(message.getMessage()+ message.getKey());
                }else{
                    ReceivedHolder.mMessage.setText(null);
                }

                if (null != message.getSenderId()) {
                    // [START create_storage_reference]
                    //ReceivedHolder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                    mStorageRef.child("images/"+message.getSenderId()+"/"+ AVATAR_THUMBNAIL_NAME).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.drawable.ic_user_account_grey_white)
                                    .error(R.drawable.ic_broken_image)
                                    .into(ReceivedHolder.mAvatar);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            ReceivedHolder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                        }
                    });
                }else{
                    // Handle if getSenderId() is null
                    ReceivedHolder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                }

                if (null != message.getCreated()) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(message.getCreatedLong());
                    String sentTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(c.getTime());
                    ReceivedHolder.mSentTime.setText(sentTime);
                }else{
                    ReceivedHolder.mSentTime.setText(null);
                }
            }
        }

        if (holder instanceof SentMessageHolder){
            SentMessageHolder SentHolder = (SentMessageHolder) holder;

            if (message != null) {
                //holder.bindTo(user);
                //Log.d(TAG, "mama  onBindViewHolder. users key"  +  user.getCreatedLong()+ "name: "+user.getName());
                // click listener using interface
                // user name text value
                if (null != message.getMessage()) {
                    SentHolder.mMessage.setText(message.getMessage()+ message.getKey());
                }else{
                    SentHolder.mMessage.setText(null);
                }

                if (null != message.getCreated()) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(message.getCreatedLong());
                    String sentTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(c.getTime());
                    SentHolder.mSentTime.setText(sentTime);
                }else{
                    SentHolder.mSentTime.setText(null);
                }
            }
        }

    }


    // CALLBACK to calculate the difference between the old item and the new item
    public static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Message>() {
                // User details may have changed if reloaded from the database,
                // but ID is fixed.

                // if the two items are the same
                @Override
                public boolean areItemsTheSame(Message oldMessage, Message newMessage) {
                    /*Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + (oldUser.getCreatedLong() == newUser.getCreatedLong()));
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldUser.getCreatedLong() +" new: "+ newUser.getCreatedLong());*/
                    return oldMessage.getKey().equals(newMessage.getKey());
                    //return true;
                }

                // if the content of two items is the same
                @Override
                public boolean areContentsTheSame(Message oldMessage, Message newMessage) {
                   /* Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame object " + (oldUser.equals(newUser)));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame Names() " + (oldUser.getName().equals(newUser.getName())));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame old name: " + oldUser.getName() + " new name: "+newUser.getName());
*/
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    //TODO override equals method on User object
                    return oldMessage.getMessage().equals(newMessage.getMessage());
                    //return false;
                }
            };

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        Message message = getItem(position);

        if(null!= message.getSenderId() && null != currentUserId && currentUserId.equals(message.getSenderId())){
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        }else{// it's a message from chat user
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }

   /* @Override
    public void submitList(PagedList<Message> pagedList) {
        super.submitList(pagedList);
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Message> currentList) {
        super.onCurrentListChanged(currentList);
    }*/


    /// ViewHolder for ReceivedMessages list /////
    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView mMessage, mSentTime;
        private CircleImageView mAvatar;


        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mMessage = row.findViewById(R.id.message_button_text);
            mAvatar = row.findViewById(R.id.user_image);
            mSentTime = row.findViewById(R.id.sent_time);
        }


    }

    /// ViewHolder for SentMessages list /////
    public class SentMessageHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView mMessage, mSentTime;
        private CircleImageView mAvatar;


        public SentMessageHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mMessage = row.findViewById(R.id.message_button_text);
            mAvatar = row.findViewById(R.id.user_image);
            mSentTime = row.findViewById(R.id.sent_time);
        }

    }

}

