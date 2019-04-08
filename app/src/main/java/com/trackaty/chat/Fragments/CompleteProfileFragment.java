package com.trackaty.chat.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.trackaty.chat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteProfileFragment extends Fragment {


    private TextView textView;
    private FirebaseUser mFirebaseCurrentUser;
    private String mCurrentUserId;

    //val data: String = CompleteProfileFragmentArgs.fromBundle(arguments).data // data = "Any data"

    public CompleteProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragView = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        textView = (TextView) fragView.findViewById(R.id.wael);

        //Get current logged in user
        mFirebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserId = mFirebaseCurrentUser != null ? mFirebaseCurrentUser.getUid() : null;



        //String currentUserId = CompleteProfileFragmentArgs.fromBundle(getArguments()).getCurrentUserId();
        String userEmail = CompleteProfileFragmentArgs.fromBundle(getArguments()).getUserEmail();
        String userName = CompleteProfileFragmentArgs.fromBundle(getArguments()).getUserName();
        textView.setText(mCurrentUserId+userEmail+userName);

        // Inflate the layout for this fragment
        return fragView;
    }

}
