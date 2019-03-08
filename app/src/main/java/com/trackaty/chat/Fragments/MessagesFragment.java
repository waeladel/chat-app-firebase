package com.trackaty.chat.Fragments;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Adapters.MessagesAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.MessagesViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.trackaty.chat.Utils.DatabaseKeys.getJoinedKeys;
import static com.trackaty.chat.Utils.StringUtils.getFirstWord;

public class MessagesFragment extends Fragment {

    private final static String TAG = MessagesFragment.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;
    private DatabaseReference mMessagesRef;

    private RecyclerView mMessagesRecycler;
    private ArrayList<Message> mMessagesArrayList;
    private MessagesAdapter mMessagesAdapter;



    private MessagesViewModel mViewModel;
    private String mCurrentUserId, mUserId , mChatId;
    private User mUser;

    private Context mActivityContext;
    private Activity activity;

    private TextView mUserName, mLastSeen;
    private CircleImageView mUserPhoto;
    private EditText mMessage;
    private ImageButton mSendButton;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.messages_fragment, container, false);
        if(getArguments() != null) {
            mCurrentUserId = MessagesFragmentArgs.fromBundle(getArguments()).getCurrentUserId();//logged in user
            mUser = MessagesFragmentArgs.fromBundle(getArguments()).getUser();// any user
            mUserId = mUser.getKey();

            // join mCurrentUserId and mUserId to generate a chat Key
            mChatId = getJoinedKeys(mCurrentUserId , mUserId);
            Log.d(TAG, "mCurrentUserId= " + mCurrentUserId + " mUserId= " + mUserId + " name= " + mUser.getName() + "pickups=" + mUser.getPickupCounter());
        }

        mMessage = (EditText) fragView.findViewById(R.id.message_text);
        mSendButton = (ImageButton) fragView.findViewById(R.id.send_button);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mChatsRef = mDatabaseRef.child("chats");
        mMessagesRef = mDatabaseRef.child("messages");


        // prepare the Adapter
        mMessagesArrayList = new ArrayList<>();
        mMessagesAdapter = new MessagesAdapter();

        // Initiate the RecyclerView
        mMessagesRecycler = (RecyclerView) fragView.findViewById(R.id.messages_recycler);
        mMessagesRecycler.setHasFixedSize(true);
        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mMessagesRecycler.setAdapter(mMessagesAdapter);


       /* /// a query to get all chats for current user then find if it has the key of the receiver//////
       mChatsRef.orderByChild(mCurrentUserId).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                // start with null chat id, to get a new one if chat is exist
                mChatId = null;
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                        Log.d(TAG, "dataSnapshot. getSnapshotKey= " +  userSnapshot.getKey());
                        //userSnapshot.getValue().String.class;
                        if(userSnapshot.hasChild(mUserId)){
                            mChatId =  userSnapshot.getKey();
                            Log.d(TAG, "dataSnapshot hasChild= " +  userSnapshot.getKey()+ " count "+userSnapshot.getChildrenCount());
                            // chat room already exist. fetch data
                            //getMessages(userSnapshot.getKey());
                        }
                    }
                    if(mChatId == null){
                        // no chat room exists between these two users. create new chat room
                        Log.d(TAG, "dataSnapshot no hasChild: no chat room exists between these two users");
                        //createChat();
                    }

                } else {
                    Log.w(TAG, "no chats exist");
                    // no chat room exist. create new chat room
                    mChatId = null;
                    //createChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });*/


        /* //// a better query, no need to get all chats for current user, just chat key for his room with the receiver
        mChatsRef.orderByChild(mCurrentUserId).equalTo(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // [START_EXCLUDE]
                // start with null chat id, to get a new one if chat is exist
                mChatId = null;
                if (dataSnapshot.exists()) {
                    // loop throw users value
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                            Log.d(TAG, "dataSnapshot hasChild= " +  userSnapshot.getKey()+ " count "+userSnapshot.getChildrenCount());
                            mChatId =  userSnapshot.getKey();
                            // chat room already exist. fetch data
                            //getMessages(userSnapshot.getKey());
                    }
                    if(mChatId == null){
                        // no chat room exists between these two users. create new chat room
                        Log.d(TAG, "dataSnapshot no hasChild: no chat room exists between these two users");
                        //createChat();
                    }

                } else {
                    Log.w(TAG, "no chats exist");
                    // no chat room exist. create new chat room
                    mChatId = null;
                    //createChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });*/

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "mSendButton is clicked ");
                String messageText = mMessage.getText().toString().trim();
                Log.d(TAG, "getJoinedKeys ="+getJoinedKeys(mCurrentUserId , mUserId));
                if(!TextUtils.isEmpty(messageText)){
                    sendMessage(mChatId, messageText);
                }
                /*if(!TextUtils.isEmpty(messageText)){
                    if(mChatId != null){
                        //send message to the existing chat room
                        Log.d(TAG, "sendMessage. chatId= "+mChatId +" message= "+messageText);
                        sendMessage(mChatId, messageText);
                    }else{
                        //create chat room then send the message
                        Log.d(TAG, "createChat. message= "+messageText);
                        createChat(messageText);
                    }

                }*/
            }
        });

        return fragView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;

        if (context instanceof Activity){// check if context is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(((MainActivity)getActivity())!= null){
            ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
            actionbar.setTitle(null);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayShowCustomEnabled(true);

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = inflater.inflate(R.layout.messages_toolbar, null);
            actionbar.setCustomView(actionBarView);

            // custom action bar items to add receiver's avatar and name //
            mUserName = (TextView) actionBarView.findViewById(R.id.user_name);
            mLastSeen = (TextView) actionBarView.findViewById(R.id.last_seen);
            mUserPhoto = (CircleImageView) actionBarView.findViewById(R.id.user_image);

            mUserName.setText(getFirstWord(mUser.getName()));
            //ToDo: Display last online not user's name
            mLastSeen.setText(getFirstWord(mUser.getName()));

            // Get user values
            if (null != mUser.getAvatar()) {
                Picasso.get()
                        .load(mUser.getAvatar())
                        .placeholder(R.drawable.ic_user_account_grey_white)
                        .error(R.drawable.ic_broken_image)
                        .into(mUserPhoto);
            }

            // Open user profile with custom actionBar is clicked //
            actionBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.onTextViewNameClick(view, getAdapterPosition());
                    Log.i(TAG, "user avatar or name clicked");
                    NavDirections ProfileDirection = MessagesFragmentDirections.actionMessagesFragToProfileFrag(mCurrentUserId, mUser.getKey(), mUser);

                    //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

                    //check if we are on Main Fragment not on complete Profile already
                    //Navigation.findNavController(view).navigate(ProfileDirection);

                    Navigation.findNavController(activity, R.id.host_fragment).navigate(ProfileDirection);
                }
            });

        //mViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        // extend ViewModel to pass Chat Key value //
        mViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T)new MessagesViewModel (mChatId);
                }
            }).get(MessagesViewModel.class);

        mViewModel.itemPagedList.observe(this, new Observer<PagedList<Message>>() {
                @Override
                public void onChanged(@Nullable final PagedList<Message> items) {
                    System.out.println("mama onChanged");
                    if (items != null ){


                        //delay submitList till items size is not 0
                       new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        // your code here
                                        Log.d(TAG, "mama messages submitList size" +  items.size());
                                        mMessagesAdapter.submitList(items);
                                        // Scroll to last item
                                        mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                                        // Scroll to last item on the list
                                        //int position = mMessagesRecycler.getAdapter().getItemCount()-1;
                                        /*int position = mMessagesAdapter.getItemCount()-1;
                                        Log.d(TAG, "mama last position= " +  position);

                                        if(position >= 0 ){
                                            mMessagesRecycler.smoothScrollToPosition(position);
                                            //mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                                        }*/
                                    }
                                },
                                5000
                        );

                    }
                }
            });
    }

}

    private void sendMessage(String mChatId, String messageText) {

        String  messageKey = mMessagesRef.push().getKey();
        Message message = new Message(messageText, mCurrentUserId, "wael", false , ServerValue.TIMESTAMP);
        Map<String, Object> messageValues = message.toMap();

        Chat chat = new Chat(mCurrentUserId, mUserId, messageText );
        Map<String, Object> chatValues = chat.toMap();
        /*Map<String, Object> chatValues = new HashMap<>();
        chatValues.put("lastMessage", messageText);
        chatValues.put("lastSent", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + mChatId + "/" + messageKey, messageValues);
        childUpdates.put("/chats/" + mChatId + "/lastMessage/", messageText);
        childUpdates.put("/chats/" + mChatId + "/lastSent/", ServerValue.TIMESTAMP);*/

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + mChatId + "/" + messageKey, messageValues);
        childUpdates.put("/chats/" + mChatId ,chatValues);

        // only if Lookup is needed
        childUpdates.put("/userChats/" + mCurrentUserId + "/" + mChatId, chatValues);
        childUpdates.put("/userChats/" + mUserId + "/" + mChatId, chatValues);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                mMessage.setText(null);
                Log.i(TAG, "send message onSuccess");
                // ...
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), R.string.send_message_error,
                                Toast.LENGTH_LONG).show();
                        // ...
                    }
                });

    }

   /* /////// Not needed method. It was used to create new chat room if not exist ///////
    private void createChat(String messageText) {

        Message message = new Message(messageText, mCurrentUserId, "wael", false );
        Map<String, Object> messageValues = message.toMap();

        String chatKey = mChatsRef.push().getKey();
        String messageKey = mMessagesRef.push().getKey();


        Map<String, Object> chatValues = new HashMap<>();
        chatValues.put(mCurrentUserId, true);
        chatValues.put(mUserId, true);
        chatValues.put("lastMessage", messageText);
        chatValues.put("lastSent", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/messages/" + chatKey + "/" + messageKey, messageValues);
        childUpdates.put("/chats/" + chatKey , chatValues);


        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                mMessage.setText(null);
                Log.i(TAG, "send message onSuccess");
                // ...
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(getActivity(), R.string.send_message_error,
                                Toast.LENGTH_LONG).show();
                        // ...
                    }
                });
    }*/

}
