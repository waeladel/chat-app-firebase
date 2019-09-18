package com.trackaty.chat.Adapters;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Fragments.ChatsFragmentDirections;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.ChatMember;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends PagedListAdapter<Chat, ChatsAdapter.ViewHolder> {

    private final static String TAG = ChatsAdapter.class.getSimpleName();

    private FirebaseUser currentFirebaseUser ;
    private String currentUserId ;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private StorageReference mStorageRef;

    private Fragment fragment;
    public ChatsAdapter(Fragment fragment) {
        super(DIFF_CALLBACK);
        // [START create_storage_reference]
        this.fragment = fragment;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentFirebaseUser != null){
            currentUserId = currentFirebaseUser.getUid();
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
                holder.mLastMessage.setText(chat.getLastMessage());
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
                final List<ChatMember> membersList = new ArrayList<>();
                for (Object o : chat.getMembers().entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    Log.d(TAG, "mama Chats getMember = " + pair.getKey() + " = " + pair.getValue() + currentFirebaseUser.getUid());

                    if (!currentFirebaseUser.getUid().equals(pair.getKey())) {
                        ChatMember user = chat.getMembers().get(String.valueOf(pair.getKey()));
                        if (user != null) {
                            user.setKey(String.valueOf(pair.getKey()));
                            membersList.add(user);
                            Log.d(TAG, "mama Chats membersListSize=" + membersList.size());
                            Log.d(TAG, "mama Chats getMember name=" + user.getName());
                        }
                    }else{
                        // this is the current user
                        ChatMember currentMember = chat.getMembers().get(String.valueOf(pair.getKey()));
                        if (currentMember != null) {
                            currentMember.setKey(String.valueOf(pair.getKey()));
                            // Check if current user saw this chat or not
                            if(!currentMember.isSaw()){
                                // Bold text
                                Log.d(TAG, "currentMember=" + currentMember.getName() + " isSaw= "+ currentMember.isSaw() + " message= "+ chat.getLastMessage() );
                                holder.mLastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                holder.mLastMessage.setTextColor(fragment.getResources().getColor(R.color.color_on_surface_emphasis_high));
                                /*holder.mLastMessage.setTextAppearance(App.getContext(), R.style.TextAppearance_MyTheme_Headline5);
                                holder.mLastMessage.setTextColor(R.drawable.my_on_surface_emphasis_high_type);*/
                                //holder.mLastMessage.setAlpha(0.78f);
                                // item is not clicked, display colored background
                                //holder.row.setBackgroundColor(App.getContext().getResources().getColor(R.color.transparent_read_items));
                                //holder.row.setBackgroundResource(R.color.color_highlighted_item);
                            }else{
                                // Normal text
                                Log.d(TAG, "currentMember=" + currentMember.getName() + " isSaw= "+ currentMember.isSaw() + " message= "+ chat.getLastMessage() );
                                holder.mLastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                                holder.mLastMessage.setTextColor(fragment.getResources().getColor(R.color.color_on_surface_emphasis_medium));
                                //holder.mLastMessage.setTextAppearance(App.getContext(), R.style.TextAppearance_MyTheme_Body2);
                                //holder.mLastMessage.setTextColor(App.getContext().getResources().getColor(R.color.color_on_background));
                                //holder.mLastMessage.setAlpha(0.54f);
                                // If item was clicked, normal background
                                //holder.row.setBackgroundColor(App.getContext().getResources().getColor(R.color.color_background));
                                //holder.row.setBackgroundColor(android.R.attr.colorBackground);
                            }
                        }

                    }
                    //iterator.remove(); // avoids a ConcurrentModificationException
                }

                holder.setItemClickListener(new ItemClickListener(){
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if(membersList.size()== 1){
                            // it's private chat
                            //-1 entire row is clicked
                            if (view.getId() == R.id.user_image) { // only avatar is clicked
                                Log.i(TAG, "user avatar clicked= " + view.getId());
                                Log.i(TAG, "user avatar currentUserId= " + currentUserId + " userId " + membersList.get(0).getKey());
                                NavDirections ProfileDirection = ChatsFragmentDirections.actionChatsFragmentToProfileFragment(membersList.get(0).getKey());
                                Navigation.findNavController(view).navigate(ProfileDirection);
                            } else {
                                Log.i(TAG, "user row clicked= " + view.getId());
                                NavDirections MessageDirection = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(chat.getKey(), membersList.get(0).getKey(), false);
                                Navigation.findNavController(view).navigate(MessageDirection);
                            }
                        }else{
                            // it's group chat
                            //-1 entire row is clicked
                            if (view.getId() == R.id.user_image) { // only avatar is clicked
                                    /*Log.i(TAG, "user avatar clicked= "+view.getId());
                                    NavDirections ProfileDirection = MainFragmentDirections.actionMainToProfile(currentUserId, membersList.get(0).getKey(), membersList.get(0));
                                    Navigation.findNavController(view).navigate(ProfileDirection);*/
                                Log.i(TAG, "chat avatar is clicked= " + view.getId());
                                NavDirections MessageDirection = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(chat.getKey(), membersList.get(0).getKey(), true);
                                Navigation.findNavController(view).navigate(MessageDirection);
                            } else {
                                Log.i(TAG, "user row clicked= " + view.getId());
                                NavDirections MessageDirection = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(chat.getKey(), membersList.get(0).getKey(), true);
                                Navigation.findNavController(view).navigate(MessageDirection);
                            }
                        }

                    }
                });

                switch (membersList.size()){
                    case 1:// there is only one member other than current user
                        Log.d(TAG, "mama getItems membersList name= "+membersList.get(0).getName());
                        Log.d(TAG, "mama getItems membersList key= "+membersList.get(0).getKey());
                        Log.d(TAG, "mama getItems membersList avatar= "+membersList.get(0).getAvatar());
                        // names text value
                        if (null != membersList.get(0).getName()) {
                            holder.mChatTitle.setText(membersList.get(0).getName());
                        }else{
                            holder.mChatTitle.setText(null);
                        }

                        // Avatar
                        if (null != membersList.get(0).getAvatar()) {
                            holder.mAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
                            Picasso.get()
                                    .load(membersList.get(0).getAvatar())
                                    .placeholder(R.mipmap.ic_round_account_filled_72)
                                    .error(R.drawable.ic_round_broken_image_72px)
                                    .into(holder.mAvatar);
                        }else{
                            // end of user avatar
                            holder.mAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
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
                        Log.d(TAG, "mama getItems getMember= "+membersList.get(0));
                        Log.d(TAG, "mama getItems getMember= "+membersList.get(1));
                        break;
                }


            }else{
                Log.d(TAG, "mama Chats= null");
                holder.mAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
            }

        }

    }

    // CALLBACK to calculate the difference between the old item and the new item
    private static final DiffUtil.ItemCallback<Chat> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Chat>() {
                // User details may have changed if reloaded from the database,
                // but ID is fixed.
                // if the two items are the same
                @Override
                public boolean areItemsTheSame(Chat oldChat, Chat newChat) {

                    //Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + newChat);
                    //Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldChat.getLastMessage() +" new: "+ oldChat.getLastMessage());
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
                    /*Log.d(TAG, " messages query DIFF_CALLBACK areContentsTheSame old name: " + oldChat.getLastMessage() + " new name: "+newChat.getLastMessage()+ " value= "+(oldChat.getLastMessage().equals(newChat.getLastMessage())
                            && (oldChat.getLastSentLong() == newChat.getLastSentLong())));*/

                    // compare old and new chat's sent time and last messages
                    /*return (oldChat.getLastMessage().equals(newChat.getLastMessage())
                            && (oldChat.getLastSentLong() == newChat.getLastSentLong()));*/
                    //Log.d(TAG, "messages query DIFF_CALLBACK areContentsTheSame old name: " + oldChat.getLastMessage() + " new name: "+newChat.getLastMessage()+ " value= "+(oldChat.equals(newChat)));
                    return oldChat.equals(newChat);
                    //return oldChat.getLastMessage().equals(newChat.getLastMessage());
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
            mLastMessage = row.findViewById(R.id.chat_text);
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

