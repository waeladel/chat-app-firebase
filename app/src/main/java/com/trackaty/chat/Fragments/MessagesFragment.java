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

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Adapters.MessagesAdapter;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.ViewModels.MessagesViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

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



    private MessagesViewModel mMessagesViewModel;
    private MainActivityViewModel mMainViewModel;
    private String mCurrentUserId, mChatUserId, mChatId;
    private User mChatUser, mCurrentUser ;
    private FirebaseUser mFirebaseCurrentUser;
    private Boolean isGroup;

    private Context mActivityContext;
    private Activity activity;

    public TextView mUserName, mLastSeen;
    private CircleImageView mUserPhoto;
    private EditText mMessage;
    private ImageButton mSendButton;

    private Timer mTimer;
    private CountDownTimer mCountDownTimer;
    private Boolean isListEnded;// = false;


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

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;

        //mCurrentUserId = App.getCurrentUserId();
        if(getArguments() != null) {
            //mCurrentUserId = MessagesFragmentArgs.fromBundle(getArguments()).getCurrentUserId();//logged in user
            mChatUserId = MessagesFragmentArgs.fromBundle(getArguments()).getChatUserId();// any user
            //mCurrentUserId = MessagesFragmentArgs.fromBundle(getArguments()).getCurrentUserId();
            isGroup = MessagesFragmentArgs.fromBundle(getArguments()).getIsGroup();
            if(null != MessagesFragmentArgs.fromBundle(getArguments()).getChatId()){
                mChatId = MessagesFragmentArgs.fromBundle(getArguments()).getChatId();
            }else{
                // Chat ID is not passed from MainFragment, we need to create
                mChatId = getJoinedKeys(mCurrentUserId , mChatUserId);
            }
            Log.d(TAG, "currentUserId mCurrentUserId= " + mCurrentUserId + " mChatUserId= " + mChatUserId+ " mChatId= "+ mChatId);
        }

        mMessage = (EditText) fragView.findViewById(R.id.message_button_text);
        mSendButton = (ImageButton) fragView.findViewById(R.id.send_button);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mChatsRef = mDatabaseRef.child("chats");
        mMessagesRef = mDatabaseRef.child("messages");

        //isListEnded = true;

        // prepare the Adapter
        mMessagesArrayList = new ArrayList<>();
        mMessagesAdapter = new MessagesAdapter();

        // Initiate the RecyclerView
        mMessagesRecycler = (RecyclerView) fragView.findViewById(R.id.messages_recycler);
        mMessagesRecycler.setHasFixedSize(true);
        /* setStackFromEnd is usefuall to start stacking recycler from it's last
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mLinearLayoutManager.setStackFromEnd(true);*/
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mMessagesRecycler.setLayoutManager(mLinearLayoutManager);

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mMessagesRecycler.setAdapter(mMessagesAdapter);

        /*// Push up content when clicking in edit text
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/

        // Listen for scroll events
        mMessagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //Log.d(TAG, "onScrollStateChanged newState= "+newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d(TAG, "onScrolled dx= "+dx +" dy= "+dy);
                int visibleItemCount = mMessagesRecycler.getChildCount(); // items are shown on screen right now
                //int totalItemCount = mLinearLayoutManager.getItemCount();
                int totalItemCount = mMessagesAdapter.getItemCount(); // total items count from the adapter
                int pastVisibleItems = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); // the position of last displayed item
                //int pastVisibleItems = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+pastVisibleItems);

                // The position of last displayed item = total items, witch means we are at the bottom
                if(pastVisibleItems >= (totalItemCount-1) ){
                    // End of the list is here.
                    Log.i(TAG, "End of list");
                    isListEnded = true;
                }else{
                    isListEnded = false;
                }

                /*if(pastVisibleItems+visibleItemCount >= (totalItemCount-1)){
                    // End of the list is here.
                    Log.i(TAG, "End of list");
                    isListEnded = true;
                }else{
                    isListEnded = false;
                }*/

            }
        });


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
                        if(userSnapshot.hasChild(mChatUserId)){
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
        mChatsRef.orderByChild(mCurrentUserId).equalTo(mChatUserId).addValueEventListener(new ValueEventListener() {
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
                Log.d(TAG, "getJoinedKeys ="+getJoinedKeys(mCurrentUserId , mChatUserId));
                if(!TextUtils.isEmpty(messageText)){
                    // clear text before sending the message successfully for offline capabilities
                    mMessage.setText(null);
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

        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
            // Push up content when clicking in edit text
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            Log.d(TAG, "mCountDownTimer canceled");
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


            mTimer = new Timer();
            // custom action bar items to add receiver's avatar and name //
            mUserName = (TextView) actionBarView.findViewById(R.id.user_name);
            mLastSeen = (TextView) actionBarView.findViewById(R.id.last_seen);
            mUserPhoto = (CircleImageView) actionBarView.findViewById(R.id.user_image);

            // get Current User Id and object from main ViewModel

            // update the CurrentUserId whenever it changes due to log out
            mMainViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            /*mMainViewModel.getCurrentUserId().observe(this, new Observer<String>() {
                @Override
                public void onChanged(final String userId) {
                    Log.d(TAG, "onChanged user userId= "+userId);
                    mCurrentUserId = userId;
                    // join mCurrentUserId and mChatUserId to generate a chat Key
                    if(mChatId == null){
                        // Chat ID is not passed from MainFragment, we need to create
                        mChatId = getJoinedKeys(mCurrentUserId , mChatUserId);
                    }
                    Log.d(TAG, "mCurrentUserId= " + mCurrentUserId + " mChatUserId= " + mChatUserId);
                }
            });*/ // End init  mMessagesViewModel getChatUser//




            //mMessagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);

            // start init  mMessagesViewModel here after mCurrentUserId is received//
            // extend mMessagesViewModel to pass Chat Key value //
            mMessagesViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T)new MessagesViewModel (mChatId);
                }
            }).get(MessagesViewModel.class);


            mMessagesViewModel.getMessagesList().observe(MessagesFragment.this, new Observer<PagedList<Message>>() {
                @Override
                public void onChanged(@Nullable final PagedList<Message> items) {
                    System.out.println("mama onChanged");
                    if (items != null ){
                        // your code here
                        // Create new Thread to loop until items.size() is greater than 0
                        new Thread(new Runnable() {
                            int sleepCounter = 0;
                            @Override
                            public void run() {
                                try {
                                    while(items.size()==0) {
                                        //Keep looping as long as items size is 0
                                        Thread.sleep(20);
                                        Log.d(TAG, "sleep 1000. size= "+items.size()+" sleepCounter="+sleepCounter++);
                                        if(sleepCounter == 1000){
                                            break;
                                        }
                                        //handler.post(this);
                                    }
                                    //Now items size is greater than 0, let's submit the List
                                    Log.d(TAG, "after  sleep finished. size= "+items.size());
                                    if(items.size() == 0 && sleepCounter == 1000){
                                        // If we submit List after loop is finish with 0 results
                                        // we may erase another results submitted via newer thread
                                        Log.d(TAG, "Loop finished with 0 items. Don't submitList");
                                    }else{
                                        Log.d(TAG, "submitList");
                                        mMessagesAdapter.submitList(items);
                                    }

                                    // Scroll to last item
                                    Log.d(TAG, "isListEnded = "+isListEnded);

                                    // Only scroll to bottom if user is not reading messages above
                                    if(null == isListEnded || isListEnded){
                                        if(items.size()>0 && mMessagesAdapter.getItemCount()>0 ){// stop scroll to bottom if there are no items
                                            //mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                                            Log.d(TAG, "adapter getItemCount= "+mMessagesAdapter.getItemCount());
                                            mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount()-1);
                                        }
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();

                        /*Thread thread = new Thread() {
                            int sleepCounter = 0;
                            @Override
                            public void run() {
                                try {
                                    while(items.size()==0) {
                                        //Keep looping as long as items size is 0
                                        sleep(20);
                                        Log.d(TAG, "sleep 1000. size= "+items.size()+" sleepCounter="+sleepCounter++);
                                        if(sleepCounter == 1000){
                                            break;
                                        }
                                        //handler.post(this);
                                    }
                                    //Now items size is greater than 0, let's submit the List
                                    Log.d(TAG, "after  sleep finished. size= "+items.size());
                                    mMessagesAdapter.submitList(items);

                                    // Scroll to last item
                                    Log.d(TAG, "isListEnded = "+isListEnded);

                                    // Only scroll to bottom if user is not reading messages above
                                    if(null == isListEnded || isListEnded){
                                        if(items.size()>0){// stop scroll to bottom if there are no items
                                        //mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                                        Log.d(TAG, "adapter getItemCount= "+mMessagesAdapter.getItemCount());
                                        mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount()-1);
                                        }
                                    }


                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();*/

                    }
                }
            });// End init  mMessagesViewModel itemPagedList here after mCurrentUserId is received//

            // get Chat User
            if(!isGroup){
                mMessagesViewModel.getChatUser(mChatUserId).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Log.d(TAG, "mMessagesViewModel onChanged chatUser userId name= "+user.getName());
                        mChatUser = user;
                        // display ChatUser name
                        if(null != mChatUser.getName()){
                            mUserName.setText(getFirstWord(mChatUser.getName()));
                        }

                        // display last online
                        if(null != mChatUser.getLastOnline()){

                            Log.d(TAG, "getLastOnline()= "+mChatUser.getLastOnline());

                            if(mChatUser.getLastOnline() == 0){
                                //user is active now
                                Log.d(TAG, "LastOnline() == 0");
                                mLastSeen.setText(R.string.user_active_now);
                            }else{
                                // Display last online
                                //Calendar c = Calendar.getInstance();
                                //c.setTimeInMillis(mChatUser.getLastOnline());


                                /*mTimer.schedule( new TimerTask() {
                                    public void run() {
                                        // do your work
                                        UpdateTimeAgo(mChatUser.getLastOnline());
                                        *//*mLastSeen.setText(ago);
                                        Log.d(TAG, "mTimer = "+ago);*//*
                                        //new UpdateTimeAgo().execute(mChatUser.getLastOnline());
                                    }
                                }, 0, 5 *1000);*/

                                /*mMessagesViewModel.getLastOnlineAgo(mChatUser.getLastOnline()).observe(MessagesFragment.this, new Observer<CharSequence>() {
                                    @Override
                                    public void onChanged(CharSequence charSequence) {
                                        Log.d(TAG, "onChanged Time Ago = "+charSequence);
                                    }
                                });*/

                                mCountDownTimer = new CountDownTimer(10*60*1000, 60*1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        long now = System.currentTimeMillis();
                                        Log.d(TAG, "now = "+now + " getLastOnline= "+mChatUser.getLastOnline());
                                        CharSequence ago =
                                                DateUtils.getRelativeTimeSpanString(mChatUser.getLastOnline(), now, DateUtils.MINUTE_IN_MILLIS);
                                                mLastSeen.setText(getString(R.string.user_active_ago, ago));
                                    }

                                    @Override
                                    public void onFinish() {
                                        //restart countDownTimer again
                                        Log.d(TAG, "mCountDownTimer onFinish. We will restart it");
                                        mCountDownTimer.start();
                                    }
                                }.start();
                            }

                        }
                        // Get user values
                        if (null != mChatUser.getAvatar()) {
                            Picasso.get()
                                    .load(mChatUser.getAvatar())
                                    .placeholder(R.drawable.ic_user_account_grey_white)
                                    .error(R.drawable.ic_broken_image)
                                    .into(mUserPhoto);
                        }

                    }
                });
            }

            mMainViewModel.getCurrentUser().observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    Log.d(TAG, "onChanged CurrentUser userId name= "+user.getName());
                    mCurrentUser = user;
                }
            });

            // Open user profile with custom actionBar is clicked //
            actionBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.onTextViewNameClick(view, getAdapterPosition());
                    Log.i(TAG, "user avatar or name clicked");
                    NavDirections ProfileDirection = MessagesFragmentDirections.actionMessagesFragToProfileFrag(mChatUser.getKey());

                    //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

                    //check if we are on Main Fragment not on complete Profile already
                    //Navigation.findNavController(view).navigate(ProfileDirection);

                    Navigation.findNavController(activity, R.id.host_fragment).navigate(ProfileDirection);
                }
            });

    }

}

    private void UpdateTimeAgo(Long lastOnline) {
        long now = System.currentTimeMillis();
        Log.d(TAG, "now = "+now);

        CharSequence ago =
                DateUtils.getRelativeTimeSpanString(lastOnline, now, DateUtils.MINUTE_IN_MILLIS);

        mLastSeen.setText(ago);

    }

    private void sendMessage(String mChatId, String messageText) {

        String  messageKey = mMessagesRef.push().getKey();
        Message message = new Message(messageText, mCurrentUserId, mCurrentUser.getName(),mCurrentUser.getAvatar(), false);
        Map<String, Object> messageValues = message.toMap();

        /*// Create members array list, it's better to loop throw  selected members
        ArrayList <String> members = new ArrayList<>();
        members.add(mCurrentUserId);
        members.add(mChatUserId);*/

        Log.d(TAG, "sendMessage CurrentUserId= "+mCurrentUserId+ " userId= "+ mChatUserId + " chatUser= "+ mChatUser);


        // Create members Hash list, it's better to loop throw  selected members
        Map<String, User> members = new HashMap<>();
        members.put(mCurrentUserId, mCurrentUser);
        members.put(mChatUserId, mChatUser);

        // Create chat map
        Chat chat = new Chat(messageText, members);
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
        childUpdates.put("/userChats/" + mChatUserId + "/" + mChatId, chatValues);

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "send message onSuccess");
                isListEnded = true; // To scroll to bottom when user send new message
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
        chatValues.put(mChatUserId, true);
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


