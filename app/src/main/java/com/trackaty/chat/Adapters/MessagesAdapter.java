package com.trackaty.chat.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clock.scratch.ScratchView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private String chatKey; // the chat key
    private Chat chat; // the chat object

    // An array list for all revealed messages to be updated when fragment stops
    private static List<Message> totalRevealedList;// = new ArrayList<>();

    public MessagesAdapter(String chatKey) {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // use received chatKey to create a database ref
        mMessagesRef = mDatabaseRef.child("messages");
        this.chatKey = chatKey;

        if(totalRevealedList == null){
            totalRevealedList = new ArrayList<>();
            Log.d(TAG, "totalRevealedList is null. new ArrayList is created= " + totalRevealedList.size());
        }else{
            Log.d(TAG, "totalRevealedList is not null. size=  "+ totalRevealedList.size());
            if(totalRevealedList.size() >0){
                totalRevealedList.clear();
                Log.d(TAG, "totalRevealedList is cleared. size=  "+ totalRevealedList.size());
            }

        }
    }

    // chat object to know active end time
    public Chat getChat() {
        return chat;
    }

    // chat object to know active end time
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public List<Message> getRevealedList(){
        return totalRevealedList;
    }

    // clear revealed messages list after updating the database
    public void clearRevealedList(){
        totalRevealedList.clear();
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
                    //ReceivedHolder.mScratch.setText(message.getMessage()+ message.getKey());
                }else{
                    ReceivedHolder.mMessage.setText(null);
                    //ReceivedHolder.mScratch.setText(null);
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

                /*ReceivedHolder.setItemClickListener(new ItemClickListener(){
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(isLongClick){
                            Log.d(TAG, "Action ReceivedHolder  setItemClickListener"+ message.getKey());
                            ReceivedHolder.mResetView.setVisibility(View.GONE);

                        }
                    }
                });*/

                // Check if this conversation is active forever, if so reveal all messages
                if(null != chat && chat.getActive() == 0){
                    // Show message
                    //ReceivedHolder.mScratch.clear();
                    ReceivedHolder.mScratch.setVisibility(View.GONE);
                    ReceivedHolder.mResetView.setVisibility(View.GONE);
                }else{
                    // check if message is revealed previously or not. If so reveal only revealed messages
                    if (null != message.getRevealed() && message.getRevealed()) {
                        // Show message
                        //ReceivedHolder.mScratch.clear();
                        ReceivedHolder.mScratch.setVisibility(View.GONE);
                        ReceivedHolder.mResetView.setVisibility(View.GONE);
                        Log.d(TAG, "reveal message "+ message.getKey());
                    }else{
                        // Hide message
                        /*ReceivedHolder.mScratch.setVisibility(View.VISIBLE);
                        ReceivedHolder.mResetView.setVisibility(View.VISIBLE);*/

                        if(message.getPercent() > 0){
                            //Message was half revealed before, Don't reset it, keep it half revealed
                            Log.d(TAG, "keep message half revealed ... getPercent = "+message.getPercent()+ " message= " +message.getMessage()+ " key ="+ message.getKey());
                            ReceivedHolder.mScratch.setVisibility(View.VISIBLE);
                            ReceivedHolder.mResetView.setVisibility(View.GONE);
                        }else{
                            //Message was never half revealed. Hide it
                            Log.d(TAG, "message was never revealed Keep it hide ... getPercent = "+message.getPercent()+ " message= " +message.getMessage()+ " key ="+ message.getKey());
                            ReceivedHolder.mScratch.setVisibility(View.VISIBLE);
                            ReceivedHolder.mResetView.setVisibility(View.VISIBLE);
                            //ReceivedHolder.mScratch.reset();
                        }

                        /*if(ReceivedHolder.mScratch.getHeight()> 0 && ReceivedHolder.mScratch.getWidth() > 0 ){
                            if(message.getPercent() > 0){
                                ReceivedHolder.mScratch.reset();
                                Log.d(TAG, "onPercentChanged revealed message... getPercent = "+message.getPercent()+ " message= " +message.getMessage()+ " key ="+ message.getKey());

                            }
                        }*/

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
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldMessage.getKey() +" name: "+ oldMessage.getMessage()+" new: "+ newMessage.getKey()+ " name: "+ newMessage.getMessage()+" areItemsTheSame: " +oldMessage.getKey().equals(newMessage.getKey()));
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
                    //Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame old name: " + oldMessage.getMessage() + " new name: "+newMessage.getMessage()+ " areContentsTheSame= "+oldMessage.getMessage().equals(newMessage.getMessage()));
                    //return oldMessage.getMessage().equals(newMessage.getMessage());

                    // If updated database has item that is not revealed but it exist on the totalRevealedList
                    for (int i = 0; i < totalRevealedList.size(); i++) {
                        if(newMessage.getKey().equals(totalRevealedList.get(i).getKey())){
                            // reveal the new message because the user revealed it on the totalRevealedList
                            // but we didn't update the database yet
                            newMessage.setRevealed(true);
                        }
                    }
                    Log.d(TAG, " DIFF_CALLBACK areContentsTheSame old name: " +oldMessage.getMessage()+" value: " + oldMessage.getRevealed() + " new name: "+ newMessage.getMessage()+" value: " +newMessage.getRevealed()+ " areContentsTheSame= "+(oldMessage.getRevealed() == newMessage.getRevealed()));
                    return oldMessage.getRevealed()== newMessage.getRevealed();
                    //return false;
                }
            };

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        Message message = getItem(position);

        if(null!= (message != null ? message.getSenderId() : null) && null != currentUserId && currentUserId.equals(message.getSenderId())){
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
    public class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        View row;
        private TextView mMessage, mSentTime, mResetView;
        private ScratchView mScratch;
        private CircleImageView mAvatar;
        ItemClickListener itemClickListener;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mMessage = row.findViewById(R.id.message_button_text);
            mScratch = row.findViewById(R.id.scratch_view); // Scratch view above text view
            mResetView = row.findViewById(R.id.reset_view); // Reset view above Scratch view
            mAvatar = row.findViewById(R.id.user_image);
            mSentTime = row.findViewById(R.id.sent_time);

            /*itemClickListener= new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                }
            };*/
            //mResetView.setOnTouchListener(this);
            // Set OnTouchListener to listen to ACTION_DOWN
            mScratch.setOnTouchListener(this);

            // Set listener to user scratching
            mScratch.setEraseStatusListener(new ScratchView.EraseStatusListener() {
                @Override
                public void onProgress(int percent) {
                    Message message = getItem(getAdapterPosition());

                    if (message != null) {
                        if(null != message.getRevealed() && message.getRevealed()){
                            return;
                        }
                    }

                    /*mResetView.setVisibility(View.GONE);
                    if(message != null && message.getPercent() == 0){
                        mScratch.reset();
                        Log.d(TAG, "onPercentChanged Scratch reset. Percent= "+percent+ " message= " +message.getMessage()+ " key ="+ message.getKey());
                    }*/

                    // When user star scratching set the message's scratch percent, it's use to know if the message is half revealed or not
                    if (message != null && percent > 0 ) {
                        message.setPercent(percent);
                        Log.d(TAG, "onPercentChanged revealed message... setPercent = "+percent+ " message= " +message.getMessage()+ " key ="+ message.getKey());
                    }

                    // Reveal the message
                    if(percent > 85){
                        if(getAdapterPosition() != RecyclerView.NO_POSITION){
                            Log.d(TAG, "onRevealed onProgress: revealed message = "+message.getMessage()+ " key ="+ message.getKey());

                            // set message's reveal to true, to be remembered when scrolling, and to update the database when fragment stops
                            message.setRevealed(true);
                            // Reveal the message
                            //mScratch.clear();
                            mScratch.setVisibility(View.GONE);

                            // update message's revealed list. It's used to update the database when fragment stops
                            totalRevealedList.add(message);
                            //mMessagesRef.child(chatKey).child(message.getKey()).child("revealed").setValue(true);
                        }
                    }
                }

                @Override
                public void onCompleted(View view) {
                    //on complete is not accurate, sometimes it's not called for a strange reason
                    Log.d(TAG, "onCompleted revealed message getAdapterPosition= "+getAdapterPosition());

                }
            });

            /*//mScratch.setStrokeWidth(5);

            // A listener for reveal change to update message revealed boolean
            mScratch.setRevealListener(new ScratchTextView.IRevealListener() {
                @Override
                public void onRevealed(ScratchTextView tv) {
                    //on reveal
                    Log.d(TAG, "onRevealed percent onRevealed "+tv);
                    mScratch.setVisibility(View.GONE);

                    Log.d(TAG, "onRevealed revealed message getAdapterPosition= "+getAdapterPosition());
                    if(getAdapterPosition() != RecyclerView.NO_POSITION){
                        Message message = getItem(getAdapterPosition());
                        Log.d(TAG, "onRevealed revealed message = "+message.getMessage()+ " key ="+ message.getKey());
                        // update message reveal to true
                        //mMessagesRef.child(chatKey).child(message.getKey()).child("revealed").setValue(true);
                    }
                }


                @Override
                public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                    // on text percent reveal
                    Log.d(TAG, "onPercentChanged revealed message... percent = "+percent);

                    if(percent > 0.50){
                        Log.i(TAG, "onPercentChanged reveal ScratchTextView. color= " +mScratch.getColor());
                        stv.reveal();
                        //mScratch.reveal();
                        // Reveal the message
                        //mScratch.setVisibility(View.GONE);

                    }
                }
            });// End of RevealListener*/


        }


       /* @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "onDrag ");
            //return false;
            //itemClickListener.onClick(v, getAdapterPosition(), true);
            return false;
        }*/

       /* @Override
        public boolean onHover(View v, MotionEvent event) {
            Log.d(TAG, "onDrag ");
            return true;
        }*/

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int action = event.getAction();
            Message message = getItem(getAdapterPosition());

            /*if(v.getId() == R.id.scratch_view){
                Log.d(TAG,"Action View scratch_view. Id= "+v.getId()+ " getTag= " +v.getTag());
            }else if(v.getId() == R.id.reset_view){
                Log.d(TAG,"Action View reset_view. Id= "+v.getId()+ " getTag= " +v.getTag());
            }*/

            switch(action) {
                case (MotionEvent.ACTION_DOWN) :
                    Log.d(TAG,"Action was DOWN");
                    // Reset scratch view if messages was never revealed or half revealed
                    if(message != null && message.getPercent() == 0){
                        mScratch.reset();
                        Log.d(TAG, "onTouch Scratch reset. message= " +message.getMessage()+ " key ="+ message.getKey());
                    }
                    mScratch.onTouchEvent(event); // send the touch event to scratch view
                    mResetView.setVisibility(View.GONE);
                    return true;
                /*case (MotionEvent.ACTION_MOVE) :
                    Log.d(TAG,"Action was MOVE");
                    *//*if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        itemClickListener.onClick(v, getAdapterPosition(), true);
                    }*//*
                    return true;
                case (MotionEvent.ACTION_UP) :
                    Log.d(TAG,"Action was UP");
                    v.performClick();
                    return true;
                case (MotionEvent.ACTION_CANCEL) :
                    Log.d(TAG,"Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE) :
                    Log.d(TAG,"Movement occurred outside bounds " +
                            "of current screen element");
                    return true;*/

            }

            return false;
        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
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

