package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trackaty.chat.Adapters.RevealAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.ViewModels.RevealViewModel;
import com.trackaty.chat.models.Social;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RevealFragment extends DialogFragment implements ItemClickListener {
    private final static String TAG = RevealFragment.class.getSimpleName();

    private final static String PRIVET_CONTACTS_KEY = "privateContacts";
    private final static String RELATION_STATUS_KEY = "relationStatus";
    private ArrayList<Social> pramsContacts;
    private static ArrayList<Social> sContacts;
    private RevealAdapter mRequestAdapter;
    private RecyclerView mRequestRecycler;
    private Button mSendButton, mCancelButton;
    private TextView mTitle;
    // requests and relations status
    private static final String RELATION_STATUS_SENDER = "sender";
    private static final String RELATION_STATUS_RECEIVER = "receiver";
    private static final String RELATION_STATUS_STALKER = "stalker";
    private static final String RELATION_STATUS_FOLLOWED = "followed";
    private static final String RELATION_STATUS_NOT_FRIEND = "notFriend";
    private String mRelationStatus;

    private Context mActivityContext;
    private Activity activity;
    private RevealViewModel mViewModel;

    private static ItemClickListener sItemClickListen;
    private List<Boolean> checkedList;

    public RevealFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RevealFragment newInstance(ArrayList<Social> contacts, String relationStatus, ItemClickListener itemClickListener) {
        //sOriginalContacts = contacts;
        sItemClickListen = itemClickListener;
        //sContacts = contacts;

        RevealFragment fragment = new RevealFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PRIVET_CONTACTS_KEY, contacts); // Send the contact list as arguments
        args.putString(RELATION_STATUS_KEY, relationStatus); // Send relationStatus as arguments, it's important for the dialog title
        fragment.setArguments(args);
        return fragment;
    }

    public void setRelationStatus(String relationStatus){
        mRelationStatus = relationStatus;
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
        setStyle(STYLE_NO_TITLE, R.style.DatePickerMyTheme);
        checkedList = new ArrayList<>();
        // Use a viewModel to preserve the new created contacts, We don't want to use the argument contacts because it changes the original contacts in ProfileFragment
        mViewModel = new ViewModelProvider(this).get(RevealViewModel.class);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }

        View fragView = inflater.inflate(R.layout.reveal_request_dialog_fragment, container);

        mSendButton =  fragView.findViewById(R.id.send_button);
        mCancelButton =  fragView.findViewById(R.id.cancel_button);
        mTitle =  fragView.findViewById(R.id.reported_name);


        // get PrivateContactsList array from arguments
        if (getArguments() != null) {
            pramsContacts = getArguments().getParcelableArrayList(PRIVET_CONTACTS_KEY);
            mRelationStatus = getArguments().getString(RELATION_STATUS_KEY);
            if(null == mViewModel.getContacts() || mViewModel.getContacts().isEmpty()){
                Log.i(TAG, "set mViewModel Contacts");
                // Create new contacts from the argument contacts because we don't want to make changes to the original contacts
                mViewModel.setContacts(pramsContacts);
            }

            if(null == mViewModel.getRelationStatus()){
                Log.i(TAG, "set mViewModel relationStatus");
                mViewModel.setRelationStatus(mRelationStatus);
            }

            if (mViewModel.getContacts() != null) {
                for (int i = 0; i < mViewModel.getContacts().size(); i++) {
                    Log.i(TAG, "getArguments() mViewModel.getContacts() sorted " + mViewModel.getContacts().get(i).getKey() + " public= "+ mViewModel.getContacts().get(i).getValue().getPublic());
                    if(mViewModel.getContacts().get(i).getValue().getPublic()){
                        Log.i(TAG, "this "+ mViewModel.getContacts().get(i).getKey()+ "item already selected of public");
                        // Enable send button, we already have at least one public or selected contact
                        mSendButton.setEnabled(true);

                        // Add the public item to checkedList
                        checkedList.add(true);
                    }
                }
            }

            // Adjust button text and dialog title according to relationship status
            if(mRelationStatus != null){
                Log.d(TAG, "mRelationStatus= " +mRelationStatus);
                switch (mRelationStatus){
                    case RELATION_STATUS_SENDER:
                        // If this selected user sent me the request
                        //Approve request
                        mSendButton.setText(R.string.request_button_approve_hint);
                        mTitle.setText(R.string.user_request_approve_dialog_hint);
                        Log.d(TAG, "mRelationStatus= " +mRelationStatus);
                        break;
                    case RELATION_STATUS_STALKER:
                        // If this selected user sent me the request and i am editing it
                        // edit relation
                        mSendButton.setText(R.string.save_button);
                        mTitle.setText(R.string.user_request_edit_dialog_hint);
                        Log.d(TAG, "mRelationStatus= " +mRelationStatus);
                        break;
                    default:
                        // Show request dialog
                        Log.d(TAG, "mRelationStatus= " +mRelationStatus);
                        break;
                }
            }

            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sItemClickListen != null){
                        sItemClickListen.onClick(view, 1, false);
                    }
                    dismiss();
                }
            });

            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sItemClickListen != null){
                        sItemClickListen.onClick(view, 0, false);
                    }

                    dismiss();
                }
            });

            mRequestAdapter  = new RevealAdapter(mActivityContext, mViewModel.getContacts(), this);

            // Initiate the RecyclerView
            mRequestRecycler  =  fragView.findViewById(R.id.contacts_recycler);
            mRequestRecycler.setHasFixedSize(true);
            mRequestRecycler.setLayoutManager(new LinearLayoutManager(mActivityContext));
            mRequestRecycler.setAdapter(mRequestAdapter);
        }

        // R.layout.dialog_color_picker is the custom layout of my dialog
        //WindowManager.LayoutParams windowManagerLayout = getDialog().getWindow().getAttributes();
        //windowManagerLayout.gravity = Gravity.LEFT;

        return fragView;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Log.i(TAG, "onClick view= " + view.getTag()+ " position= " +position);

        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            if (checkBox.isChecked()) {
                Log.i(TAG, "onClick checkBox is checked. position= "+ position);
                if (mViewModel.getContacts() != null) {
                    // Change the value of ViewModel contact to true so it stays checked in configuration changes
                    mViewModel.getContacts().get(position).getValue().setPublic(true);
                    // Add to public item to checkedList
                    checkedList.add( true);
                    // Enable send button
                    mSendButton.setEnabled(true);
                }
            }else{
                Log.i(TAG, "onClick checkBox is unchecked. position= "+ position);
                if (mViewModel.getContacts() != null) {
                    // Change the value of ViewModel contact to false so it stays unchecked in configuration changes
                    mViewModel.getContacts().get(position).getValue().setPublic(false);
                    // remove from public item to checkedList
                    if(!checkedList.isEmpty()){
                        checkedList.remove(0);
                    }

                    for (int i = 0; i < checkedList.size(); i++) {
                        Log.d(TAG, "onClick checkBox checkedList loop = "+ checkedList.size());
                    }

                    // Disable send button if there is no selected item
                    mSendButton.setEnabled(checkedList.size() > 0);

                }

            }

            if(sItemClickListen != null && position != RecyclerView.NO_POSITION){
                sItemClickListen.onClick(view, position, false);
            }
        }
    }

    // needed only if i want the listener to be inside the adapter
        public void setItemClickListener(ItemClickListener itemClickListener) {
            sItemClickListen = itemClickListener;
        }

}
