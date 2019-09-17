package com.trackaty.chat.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.App;
import com.trackaty.chat.fragments.NotificationsFragmentDirections;
import com.trackaty.chat.interfaces.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.DatabaseNotification;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class NotificationsAdapter extends PagedListAdapter<DatabaseNotification, NotificationsAdapter.ViewHolder> {

    private final static String TAG = NotificationsAdapter.class.getSimpleName();

    private FirebaseUser firebaseCurrentUser ;
    private String currentUserId ;

    public Context context;

    private static final String AVATAR_THUMBNAIL_NAME = "avatar.jpg";
    private static final String COVER_THUMBNAIL_NAME = "cover.jpg";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private static final String NOTIFICATION_TYPE_PICK_UP = "Pickup";
    private static final String NOTIFICATION_TYPE_MESSAGE = "Message";
    private static final String NOTIFICATION_TYPE_LIKE = "Like"; // if other user liked me. I did't liked him
    private static final String NOTIFICATION_TYPE_LIKE_BACK = "LikeBack"; // if other user liked me after i liked him
    private static final String NOTIFICATION_TYPE_REQUESTS_SENT = "RequestSent";
    private static final String NOTIFICATION_TYPE_REQUESTS_APPROVED = "RequestApproved";

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mNotificationsRef;

    private Fragment fragment;

    public NotificationsAdapter(Fragment fragment) {
        super(DIFF_CALLBACK);

        this.fragment = fragment;

        firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseCurrentUser != null){
            currentUserId = firebaseCurrentUser.getUid();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();
            // use received chatKey to create a database ref
            mNotificationsRef = mDatabaseRef.child("notifications").child("alerts").child(currentUserId);
        }



    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item , parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final NotificationsAdapter.ViewHolder holder, final int position) {

        final DatabaseNotification notification = getItem(position);
        if (notification != null) {
            // Notification body value
            if (null != notification.getType()) {
                switch (notification.getType()){
                    case NOTIFICATION_TYPE_LIKE:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_like_body, name));
                            Log.i(TAG, "getSenderName= "+notification.getSenderName());
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));
                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_like_body);
                        }
                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_favorite_24);
                        break;
                    case NOTIFICATION_TYPE_LIKE_BACK:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_like_back_body, name));
                            Log.i(TAG, "getSenderName= "+notification.getSenderName());
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));
                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_like_body);
                        }
                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_favorite_24);
                        break;
                    case NOTIFICATION_TYPE_PICK_UP:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_pick_up_body, name));
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));
                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_pick_up_body);
                        }
                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_chat_24);
                        break;
                    case NOTIFICATION_TYPE_MESSAGE:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_message_body, name));
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));

                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_message_body);
                        }
                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_chat_24);
                        break;
                    case NOTIFICATION_TYPE_REQUESTS_SENT:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_request_sent_body, name));
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));

                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_request_sent_body);
                        }

                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_visibility_24);
                        break;
                    case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                        if(null != notification.getSenderName()){
                            String name = notification.getSenderName();
                            String wholeText = (App.getContext().getString(R.string.notification_request_approved_body, name));
                            setTextWithSpan(holder.mNotificationBody, wholeText, name, new android.text.style.StyleSpan(BOLD));
                        }else{
                            holder.mNotificationBody.setText(R.string.notification_default_request_approved_body);
                        }
                        // switch icons according to notification type
                        holder.mIcon.setImageResource(R.drawable.ic_circle_visibility_24);
                        break;
                }
            }else{
                holder.mNotificationBody.setText(null);

                // Use default notification icon
                holder.mIcon.setImageResource(R.drawable.ic_circle_notification_24);
            }

            // LastSentTime text value
            if (null != notification.getSent()) {
                long now = System.currentTimeMillis();
                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(notification.getSentLong(), now, DateUtils.MINUTE_IN_MILLIS);
                holder.mSentTime.setText(ago);
            }else{
                holder.mSentTime.setText(null);
            }

            // Display sender avatar
            if (null != notification.getSenderAvatar()) {
                // [START create_storage_reference]
                //ReceivedHolder.mAvatar.setImageResource(R.drawable.ic_user_account_grey_white);
                //mStorageRef.child("images/"+message.getSenderId()+"/"+ AVATAR_THUMBNAIL_NAME).getFile()
                String avatarUrl = notification.getSenderAvatar();
                Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.mipmap.ic_round_account_filled_72)
                        .error(R.drawable.ic_round_broken_image_72px)
                        .into(holder.mAvatar);
            }else{
                // Handle if getSenderId() is null
                holder.mAvatar.setImageResource(R.drawable.ic_round_account_filled_72);
            }

            // background color
            if (notification.isClicked()) {
                // If item was clicked, normal background
                holder.row.setBackgroundColor(android.R.attr.colorBackground);
            }else{
                // item is not clicked, display colored background
                //holder.row.setBackgroundColor(App.getContext().getResources().getColor(R.color.transparent_read_items));
                holder.row.setBackgroundResource(R.color.color_highlighted_item);
            }

         }



    }

    private void setTextWithSpan(TextView textView, String wholeText, String spanText, StyleSpan style) {

        SpannableStringBuilder sb = new SpannableStringBuilder(wholeText);
        int start = wholeText.indexOf(spanText);
        int end = start + spanText.length();
        sb.setSpan(new StyleSpan(BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = fragment.getResources().getColor(R.color.color_on_surface_emphasis_high);
        sb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sb);
    }

    // CALLBACK to calculate the difference between the old item and the new item
    private static final DiffUtil.ItemCallback<DatabaseNotification> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DatabaseNotification>() {
                // User details may have changed if reloaded from the database,
                // but ID is fixed.
                // if the two items are the same
                @Override
                public boolean areItemsTheSame(DatabaseNotification oldItem, DatabaseNotification newItem) {

                    //Log.d(TAG, " DIFF_CALLBACK areItemsTheSame " + newChat);
                    Log.d(TAG, " DIFF_CALLBACK areItemsTheSame keys= old: " + oldItem.getSenderName() +" new: "+ newItem.getSenderName()+ " value= "+oldItem.getKey().equals(newItem.getKey()));
                    return oldItem.getKey().equals(newItem.getKey());
                    //return oldChat.getLastSentLong() == (newChat.getLastSentLong());
                    //return true;
                }

                // if the content of two items is the same
                @Override
                public boolean areContentsTheSame(DatabaseNotification oldItem, DatabaseNotification newItem) {

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
                    Log.d(TAG, "notifications query DIFF_CALLBACK areContentsTheSame old name: " + oldItem.getSenderName() + " new name: "+newItem.getSenderName()+ " value= "+(oldItem.equals(newItem)));
                    return oldItem.equals(newItem);
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
        private TextView mNotificationBody, mSentTime;
        private CircleImageView mAvatar;
        private ImageView mIcon;
        ItemClickListener itemClickListener;


        public ViewHolder(View itemView) {
            super(itemView);
            //itemView = row;

            row = itemView;
            mNotificationBody = row.findViewById(R.id.notification_text);
            mAvatar = row.findViewById(R.id.user_image);
            mIcon = row.findViewById(R.id.notification_type);
            mSentTime = row.findViewById(R.id.sent_time);

            row.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(itemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                itemClickListener.onClick(view, getAdapterPosition(), false);
            }

            if(getAdapterPosition() != RecyclerView.NO_POSITION){
                Log.i(TAG, "user row clicked= "+view.getId()+ " Position= "+ getAdapterPosition());
                // get clicked notification
                DatabaseNotification notification = getItem(getAdapterPosition());
                if(notification != null){
                    notification.setClicked(true); // set clicked notification to true
                    mNotificationsRef.child(notification.getKey()).child("clicked").setValue(true);// update clicked field on database

                    // to open sender profile when notification type is not a message or pick-up
                    NavDirections ProfileDirection = NotificationsFragmentDirections.actionNotificationsFragToProfileFrag(notification.getSenderId());
                    // to open chat room when notification type is a message or pick-up
                    NavDirections MessageDirection = NotificationsFragmentDirections.actionNotificationsFragToMessagesFrag(notification.getChatId(), notification.getSenderId(), false);

                    if (null != notification.getType()) {
                        switch (notification.getType()){
                            case NOTIFICATION_TYPE_LIKE:
                                //TODO Go to likes fragment
                                Navigation.findNavController(view).navigate(ProfileDirection);
                                break;
                            case NOTIFICATION_TYPE_LIKE_BACK:
                                //TODO Go to likes fragment
                                Navigation.findNavController(view).navigate(ProfileDirection);
                                break;
                            case NOTIFICATION_TYPE_PICK_UP:
                                Navigation.findNavController(view).navigate(MessageDirection);
                                break;
                            case NOTIFICATION_TYPE_MESSAGE:
                                Navigation.findNavController(view).navigate(MessageDirection);
                                break;
                            case NOTIFICATION_TYPE_REQUESTS_SENT:
                                Navigation.findNavController(view).navigate(ProfileDirection);

                                break;
                            case NOTIFICATION_TYPE_REQUESTS_APPROVED:
                                Navigation.findNavController(view).navigate(ProfileDirection);

                                break;
                            default:
                                Navigation.findNavController(view).navigate(ProfileDirection);
                                break;
                        }

                    }
                }



            }

        }

        // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

}

