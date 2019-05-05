package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.Adapters.RevealAdapter;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;
import com.trackaty.chat.models.Social;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ActivateChatAlertFragment extends DialogFragment {
    private final static String TAG = ActivateChatAlertFragment.class.getSimpleName();
    DatePickerDialog.OnDateSetListener ondateSet;

    private Button mSendButton, mCancelButton;
    private Spinner mSpinner;
    private long selectedTime;

    private final static String ARGS_KEY_CHAT_ID = "chatId";

    private Context mActivityContext;
    private Activity activity;
    private String chatId;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;


    public ActivateChatAlertFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ActivateChatAlertFragment newInstance(String chatId) {

        ActivateChatAlertFragment fragment = new ActivateChatAlertFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        args.putString(ARGS_KEY_CHAT_ID , chatId);
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

       /* if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }*/
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        View fragView = inflater.inflate(R.layout.activate_chat_dialog_fragment, container);

        if (getArguments() != null) {
            chatId = (String) getArguments().get(ARGS_KEY_CHAT_ID);
            // use received chatKey to create a database ref
            mChatsRef = mDatabaseRef.child("chats").child(chatId);
        }

        // get PrivateContactsList array from arguments

        mSendButton = (Button) fragView.findViewById(R.id.send_button);
        mCancelButton = (Button) fragView.findViewById(R.id.cancel_button);
        mSpinner = (Spinner) fragView.findViewById(R.id.activation_spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        //selectedTime = 5;
                        selectedTime = TimeUnit.MINUTES.toMillis(5);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 1:
                        selectedTime = TimeUnit.MINUTES.toMillis(10);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 2:
                        selectedTime = TimeUnit.MINUTES.toMillis(15);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 3:
                        selectedTime = TimeUnit.MINUTES.toMillis(30);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 4:
                        selectedTime = TimeUnit.MINUTES.toMillis(45);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 5:
                        selectedTime = TimeUnit.HOURS.toMillis(1);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 6:
                        selectedTime = TimeUnit.HOURS.toMillis(2);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 7:
                        selectedTime = TimeUnit.HOURS.toMillis(3);
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;
                    case 8:
                        selectedTime = 0L;
                        Log.d(TAG, "selectedTime= " + selectedTime);
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "mSendButton onClick");
                Log.d(TAG, "chatId= " + chatId);
                long now = System.currentTimeMillis();
                Log.d(TAG, "selectedTime= " + selectedTime + " now= "+now +" result= " +(selectedTime+ now));
                //mChatsRef.keepSynced(false);
                mChatsRef.child("active").setValue(selectedTime+ now).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "onComplete: active onComplete");
                        if(task.isSuccessful()){
                            Log.i(TAG, "onSuccess: active updated ");
                            dismiss();
                        }else{
                            Log.i(TAG, "onFailure: active failed ");
                            Toast.makeText(mActivityContext,R.string.activate_chat_error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Dismiss dialog even if not Success, active is updated offline.
                dismiss();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "mCancelButton onClick");
                dismiss();
            }
        });


        // R.layout.dialog_color_picker is the custom layout of my dialog
        //WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        //wmlp.gravity = Gravity.LEFT;

        return fragView;
    }



    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(null != getDialog()){
            getDialog().setTitle(R.string.user_request_dialog_title);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

}
