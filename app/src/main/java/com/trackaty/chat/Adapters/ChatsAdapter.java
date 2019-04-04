package com.trackaty.chat.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Fragments.ChatsFragmentDirections;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends PagedListAdapter<Chat, ChatsAdapter.ViewHolder> {

    private final static String TAG = ChatsAdapter.class.getSimpleName();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUserId ;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private StorageReference mStorageRef;

    public ChatsAdapter() {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        mStorageRef = FirebaseStorage.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            currentUserId = currentUser.getUid();
        }
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

            // participants' avatars and names
            if (null != chat.getMembers()) {
                // loop to get all chat members HashMap
                //String participantId;
                final List<User> membersList = new ArrayList<>();
                for (Object o : chat.getMembers().entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    Log.d(TAG, "mama Chats getMember = " + pair.getKey() + " = " + pair.getValue() + currentUser.getUid());

                    if (!currentUser.getUid().equals(pair.getKey())) {
                        User user = chat.getMembers().get(String.valueOf(pair.getKey()));
                        if (user != null) {
                            user.setKey(String.valueOf(pair.getKey()));
                            membersList.add(user);
                            Log.d(TAG, "mama Chats membersListSize=" + membersList.size());
                            Log.d(TAG, "mama Chats getMember name=" + user.getName());
                        }
                    }
                    //iterator.remove(); // avoids a ConcurrentModificationException
                }

                holder.setItemClickListener(new ItemClickListener(){
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if(membersList.size()== 1){
                            // it's private chat
                            switch (view.getId()) {
                                case R.id.user_image: // only avatar is clicked
                                    Log.i(TAG, "user avatar clicked= "+view.getId());
                                    Log.i(TAG, "user avatar currenUserId= "+currentUserId+ " userId " + membersList.get(0).getKey() );
                                    NavDirections ProfileDirection = ChatsFragmentDirections.actionChatsFragmentToProfileFragment(currentUserId, membersList.get(0).getKey(), membersList.get(0));
                                    Navigation.findNavController(view).navigate(ProfileDirection);
                                    break;
                                default://-1 entire row is clicked
                                    Log.i(TAG, "user row clicked= "+view.getId());
                                    NavDirections MessageDirection = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(currentUserId , chat.getKey(), membersList.get(0).getKey(), false);
                                    Navigation.findNavController(view).navigate(MessageDirection);
                                    break;
                            }
                        }else{
                            // it's group chat
                            switch (view.getId()) {
                                case R.id.user_image: // only avatar is clicked
                                    /*Log.i(TAG, "user avatar clicked= "+view.getId());
                                    NavDirections ProfileDirection = MainFragmentDirections.actionMainToProfile(currentUserId, membersList.get(0).getKey(), membersList.get(0));
                                    Navigation.findNavController(view).navigate(ProfileDirection);*/
                                    break;
                                default://-1 entire row is clicked
                                    Log.i(TAG, "user row clicked= "+view.getId());
                                    NavDirections MessageDirection = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(currentUserId , chat.getKey(), membersList.get(0).getKey(),true);
                                    Navigation.findNavController(view).navigate(MessageDirection);
                                    break;
                            }
                        }


                    }
                });

                switch (membersList.size()){
                    case 1:// there is only one member other than current user
                        Log.d(TAG, "mama getChats membersList name= "+membersList.get(0).getName());
                        Log.d(TAG, "mama getChats membersList key= "+membersList.get(0).getKey());
                        Log.d(TAG, "mama getChats membersList avatar= "+membersList.get(0).getAvatar());
                        // names text value
                        if (null != membersList.get(0).getName()) {
                            holder.mChatTitle.setText(membersList.get(0).getName());
                        }else{
                            holder.mChatTitle.setText(null);
                        }

                        // Avatar
                        if (null != membersList.get(0).getAvatar()) {
                            holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                            Picasso.get()
                                    .load(membersList.get(0).getAvatar())
                                    .placeholder(R.drawable.ic_user_account_grey_white)
                                    .error(R.drawable.ic_broken_image)
                                    .into(holder.mAvatar);
                        }else{
                            // end of user avatar
                            holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                        }

                        /*// [START create_storage_reference]
                        //ReceivedHolder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                        mStorageRef.child("images/"+membersList.get(0).getKey()+"/"+ AVATAR_THUMBNAIL_NAME).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                        });*/

                        break;
                    case 2:// there is 2 member other than current user
                        Log.d(TAG, "mama getChats getMember= "+membersList.get(0));
                        Log.d(TAG, "mama getChats getMember= "+membersList.get(1));
                        break;
                }


            }else{
                Log.d(TAG, "mama Chats= null");
                holder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View row;
        private TextView mLastMessage, mLastSentTime, mChatTitle;
        private CircleImageView mAvatar;
        ItemClickListener itemClickListener;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mLastMessage = row.findViewById(R.id.message_button_text);
            mAvatar = row.findViewById(R.id.user_image);
            mLastSentTime = row.findViewById(R.id.last_sent);
            mChatTitle = row.findViewById(R.id.user_name);

            mAvatar.setOnClickListener(this);
            row.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }
        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

}

