package com.trackaty.chat.Adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cooltechworks.views.ScratchTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mMessagesRef;

    private String chatKey;
    private Chat chat;

    public MessagesAdapter(String chatKey) {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mMessagesRef = mDatabaseRef.child("messages");
        this.chatKey = chatKey;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
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
                    ReceivedHolder.mScratch.setText(message.getMessage()+ message.getKey());
                }else{
                    ReceivedHolder.mMessage.setText(null);
                    ReceivedHolder.mScratch.setText(null);
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

                // check if this conversation is active forever
                if(null != chat && chat.getActive() == 0){
                    // Show message
                    ReceivedHolder.mScratch.setVisibility(View.GONE);

                }else{
                    // check if message is revealed previously or not
                    if (null != message.getRevealed() && message.getRevealed()) {
                        // Show message
                        ReceivedHolder.mScratch.setVisibility(View.GONE);
                        Log.d(TAG, "reveal message "+ message.getKey());
                    }else{
                        // Hide message
                        ReceivedHolder.mScratch.setVisibility(View.VISIBLE);
                    }
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
                    //return oldMessage.getRevealed()== newMessage.getRevealed();
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

    @Nullable
    @Override
    protected Message getItem(int position) {
        return super.getItem(position);
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
        private ScratchTextView mScratch;
        private CircleImageView mAvatar;


        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mMessage = row.findViewById(R.id.message_button_text);
            mScratch = row.findViewById(R.id.message_scratch_view); // Scratch view above text view
            mAvatar = row.findViewById(R.id.user_image);
            mSentTime = row.findViewById(R.id.sent_time);

            //mScratch.setStrokeWidth(5);

            // A listener for reveal change to update message revealed boolean
            mScratch.setRevealListener(new ScratchTextView.IRevealListener() {
                @Override
                public void onRevealed(ScratchTextView tv) {
                    //on reveal
                    Log.d(TAG, "percent onRevealed "+tv);
                }


                @Override
                public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                    // on text percent reveal
                    Log.d(TAG, "percent = "+percent);

                    if(percent > 0.90){
                        Log.i(TAG, "reveal ScratchTextView. color= " +mScratch.getColor());
                        //stv.reveal();
                        //mScratch.reveal();
                        // Reveal the message
                        mScratch.setVisibility(View.GONE);
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                            Message message = getItem(getAdapterPosition());
                            Log.d(TAG, "revealed message = "+message.getMessage()+ " key ="+ message.getKey());
                            // update message reveal to true
                            mMessagesRef.child(chatKey).child(message.getKey()).child("revealed").setValue(true);
                        }
                    }
                }
            });// End of RevealListener

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

