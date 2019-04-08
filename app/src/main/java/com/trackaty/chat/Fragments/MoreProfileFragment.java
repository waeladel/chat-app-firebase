package com.trackaty.chat.Fragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trackaty.chat.Adapters.ProfileAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.Utils.SortSocial;
import com.trackaty.chat.Utils.Sortbysection;
import com.trackaty.chat.ViewModels.MoreProfileViewModel;
import com.trackaty.chat.ViewModels.ProfileViewModel;
import com.trackaty.chat.activities.MainActivity;
import com.trackaty.chat.models.Profile;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.User;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreProfileFragment extends Fragment implements ItemClickListener {

    private final static String TAG = MoreProfileFragment.class.getSimpleName();
    private String mCurrentUserId, mUserId;
    private FirebaseUser mFirebaseCurrentUser;

    private User mUser;

    private  static final int SECTION_ABOUT = 500;
    private  static final int SECTION_SOCIAL = 600;
    private  static final int SECTION_WORK = 700;
    private  static final int SECTION_HABITS = 800;

    // requests and relations status
    private static final String RELATION_STATUS_SENDER = "sender";
    private static final String RELATION_STATUS_RECEIVER = "receiver";
    private static final String RELATION_STATUS_STALKER = "stalker";
    private static final String RELATION_STATUS_FOLLOWED = "followed";
    private static final String RELATION_STATUS_NOT_FRIEND = "notFriend";

    // [START declare_database_ref]
    /*private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;*/

    private RecyclerView mProfileRecycler;
    private ArrayList <Profile> mUserArrayList;

    private ProfileAdapter mProfileAdapter;

    private MoreProfileViewModel mMoreProfileViewModel;
    private Relation mRelation ;

    private Context mActivityContext;
    private Activity activity;

    public MoreProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_more_profile, container, false);

        // prepare the Adapter
        mUserArrayList  = new ArrayList<>();
        /*mUserArrayList.add("mama");
        mUserArrayList.add("baba");
        mUserArrayList.add("lala");
        mUserArrayList.add("tata");
        mUserArrayList.add("kaka");*/

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser!= null ? mFirebaseCurrentUser.getUid() : null;

        if (getArguments() != null) {
            //mCurrentUserId = MoreProfileFragmentArgs.fromBundle(getArguments()).getCurrentUserId();
            mUserId = MoreProfileFragmentArgs.fromBundle(getArguments()).getUserId(); // any user
            //mUser = ProfileFragmentArgs.fromBundle(getArguments()).getUser();// any user
            Log.d(TAG, "mCurrentUserId= " + mCurrentUserId + "mUserId= " + mUserId );
        }

        mMoreProfileViewModel = ViewModelProviders.of(this).get(MoreProfileViewModel.class);

        mMoreProfileViewModel.getUser(mUserId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    mUser = user;
                    if(mUserArrayList != null && mUserArrayList.size()>0){
                        // Clear old Array data
                        mUserArrayList.clear();
                        showCurrentUser(mUser);
                    }else{
                        showCurrentUser(mUser);
                    }

                }
            }
        });

        // [display parcelable data]
            /*if (null != mCurrentUserId && mCurrentUserId.equals(mUserId)) {
                // it's logged in user profile
                showCurrentUser(mUser);
            } else {
                // it's another user
                //mUserRef = mDatabaseRef.child("users").child(mUserId);
                //showUser(mUserId);
            }*/

            mProfileAdapter  = new ProfileAdapter(MoreProfileFragment.this,mUserArrayList,mUserId,this);

            // Initiate the RecyclerView
            mProfileRecycler  = (RecyclerView) fragView.findViewById(R.id.profile_recycler);
            mProfileRecycler.setHasFixedSize(true);
            mProfileRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
            mProfileRecycler.setAdapter(mProfileAdapter);

        return fragView;
    }

    private void showCurrentUser(User user) {
        if (user != null) {

            Field[] fields = user.getClass().getDeclaredFields();
            for (Field f : fields) {
                Log.d(TAG, "fields=" + f.getName());
                //Log.d(TAG, "fields="+f.get);
                getDynamicMethod(f.getName(), user);
            }
            Log.d(TAG, "mUserArrayList sorted get size" + mUserArrayList.size());

            // sort ArrayList into sections then notify the adapter
            Collections.sort(mUserArrayList, new Sortbysection());

            for (int i = 0; i < mUserArrayList.size(); i++) {
                Log.i(TAG, "mUserArrayList sorted" + mUserArrayList.get(i).getKey());
            }

            mProfileAdapter.notifyDataSetChanged();
                            /*String userName = dataSnapshot.child("name").getValue().toString();
                            String mCurrentUserId = dataSnapshot.getKey();*/
            Log.d(TAG, "user exist: Name=" + user.getName());

        }

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
            actionbar.setTitle(R.string.more_profile_frag_title);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayShowCustomEnabled(false);
        }

    }

    private void getDynamicMethod(String fieldName, User user) {

        Method[] methods = user.getClass().getMethods();

        for (Method method : methods) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (fieldName.length() + 3))) {
                if (method.getName().toLowerCase().endsWith(fieldName.toLowerCase())) {
                    // Method found, run it
                    try {
                        // check if is not null or empty
                        if (method.invoke(user) != null){
                            String value = method.invoke(user).toString();
                            Log.d(TAG, "Method Type=" + method.getGenericReturnType());

                            if(fieldName.equals("work")
                                    || fieldName.equals("college")
                                    || fieldName.equals("school")){
                                //add data for work section
                                if(fieldName.equals("work")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_WORK+1, SECTION_WORK));
                                }
                                if(fieldName.equals("college")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_WORK+2, SECTION_WORK));
                                }
                                if(fieldName.equals("school")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_WORK+3, SECTION_WORK));
                                }
                            }

                            if(fieldName.equals("phone")
                                    || fieldName.equals("facebook")
                                    || fieldName.equals("instagram")
                                    || fieldName.equals("twitter")
                                    || fieldName.equals("snapchat")
                                    || fieldName.equals("tumblr")
                                    || fieldName.equals("pubg")
                                    || fieldName.equals("vk")
                                    || fieldName.equals("askfm")
                                    || fieldName.equals("curiouscat")
                                    || fieldName.equals("saraha")
                                    || fieldName.equals("pinterest")
                                    || fieldName.equals("soundcloud")
                                    || fieldName.equals("spotify")
                                    || fieldName.equals("anghami")
                                    || fieldName.equals("twitch")
                                    || fieldName.equals("youtube")
                                    || fieldName.equals("linkedIn")
                                    || fieldName.equals("wikipedia")
                                    || fieldName.equals("website")){

                                //add data for social section
                                if(fieldName.equals("phone")){
                                    mUserArrayList.add(new Profile(fieldName, user.getPhone(), SECTION_SOCIAL+1 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("facebook")){
                                    mUserArrayList.add(new Profile(fieldName, user.getFacebook(), SECTION_SOCIAL+2 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("instagram")){
                                    mUserArrayList.add(new Profile(fieldName,user.getInstagram(), SECTION_SOCIAL+3 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("twitter")){
                                    mUserArrayList.add(new Profile(fieldName, user.getTwitter(), SECTION_SOCIAL+4 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("snapchat")){
                                    mUserArrayList.add(new Profile(fieldName, user.getSnapchat(), SECTION_SOCIAL+5 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("tumblr")){
                                    mUserArrayList.add(new Profile(fieldName,user.getTumblr(), SECTION_SOCIAL+6 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("pubg")){
                                    mUserArrayList.add(new Profile(fieldName, user.getPubg(), SECTION_SOCIAL+7 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("vk")){
                                    mUserArrayList.add(new Profile(fieldName, user.getVk(), SECTION_SOCIAL+8 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("askfm")){
                                    mUserArrayList.add(new Profile(fieldName, user.getAskfm(), SECTION_SOCIAL+9 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("curiouscat")){
                                    mUserArrayList.add(new Profile(fieldName, user.getCuriouscat(), SECTION_SOCIAL+10 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("saraha")){
                                    mUserArrayList.add(new Profile(fieldName, user.getSaraha(), SECTION_SOCIAL+11 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("pinterest")){
                                    mUserArrayList.add(new Profile(fieldName, user.getPinterest(), SECTION_SOCIAL+12 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("soundcloud")){
                                    mUserArrayList.add(new Profile(fieldName, user.getSoundcloud(), SECTION_SOCIAL+13 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("spotify")){
                                    mUserArrayList.add(new Profile(fieldName, user.getSpotify(), SECTION_SOCIAL+14 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("anghami")){
                                    mUserArrayList.add(new Profile(fieldName, user.getAnghami(), SECTION_SOCIAL+15 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("twitch")){
                                    mUserArrayList.add(new Profile(fieldName, user.getTwitch(), SECTION_SOCIAL+16 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("youtube")){
                                    mUserArrayList.add(new Profile(fieldName, user.getYoutube(), SECTION_SOCIAL+17 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("linkedIn")){
                                    mUserArrayList.add(new Profile(fieldName, user.getLinkedIn(), SECTION_SOCIAL+18 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("wikipedia")){
                                    mUserArrayList.add(new Profile(fieldName, user.getWikipedia(), SECTION_SOCIAL+19 ,SECTION_SOCIAL));
                                }
                                if(fieldName.equals("website")){
                                    mUserArrayList.add(new Profile(fieldName, user.getWebsite(), SECTION_SOCIAL+20 ,SECTION_SOCIAL));
                                }

                            }

                            if(fieldName.equals("birthDate")
                                    || fieldName.equals("gender")
                                    || fieldName.equals("horoscope")
                                    || fieldName.equals("lives")
                                    || fieldName.equals("hometown")
                                    || fieldName.equals("nationality")
                                    || fieldName.equals("politics")
                                    || fieldName.equals("religion")){

                                //add data for about section
                                if(fieldName.equals("birthDate")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+1 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("gender")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+2 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("horoscope")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+3 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("lives")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+4 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("hometown")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+5 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("nationality")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+6 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("politics")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+7 ,SECTION_ABOUT));
                                }
                                if(fieldName.equals("religion")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_ABOUT+8 ,SECTION_ABOUT));
                                }
                            }

                            if(fieldName.equals("smoke")
                                    || fieldName.equals("shisha")
                                    || fieldName.equals("drugs")
                                    || fieldName.equals("drink")
                                    || fieldName.equals("gamer")
                                    || fieldName.equals("cook")
                                    || fieldName.equals("read")
                                    || fieldName.equals("athlete")
                                    || fieldName.equals("travel")){

                                //add data for habits section
                                if(fieldName.equals("smoke")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+1, SECTION_HABITS));
                                }
                                if(fieldName.equals("shisha")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+2, SECTION_HABITS));
                                }
                                if(fieldName.equals("drugs")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+3, SECTION_HABITS));
                                }
                                if(fieldName.equals("drink")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+4, SECTION_HABITS));
                                }
                                if(fieldName.equals("gamer")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+5, SECTION_HABITS));
                                }
                                if(fieldName.equals("cook")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+6, SECTION_HABITS));
                                }
                                if(fieldName.equals("read")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+7, SECTION_HABITS));
                                }
                                if(fieldName.equals("athlete")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+8, SECTION_HABITS));
                                }
                                if(fieldName.equals("travel")){
                                    mUserArrayList.add(new Profile(fieldName, value, SECTION_HABITS+9, SECTION_HABITS));
                                }
                            }

                        }

                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Could not determine method: " + method.getName());

                    } catch (InvocationTargetException e) {

                        Log.e(TAG, "Could not determine method:" + method.getName());
                    }

                }
            }
        }

    }

    // user interface to detect item click on the recycler adapter
    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.d(TAG, "item clicked fragment= " + position+ " key "+mUserArrayList.get(position).getKey());
        Intent intent;
        String Url;
        switch (mUserArrayList.get(position).getKey()){
            case "phone":
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mUserArrayList.get(position).getSocial().getUrl()));
                startActivity(intent);
                break;
            case "facebook":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("facebook.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.facebook.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "instagram":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("instagram.com")&&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.instagram.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "twitter":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("twitter.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.twitter.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "snapchat":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("snapchat.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://story.snapchat.com/s/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "tumblr":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("tumblr.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "http://"+mUserArrayList.get(position).getSocial().getUrl()+".tumblr.com/";
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "pubg":
                ClipboardManager clipboard = (ClipboardManager) mActivityContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getResources().getString(R.string.user_pubg_clipboard_label), mUserArrayList.get(position).getSocial().getUrl());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(mActivityContext,R.string.user_pubg_clipboard_toast,
                        Toast.LENGTH_SHORT).show();
                break;
            case "vk":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("vk.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.vk.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "askfm":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("ask.fm") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl()) ){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.ask.fm/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "curiouscat":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("curiouscat.me") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.curiouscat.me/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "saraha":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("saraha.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://"+mUserArrayList.get(position).getSocial().getUrl()+".sarahah.com/";
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "pinterest":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("pinterest.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.pinterest.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "soundcloud":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("soundcloud.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.soundcloud.com/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "spotify":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("spotify.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://open.spotify.com/user/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "anghami":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("anghami.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://play.anghami.com/profile/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "twitch":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("twitch.tv") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.twitch.tv/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "youtube":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("youtube.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                    startActivity(intent);
                }
                break;
            case "linkedIn":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("linkedin.com") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                }else{
                    Url = "https://www.linkedin.com/in/"+mUserArrayList.get(position).getSocial().getUrl();
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                }
                startActivity(intent);
                break;
            case "wikipedia":
                if (mUserArrayList.get(position).getSocial().getUrl().contains("wikipedia.org") &&  URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                    startActivity(intent);
                }
                break;
            case "website":
                if( URLUtil.isValidUrl(mUserArrayList.get(position).getSocial().getUrl())){
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUserArrayList.get(position).getSocial().getUrl()));
                    startActivity(intent);
                }
                break;

        }
    }
}

