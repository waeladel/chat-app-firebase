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
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends PagedListAdapter<Chat, ChatsAdapter.ViewHolder> {

    private final static String TAG = ChatsAdapter.class.getSimpleName();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser != null ? currentUser.getUid() : null;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private StorageReference mStorageRef;

    public ChatsAdapter() {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.ViewHolder holder, final int position) {

        final Chat chat = getItem(position);
        if (chat != null) {

            // LastMessage text value
            if (null != chat.getLastMessage()) {
                holder.mLastMessage.setText(chat.getLastMessage()+ chat.getKey());
            }else{
                holder.mLastMessage.setText(null);
            }

            // LastSentTime text value
            if (null != chat.getLastSent()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(chat.getLastSentLong());
                String sentTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(c.getTime());
                holder.mLastSentTime.setText(sentTime);
            }else{
                holder.mLastSentTime.setText(null);
            }


        }

    }


    // CALLBACK to calculate the difference between the old item and the new item
    public static final DiffUtil.ItemCallback<Chat> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Chat>() {
                // User details may have changed if reloaded from the database,
                // but ID is fixed.

                // if the two items are the same
                @Override
                public boolean areItemsTheSame(Chat oldChat, Chat newChat) {
                    /*Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + (oldUser.getCreatedLong() == newUser.getCreatedLong()));
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldUser.getCreatedLong() +" new: "+ newUser.getCreatedLong());*/
                    return oldChat.getKey().equals(newChat.getKey());
                    //return oldChat.getLastSentLong() == (newChat.getLastSentLong());
                    //return true;
                }

                // if the content of two items is the same
                @Override
                public boolean areContentsTheSame(Chat oldChat, Chat newChat) {
                   /* Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame object " + (oldUser.equals(newUser)));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame Names() " + (oldUser.getName().equals(newUser.getName())));
                    Log.d(TAG, "  DIFF_CALLBACK areContentsTheSame old name: " + oldUser.getName() + " new name: "+newUser.getName());
*/
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    //TODO override equals method on User object
                    return oldChat.getLastMessage().equals(newChat.getLastMessage());
                    //return false;
                }
            };


   /* @Override
    public void submitList(PagedList<Message> pagedList) {
        super.submitList(pagedList);
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<Message> currentList) {
        super.onCurrentListChanged(currentList);
    }*/


    /// ViewHolder for ReceivedMessages list /////
    public class ViewHolder extends RecyclerView.ViewHolder {

        View row;
        private TextView mLastMessage, mLastSentTime, mChatTitle;
        private CircleImageView mAvatar;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mLastMessage = row.findViewById(R.id.last_message);
            mAvatar = row.findViewById(R.id.user_image);
            mLastSentTime = row.findViewById(R.id.last_sent);
        }


    }

}

