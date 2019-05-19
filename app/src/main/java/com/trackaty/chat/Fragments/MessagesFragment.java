package com.trackaty.chat.Fragments;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentManager;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.trackaty.chat.Adapters.MessagesAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.MainActivityViewModel;
import com.trackaty.chat.ViewModels.MessagesViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Chat;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.trackaty.chat.Utils.DatabaseKeys.getJoinedKeys;
import static com.trackaty.chat.Utils.StringUtils.getFirstWord;

public class MessagesFragment extends Fragment implements ItemClickListener {

    private final static String TAG = MessagesFragment.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;
    private DatabaseReference mMessagesRef;

    private RecyclerView mMessagesRecycler;
    private ArrayList<Message> mMessagesArrayList;
    private MessagesAdapter mMessagesAdapter;
    private  LinearLayoutManager mLinearLayoutManager;

    private FragmentManager fragmentManager;// = getFragmentManager();

    private  static final String CHAT_INACTIVE_FRAGMENT = "InactiveFragment";
    private  static final String ACTIVATE_CHAT_FRAGMENT = "ActivateFragment";

    private MessagesViewModel mMessagesViewModel;
    private MainActivityViewModel mMainViewModel;
    private String mCurrentUserId, mChatUserId, mChatId;
    private User mChatUser, mCurrentUser ;
    private FirebaseUser mFirebaseCurrentUser;
    private Boolean isGroup;
    private Boolean isSender;
    private Boolean isHitBottom;// = false;

    private Context mActivityContext;
    private Activity activity;

    public TextView mUserName, mLastSeen , mRemainingTimeText;
    private CircleImageView mUserPhoto;
    private EditText mMessage;
    private ImageButton mSendButton;
    private FloatingActionButton mScrollFab;


    //private Timer mTimer;
    private static CountDownTimer mAgoTimer, mRemainingTimer;
    //private Boolean isHitBottom ;// = false;
    private long mTimeLiftInMillis;
    private Long mActiveEndTime, mLastOnlineEndTime;

    private Chat mChat;

    private int bottomVisibleItemCount;
    private int scrollDirectionY;

    private PagedList<Message> mItems;

    private static final int REACHED_THE_TOP = 2;
    private static final int SCROLLING_UP = 1;
    private static final int SCROLLING_DOWN = -1;
    private static final int REACHED_THE_BOTTOM = -2;
    private int mScrollDirection;

    private static final String IS_HII_BOTTOM = "Hit_Bottom";


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
        mScrollFab = (FloatingActionButton) fragView.findViewById(R.id.scroll_fab);
        mRemainingTimeText = (TextView) fragView.findViewById(R.id.remaining_time);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mChatsRef = mDatabaseRef.child("chats");
        mMessagesRef = mDatabaseRef.child("messages");

        //isHitBottom = true;

        // prepare the Adapter
        mMessagesArrayList = new ArrayList<>();
        mMessagesAdapter = new MessagesAdapter(mChatId); // Pass chat id because it's needed to update message revelation

        // Initiate the RecyclerView
        mMessagesRecycler = (RecyclerView) fragView.findViewById(R.id.messages_recycler);
        mMessagesRecycler.setHasFixedSize(true);
        /* setStackFromEnd is usefuall to start stacking recycler from it's last
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mLinearLayoutManager.setStackFromEnd(true);*/
        mLinearLayoutManager = new LinearLayoutManager(mActivityContext);
        mMessagesRecycler.setLayoutManager(mLinearLayoutManager);

        //viewModel.usersList.observe(this, mUsersAdapter::submitList);

        //observe when a change happen to usersList live data
        mMessagesRecycler.setAdapter(mMessagesAdapter);

        // Push up content when clicking in edit text
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /*activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);*/

        // Listen for scroll events
        mMessagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged newState= "+newState);
                // set scrolling direction. it's needed for the initialkey
                /*if(scrollDirectionY != 0){
                    mMessagesViewModel.setScrollDirection(scrollDirectionY);
                }*/
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled dx= "+dx +" dy= "+dy);
                // set scrolling direction. it's needed for the initialkey
                //scrollDirectionY = dy;
                //mMessagesViewModel.setScrollDirection(dy);

                int visibleItemCount = mMessagesRecycler.getChildCount(); // items are shown on screen right now
                int firstCompletelyVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition(); // the position of first displayed item
                //int totalItemCount = mLinearLayoutManager.getItemCount();
                int totalItemCount = mMessagesAdapter.getItemCount(); // total items count from the adapter
                int lastCompletelyVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); // the position of last displayed item
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition(); // the position of last displayed item

                //int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition(); // the position of last displayed item

                //int pastVisibleItems = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                Log.d(TAG, "visibleItemCount = "+visibleItemCount +" totalItemCount= "+totalItemCount+" lastVisibleItem "+lastCompletelyVisibleItem);

                if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                //if(lastCompletelyVisibleItem >= (totalItemCount-5)){
                    // The position of last displayed item = total items, witch means we are at the bottom
                    mScrollDirection = REACHED_THE_BOTTOM;
                    Log.i(TAG, "List reached the bottom");
                //}else if(lastCompletelyVisibleItem <= visibleItemCount){
                }else if(firstCompletelyVisibleItem <= 4){
                    // The position of last displayed item is less than visibleItemCount, witch means we are at the top
                    mScrollDirection = REACHED_THE_TOP;
                    Log.i(TAG, "List reached the top");
                }else{
                    if(dy < 0 ){
                        // dy is negative number,  scrolling up
                        Log.i(TAG, "List scrolling up");
                        mScrollDirection = SCROLLING_UP;
                    }else{
                        // dy is positive number,  scrolling down
                        Log.i(TAG, "List scrolling down");
                        mScrollDirection = SCROLLING_DOWN;
                    }
                }

                //mMessagesViewModel.setScrollDirection(mScrollDirection);
                // Set scrolling direction and and last visible item which is needed to know
                // the initial key position weather it's above or below
                mMessagesViewModel.setScrollDirection(mScrollDirection, lastCompletelyVisibleItem);

                // The position of last displayed item = total items, witch means we are at the bottom
                if(lastCompletelyVisibleItem >= (totalItemCount-1)){
                    // End of the list is here.
                    Log.i(TAG, "List reached the End. isHitBottom="+isHitBottom);
                    isHitBottom = true;
                    bottomVisibleItemCount = mMessagesRecycler.getChildCount();
                }else{
                    isHitBottom = false;
                }
                Log.i(TAG, "isHitBottom = "+isHitBottom);

                // The position of first displayed item = total items - visible count
                // witch means user starts scrolling up ant the first visible item is now on the bottom
                if(lastVisibleItem <= ((totalItemCount-1) - bottomVisibleItemCount)){
                    // End of the list is here.
                    Log.i(TAG, "First page scrolled up");
                    mScrollFab.setVisibility(View.VISIBLE);
                }else{
                    mScrollFab.setVisibility(View.INVISIBLE);
                }

                /*if(pastVisibleItems+visibleItemCount >= (totalItemCount-1)){
                    // End of the list is here.
                    Log.i(TAG, "End of list");
                    isHitBottom = true;
                }else{
                    isHitBottom = false;
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
                    //mMessage.setText(null);
                    //sendMessage(mChatId, messageText);
                    sendActivateMessage(messageText);
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

        mScrollFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mScrollFab is clicked");
                // Scroll to bottom

                if (mMessagesAdapter.getItemCount() > 0) {// stop scroll to bottom if there are no items
                    //mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                    Log.d(TAG, "adapter getItemCount= " + mMessagesAdapter.getItemCount());
                    int totalItemCount = mMessagesAdapter.getItemCount(); // total items count from the adapter
                    int lastCompletelyVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); // the position of last displayed item

                    if((lastCompletelyVisibleItem + 20) < totalItemCount){
                        Log.d(TAG, "it's a long distance. scrollToPosition");
                        mMessagesRecycler.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
                    }else{
                        Log.d(TAG, "it's a short distance. smoothScrollToPosition");
                        mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount() - 1);
                    }
                }

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
            /*// Push up content when clicking in edit text
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        long now = System.currentTimeMillis();
        // Re-start Active countdown timer on fragment start
        if(mActiveEndTime != null){
            mTimeLiftInMillis = mActiveEndTime - now;
            ShowRemainingTime(mChat);
        }

        // Re-start Last online countdown timer on fragment start
        if(mLastOnlineEndTime != null){
            UpdateTimeAgo(mLastOnlineEndTime);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // Cancel all countdown timers on fragment stop
        CancelLastOnlineTimer();
        CancelActiveTimer();

        // Update all revealed messages on fragment's stop
        if(mMessagesAdapter != null){
            // Get revealed list from the adapter
            List<Message> revealedList = mMessagesAdapter.getRevealedList();

            // Create a map for all messages need to be updated
            Map<String, Object> updateMap = new HashMap<>();

            for (int i = 0; i < revealedList.size(); i++) {
                Log.d(TAG, "revealedList message= "+revealedList.get(i).getMessage() + " key= "+revealedList.get(i).getKey());
                updateMap.put(revealedList.get(i).getKey()+"/revealed", true);
            }
            mMessagesRef.child(mChatId).updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // onSuccess clear the list to start all over
                    mMessagesAdapter.clearRevealedList();
                }
            });
        }

        // Update all sent messages on fragment's stop
        if(mMessagesAdapter != null){
            // Get revealed list from the adapter
            List<Message> sentList = mMessagesAdapter.getSentList();

            // Create a map for all messages need to be updated
            Map<String, Object> updateMap = new HashMap<>();

            for (int i = 0; i < sentList.size(); i++) {
                Log.d(TAG, "sentList message= "+sentList.get(i).getMessage() + " key= "+sentList.get(i).getKey());
                updateMap.put(sentList.get(i).getKey()+"/sent", true);
            }
            mMessagesRef.child(mChatId).updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // onSuccess clear the list to start all over
                    mMessagesAdapter.clearSentList();
                }
            });
        }

    }

    /*@Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }*/

    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean (IS_HII_BOTTOM, isHitBottom);
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


            //mTimer = new Timer();
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
            // extend mMessagesViewModel to pass Chat Key value and chat user key //
            mMessagesViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T)new MessagesViewModel (mChatId, mChatUserId);
                }
            }).get(MessagesViewModel.class);


            mMessagesViewModel.itemPagedList.observe(MessagesFragment.this, new Observer<PagedList<Message>>() {
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
                                        // Scroll to last item
                                        // Only scroll to bottom if user is not reading messages above
                                        Log.d(TAG, "scroll to bottom if user is not above. isHitBottom= "+ isHitBottom+ " items.size= "+items.size()+ " ItemCount= "+mMessagesAdapter.getItemCount());
                                        mMessagesAdapter.submitList(items);

                                        // Check if we have isHitBottom saved when change configuration occur or not
                                        if(savedInstanceState != null){
                                            isHitBottom = savedInstanceState.getBoolean(IS_HII_BOTTOM);
                                        }

                                        if( null == isHitBottom || isHitBottom){
                                            if(mMessagesAdapter.getItemCount()>0 ){// stop scroll to bottom if there are no items
                                                //mMessagesRecycler.smoothScrollToPosition(items.size()-1);
                                                Log.d(TAG, "adapter getItemCount= "+mMessagesAdapter.getItemCount());
                                                //mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount()-1);
                                                //mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount());
                                                mMessagesRecycler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mMessagesRecycler.smoothScrollToPosition(mMessagesAdapter.getItemCount()-1);
                                                    }
                                                }, 500);
                                            }
                                        }


                                        mItems = items;
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
                                    Log.d(TAG, "isHitBottom = "+isHitBottom);

                                    // Only scroll to bottom if user is not reading messages above
                                    if(null == isHitBottom || isHitBottom){
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
                        mChatUser.setKey(mChatUserId);
                        // display ChatUser name
                        if(null != mChatUser.getName()){
                            mUserName.setText(getFirstWord(mChatUser.getName()));
                        }

                        // display last online
                        if(null != mChatUser.getLastOnline()){

                            Log.d(TAG, "getLastOnline()= "+mChatUser.getLastOnline());
                            mLastOnlineEndTime = mChatUser.getLastOnline();

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

                                // Update Last online Time every minute
                                UpdateTimeAgo(mLastOnlineEndTime);

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

            /*mMessagesViewModel.getSenderId(mChatId).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String senderId) {
                    Log.d(TAG, "onChanged senderId = "+ senderId);
                    if(senderId != null){
                        if(senderId.equals(mCurrentUserId)){
                            Log.d(TAG, "CurrentUser is the sender. onChanged senderId = "+ senderId + " mCurrentUserId "+ mCurrentUserId);
                            isSender = true;
                        }else{
                            Log.d(TAG, "CurrentUser isn't the sender. onChanged senderId = "+ senderId + " mCurrentUserId "+ mCurrentUserId);
                            isSender = false;
                        }
                    }else{
                        Log.d(TAG, "sender is null. onChanged senderId = "+ senderId + " mCurrentUserId "+ mCurrentUserId);
                        isSender = null;
                    }
                }
            });*/

            mMessagesViewModel.getChat(mChatId).observe(this, new Observer<Chat>() {
                @Override
                public void onChanged(Chat chat) {
                    if (chat != null){
                        Log.d(TAG, "onChanged chat active = "+ chat.getActive());
                        mChat = chat;
                        if(null != mChat.getActive()){
                            // End timestamp is needed to restart the countdown on fragment start
                            mActiveEndTime = mChat.getActive();
                            // pass chat to MessagesAdapter to get active end time
                            mMessagesAdapter.setChat(mChat);
                        }
                        // Display the time left till deactivate the conversation
                        ShowRemainingTime(mChat);
                    }
                }
            });

            // Open user profile with custom actionBar is clicked //
            actionBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mListener.onTextViewNameClick(view, getAdapterPosition());
                    Log.i(TAG, "user avatar or name clicked. mChatUser getKey()= "+mChatUser.getKey());
                    NavDirections ProfileDirection = MessagesFragmentDirections.actionMessagesFragToProfileFrag(mChatUser.getKey());

                    //NavController navController = Navigation.findNavController(this, R.id.host_fragment);

                    //check if we are on Main Fragment not on complete Profile already
                    //Navigation.findNavController(view).navigate(ProfileDirection);

                    Navigation.findNavController(activity, R.id.host_fragment).navigate(ProfileDirection);
                }
            });

    }

}

    // A countdown timer to update last online time every minute
    private void UpdateTimeAgo(final Long lastOnline) {
        long now = System.currentTimeMillis();
        Log.d(TAG, "now = "+now);

        if (mAgoTimer != null) {
            CancelLastOnlineTimer();
        }

        mAgoTimer = new CountDownTimer(10*60*1000, 60*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long now = System.currentTimeMillis();
                Log.d(TAG, "mAgoTimer onTick: now = "+now + " getLastOnline= "+mChatUser.getLastOnline());
                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(lastOnline, now, DateUtils.MINUTE_IN_MILLIS);
                Activity activity = getActivity();
                if(activity != null && isAdded()){
                    mLastSeen.setText(getString(R.string.user_active_ago, ago));
                }

            }

            @Override
            public void onFinish() {
                //restart countDownTimer again
                Log.d(TAG, "mAgoTimer onFinish. We will restart it");
                mAgoTimer.start();
            }
        }.start();

    }

    // Display the time left till deactivate the conversation
    private void ShowRemainingTime(Chat chat) {
        long now = System.currentTimeMillis();

        if(chat == null){
            Log.d(TAG, "RemainingTime: It's the first message, don't show timer");
            mRemainingTimeText.setVisibility(View.GONE);
        }else{
            // it's not the first message
            Log.d(TAG, "RemainingTime: it's not the first message");
            if(null != chat.getActive()) {
                // Active is set
                Log.d(TAG, "RemainingTime: Active is set");
                if (chat.getActive() == 0) {
                    // This chat is active forever
                    Log.d(TAG, "RemainingTime: This chat is active forever, don't show timer");
                    mRemainingTimeText.setVisibility(View.GONE);
                } else {
                    // Chat is not active forever, check if it's still active
                    Log.d(TAG, "RemainingTime. Chat is not active forever, check if it's still active");
                    if (chat.getActive() < now) {
                        // Show to sender: this chat room is not active dialog
                        CancelActiveTimer();
                        mRemainingTimeText.setVisibility(View.VISIBLE);
                        mRemainingTimeText.setText(R.string.message_active_default_timer);
                        mRemainingTimeText.setTextColor(getResources().getColor(R.color.colorAccent));
                        Log.d(TAG, "RemainingTime: this chat room is not active");

                    } else {
                        // Chat is not active forever but it's still active so far
                        Log.d(TAG, "RemainingTime: Chat is not active forever but it's still active so far");
                        mRemainingTimeText.setVisibility(View.VISIBLE);
                        mRemainingTimeText.setTextColor(getResources().getColor(R.color.my_app_color_on_primary));
                        mTimeLiftInMillis = chat.getActive() - now;
                        Log.d(TAG, "RemainingTime: not active forever but still active. mTimeLiftInMillis= "+mTimeLiftInMillis);

                        StartActiveTimer();

                    }

                }// end of chat.getActive()==0

            }else{// end of null != chat.getActive()
                // It's not the first messages but Active is never set. Hide timer and cancel it
                CancelActiveTimer();
                mRemainingTimeText.setText(R.string.message_active_default_timer);
                mRemainingTimeText.setVisibility(View.GONE);

            }


        }// end of chat == null && isSender == null
    }

    // Cancel active countdown timer
    private void CancelActiveTimer() {
        if(mRemainingTimer != null){
            // cancel timer
            mRemainingTimer.cancel();
            Log.d(TAG, "mRemainingTimer canceled");
        }
    }

    // Cancel last online countdown timer
    private void CancelLastOnlineTimer() {
        if(mAgoTimer != null){
            mAgoTimer.cancel();
            Log.d(TAG, "mAgoTimer canceled");
        }
    }


    private void StartActiveTimer() {

        // check if it's a negative number
        if(mTimeLiftInMillis > 0){
            Log.i(TAG, "StartActiveTimer: time left is  bigger than 0, let's start timer");
            // Cancel any previous timer
            if (mRemainingTimer != null) {
                CancelActiveTimer();
            }
            mRemainingTimer = new CountDownTimer(mTimeLiftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLiftInMillis = millisUntilFinished;
                    Log.d(TAG, "onTick.  millisUntilFinished= "+ millisUntilFinished);
                    // Update timer text
                    UpdateActiveTimeText();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "mRemainingTimer onFinish.");
                    mRemainingTimeText.setVisibility(View.VISIBLE);
                    mRemainingTimeText.setText(R.string.message_active_default_timer);
                    mRemainingTimeText.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }.start();

        }else{
            // Timer is finished, should display 0
            Log.i(TAG, "StartActiveTimer: timer is finished, should display 0");
            mRemainingTimeText.setVisibility(View.VISIBLE);
            mRemainingTimeText.setText(R.string.message_active_default_timer);
            mRemainingTimeText.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void UpdateActiveTimeText() {

        //long days = TimeUnit.MILLISECONDS.toDays(mTimeLiftInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(mTimeLiftInMillis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(mTimeLiftInMillis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mTimeLiftInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLiftInMillis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mTimeLiftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLiftInMillis));
        //long milliseconds = diff - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(diff));

        // formatted timer text
        String formattedTimeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes,seconds );
        Log.d(TAG, "UpdateActiveTimeText: hms= "+ formattedTimeLeft);
        mRemainingTimeText.setText(formattedTimeLeft);
    }

    // Check if activate message should be displayed before sending the message or not
    private void sendActivateMessage(String messageText) {

        long now = System.currentTimeMillis();

        if(mChat == null){
            Log.d(TAG, "sendMessage: It's the first message, send it");
            sendMessage(mChatId, messageText);
        }else{
            // it's not the first message
            Log.d(TAG, "sendMessage: it's not the first message");
            if(null != mChat.getActive()) {
                // Active is set
                Log.d(TAG, "sendMessage: Active is set");
                if (mChat.getActive() == 0) {
                    // This chat is active forever
                    Log.d(TAG, "sendMessage: This chat is active forever, send message");
                    sendMessage(mChatId, messageText);
                } else {
                    // Chat is not active forever, check if it's still active
                    Log.d(TAG, "sendMessage. Chat is not active forever, check if it's still active");
                    if (mCurrentUserId.equals(mChat.getSender()) && mChat.getActive() < now) {
                        // Show to sender: this chat room is not active dialog
                        showChatInactiveDialog();
                        Log.d(TAG, "sendMessage: Show to sender: this chat room is not active dialog");

                    } else if (!mCurrentUserId.equals(mChat.getSender()) && mChat.getActive() < now) {
                        // Show to receiver: select active time dialog
                        showChatActivateDialog(mChatId);
                        Log.d(TAG, "sendMessage: Show to receiver: select active time dialog");
                    } else {
                        // Chat is not active forever but it's still active so far
                        Log.d(TAG, "sendMessage: Chat is not active forever but it's still active so far");
                        sendMessage(mChatId, messageText);
                    }

                }// end of mChat.getActive()==0

            }else{// end of null != mChat.getActive()
                // Active is never set, we must set it if current user is receiver
                Log.d(TAG, "sendMessage: Active is never set, we must set it if current user is receiver");
                if (null != mChat.getSender() && !mCurrentUserId.equals(mChat.getSender())) {
                    // Show to receiver: select active time dialog
                    showChatActivateDialog(mChatId);
                    Log.d(TAG, "sendMessage: select active time dialog");
                }else if(null != mChat.getSender() && mCurrentUserId.equals(mChat.getSender())){
                    // Active is never set, but this user is the sender, so keep sending messages
                    Log.d(TAG, "sendMessage: Active is never set, but this user is the sender, so keep sending messages");
                    sendMessage(mChatId, messageText);
                }else{
                    // Active is never set, and isSender is null for a strange reason, send message anyway
                    Log.d(TAG, "sendMessage: Active is never set, and isSender is null for a strange reason, send message anyway");
                    sendMessage(mChatId, messageText);
                }
            }

        }// end of mChat == null && isSender == null

        /*if(mChat == null && isSender == null){
            Log.d(TAG, "sendMessage: It's the first message, send it message");
            sendMessage(mChatId, messageText);
        }else{
            // it's not the first message
            Log.d(TAG, "sendMessage: it's not the first message");
            if(null != mChat.getActive()) {
                // Active is set
                Log.d(TAG, "sendMessage: Active is set");
                if (mChat.getActive() == 0) {
                    // This chat is active forever
                    Log.d(TAG, "sendMessage: This chat is active forever, send message");
                    //sendMessage(mChatId, messageText);
                } else {
                    // Chat is not active forever, check if it's still active
                    Log.d(TAG, "sendMessage. Chat is not active forever, check if it's still active");
                    if (isSender && mChat.getActive() < now) {
                        // Show to sender: this chat room is not active dialog
                        Log.d(TAG, "sendMessage: Show to sender: this chat room is not active dialog");

                    } else if (!isSender && mChat.getActive() < now) {
                        // Show to receiver: select active time dialog
                        Log.d(TAG, "sendMessage: Show to receiver: select active time dialog");
                    } else {
                        // Chat is not active forever but it's still active so far
                        Log.d(TAG, "sendMessage: Chat is not active forever but it's still active so far");
                        //sendMessage(mChatId, messageText);
                    }

                }// end of mChat.getActive()==0

            }else{// end of null != mChat.getActive()
                // Active is never set, we must set it if current user is receiver
                Log.d(TAG, "sendMessage: Active is never set, we must set it if current user is receiver");
                if (null != isSender && !isSender) {
                    // Show to receiver: select active time dialog
                    Log.d(TAG, "sendMessage: select active time dialog");;
                }else if(null != isSender && isSender){
                    // Active is never set, but this user is the sender, so keep sending messages
                    Log.d(TAG, "sendMessage: Active is never set, but this user is the sender, so keep sending messages");
                    //sendMessage(mChatId, messageText);
                }else{
                    // Active is never set, and isSender is null for a strange reason, send message anyway
                    Log.d(TAG, "sendMessage: Active is never set, and isSender is null for a strange reason, send message anyway");
                    //sendMessage(mChatId, messageText);
                }
            }

        }// end of mChat == null && isSender == null*/
    }

    private void sendMessage(String mChatId, String messageText) {

        mMessage.setText(null);// Remove text from EditText

        String messageKey = mMessagesRef.child(mChatId).push().getKey();

        Message message;
        if(mChat != null){
            if(null != mChat.getActive() && mChat.getActive() == 0) {
                // message revealed should be true
                message = new Message(messageText, mCurrentUserId, mCurrentUser.getName(),mCurrentUser.getAvatar(), false, true);
            }else{
                // message revealed should be false
                message = new Message(messageText, mCurrentUserId, mCurrentUser.getName(),mCurrentUser.getAvatar(), false, false);
            }
        }else{
            // message revealed should be false
            message = new Message(messageText, mCurrentUserId, mCurrentUser.getName(),mCurrentUser.getAvatar(), false, false);
        }

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
        Map<String, Object> chatValues;
        if(mChat != null){
            // get the existing chat and post again after changing last message
            Log.d(TAG, "sendMessage: chat exist, get the existing chat and post again ");
            mChat.setLastMessage(messageText);
            if(null == mChat.getSender()){
                mChat.setSender(mCurrentUserId);
            }
            chatValues = mChat.toMap();
        }else{
            // Create new chat from scratch
            Log.d(TAG, "sendMessage: chat is null, create new chat from scratch");
            Chat chat = new Chat(messageText, mCurrentUserId, members);
            chatValues = chat.toMap();
        }


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

        //mScrollDirection = REACHED_THE_BOTTOM;
        //mMessagesViewModel.setScrollDirection(mScrollDirection, lastCompletelyVisibleItem);
        isHitBottom = true;

        mDatabaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Log.i(TAG, "send message onSuccess");
                // Add message to sent array list
                message.setKey(messageKey);
                message.setSent(true);
                // Add successfully sent message to adapter sent array list
                // It's used to notify the adapter to update item position
                mMessagesAdapter.addToSentList(message);


                /*for (int i = 0; i < mItems.size(); i++) {
                    if (mItems.get(i) != null && mItems.get(i).getKey().equals(messageKey)) {
                        Log.d(TAG, "onSuccess. mItems. message= " + mItems.get(i).getMessage() + " key= " + mItems.get(i).getKey());
                        if (mItems.get(i) != null) {
                            mItems.get(i).setSent(true);
                        }

                    }
                }

                mMessagesAdapter.submitList(mItems);*/
                //mMessagesAdapter.submitList(mItems);
                //mMessagesAdapter.notifyDataSetChanged();
                // To scroll to bottom when user send new message
                /*mScrollDirection = REACHED_THE_BOTTOM;
                mMessagesViewModel.setScrollDirection(mScrollDirection);
                isHitBottom = true;*/
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

    //Show inactive alert dialog
    private void showChatInactiveDialog() {
        ChatInactiveAlertFragment chatInactiveFragment = ChatInactiveAlertFragment.newInstance();
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            chatInactiveFragment.show(fragmentManager, CHAT_INACTIVE_FRAGMENT);
            Log.i(TAG, "edit/chatInactiveFragment show clicked ");
        }
    }

    //Show a dialog to select whether to edit or un-reveal
    private void showChatActivateDialog(String mChatId) {
        ActivateChatAlertFragment chatActivateFragment = ActivateChatAlertFragment.newInstance(mChatId, this);
        if (getFragmentManager() != null) {
            fragmentManager = getFragmentManager();
            chatActivateFragment.show(fragmentManager, ACTIVATE_CHAT_FRAGMENT);
            Log.i(TAG, "edit/chatActivateFragment show clicked ");
        }
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick forever is selected. view= " + view + " position= " + position);
        // Reveal all messages when "active forever" is selected
        mMessagesViewModel.revealMessages(mChatId);
        //Log.i(TAG, "mItems LastKey= " + mItems.getLastKey());
        //mMessagesAdapter.notifyAll();
        //mMessagesRecycler.getRecycledViewPool().clear();
        //mMessagesAdapter.submitList(mItems);
        //mMessagesRecycler.invalidate();
        //mMessagesRecycler.setAdapter(null);
        //mMessagesRecycler.setLayoutManager(null);
        //mMessagesRecycler.setAdapter(mMessagesAdapter);
        //mMessagesRecycler.setLayoutManager(mLinearLayoutManager);
        //mMessagesAdapter.notifyDataSetChanged();
        //mMessagesAdapter.notifyDataSetChanged();
    }

    /*private class CountDownTask extends AsyncTask<Void, Integer, Void> {

        // A callback method executed on UI thread on starting the task
        @Override
        protected void onPreExecute() {
            // Getting reference to the TextView tv_counter of the layout activity_main
            mLastSeen.setText("wello");

        }

        // A callback method executed on non UI thread, invoked after
        // onPreExecute method if exists
        @Override
        protected Void doInBackground(Void... params) {

            for(int i=100000000;i>=0;i--){
                try {
                    Thread.sleep(1000);
                    publishProgress(i); // Invokes onProgressUpdate()
                } catch (InterruptedException e) {
                }
            }
            return null;
        }

        // A callback method executed on UI thread, invoked by the publishProgress()
        // from doInBackground() method
        @Override
        protected void onProgressUpdate(Integer... values) {
            // Getting reference to the TextView tv_counter of the layout activity_main
            mLastSeen.setText( Integer.toString(values[0].intValue()));
        }

        // A callback method executed on UI thread, invoked after the completion of the task
        @Override
        protected void onPostExecute(Void result) {
            // Getting reference to the TextView tv_counter of the layout activity_main
        }
    }*/
}


