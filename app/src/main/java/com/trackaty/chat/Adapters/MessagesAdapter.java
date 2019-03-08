package com.trackaty.chat.Adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Fragments.MainFragmentDirections;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.DateHelper;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;
import com.yanzhenjie.album.widget.photoview.PhotoViewAttacher;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends PagedListAdapter<Message, MessagesAdapter.MessageViewHolder> {

    private final static String TAG = MessagesAdapter.class.getSimpleName();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";

    private StorageReference mStorageRef;

    public MessagesAdapter() {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        final Message message = getItem(position);
        if (message != null) {
            //holder.bindTo(user);
            //Log.d(TAG, "mama  onBindViewHolder. users key"  +  user.getCreatedLong()+ "name: "+user.getName());
            // click listener using interface
            // user name text value
            if (null != message.getMessage()) {
                holder.mMessage.setText(message.getMessage()+ message.getKey());
            }else{
                holder.mMessage.setText(null);
            }

            if (null != message.getSender()) {
                // [START create_storage_reference]
                holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                mStorageRef.child("images/"+message.getSender()+"/"+ AVATAR_THUMBNAIL_NAME).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.ic_user_account_grey_white)
                                .error(R.drawable.ic_broken_image)
                                .into(holder.mAvatar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                    }
                });
            }else{
                // Handle if getSender() is null
                holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
            }

            if (null != message.getCreated()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(message.getCreatedLong());
                String sentTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(c.getTime());
                holder.mSentTime.setText(sentTime);
            }else{
                holder.mSentTime.setText(null);
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


    /// ViewHolder for trips list /////
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView mMessage, mSentTime;
        private CircleImageView mAvatar;


        public MessageViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mMessage = row.findViewById(R.id.message_text);
            mAvatar = row.findViewById(R.id.user_image);
            mSentTime = row.findViewById(R.id.sent_time);
        }


    }

}

