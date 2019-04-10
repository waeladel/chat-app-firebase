package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trackaty.chat.Adapters.RevealAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Social;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RevealFragment extends DialogFragment implements ItemClickListener {
    private final static String TAG = RevealFragment.class.getSimpleName();
    DatePickerDialog.OnDateSetListener ondateSet;

    private final static String PRIVET_CONTACTS_KEY = "privateContacts";
    public static ArrayList<Social> privateContacts;
    private ArrayList<Social> pramsContacts;
    private RevealAdapter mRequestAdapter;
    private RecyclerView mRequestRecycler;
    private Button mSendButton, mCancelButton;



    private Context mActivityContext;
    private Activity activity;

    private static ItemClickListener itemClickListen;


    public RevealFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RevealFragment newInstance(ArrayList<Social> pContacts, ItemClickListener itemClickListener) {
        privateContacts = pContacts;
        itemClickListen = itemClickListener;


        for (int i = 0; i < privateContacts.size(); i++) {
            Log.i(TAG, "mParam PrivateContactsList sorted " + privateContacts.get(i).getKey());
        }
        RevealFragment fragment = new RevealFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year ,month ,day;
        if(birth != null){
            c.setTimeInMillis(birth);
        }
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), ondateSet, year,month,day );

        //return new DatePickerDialog(getActivity(), ondateSet, 1990,2,1 );
        //return new DatePickerDialog(getActivity(), this, year, month, day);
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.colorPickerStyle);
        // this setStyle is VERY important.
        // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
        // so for example the size of the default dialog will not get in my way
        // the style extends the default one. see bellow.
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }

        View fragView = inflater.inflate(R.layout.reveal_request_dialog_fragment, container);

        // get PrivateContactsList array from arguments
        if (getArguments() != null) {
            pramsContacts = getArguments().getParcelableArrayList(PRIVET_CONTACTS_KEY);
            for (int i = 0; i < pramsContacts.size(); i++) {
                Log.i(TAG, "getArguments() PrivateContactsList sorted " + pramsContacts.get(i).getKey());
            }

            mSendButton = (Button) fragView.findViewById(R.id.send_button);
            mCancelButton = (Button) fragView.findViewById(R.id.cancel_button);

            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListen != null){
                        itemClickListen.onClick(view, 1, false);
                    }
                }
            });

            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListen != null){
                        itemClickListen.onClick(view, 0, false);
                    }
                    //dismiss();
                }
            });

            mRequestAdapter  = new RevealAdapter(mActivityContext, pramsContacts, this);

            // Initiate the RecyclerView
            mRequestRecycler  = (RecyclerView) fragView.findViewById(R.id.contacts_recycler);
            mRequestRecycler.setHasFixedSize(true);
            mRequestRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
            mRequestRecycler.setAdapter(mRequestAdapter);
        }

        // R.layout.dialog_color_picker is the custom layout of my dialog
        //WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        //wmlp.gravity = Gravity.LEFT;

        return fragView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick view= " + view.getTag()+ " position= " +position);

        if(itemClickListen != null && position != RecyclerView.NO_POSITION){
            itemClickListen.onClick(view, position, false);
        }
    }

    // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListen = itemClickListener;
        }

}
