package com.trackaty.chat.Fragments;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ActivateChatAlertFragment extends DialogFragment implements ItemClickListener{
    private final static String TAG = ActivateChatAlertFragment.class.getSimpleName();

    private Button mSendButton, mCancelButton;
    private Spinner mSpinner;
    private long selectedTime, activeTimeValue;

    private final static String ARGS_KEY_CHAT_ID = "chatId";

    private Context mActivityContext;
    private Activity activity;
    private String chatId;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mChatsRef;

    private static ItemClickListener itemClickListen;

    public ActivateChatAlertFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ActivateChatAlertFragment newInstance(String chatId, ItemClickListener itemClickListener) {

        ActivateChatAlertFragment fragment = new ActivateChatAlertFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        args.putString(ARGS_KEY_CHAT_ID , chatId);
        fragment.setArguments(args);

        itemClickListen = itemClickListener;
        return fragment;
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
        //setStyle(STYLE_NO_TITLE, R.style.DatePickerMyTheme);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activate_chat_dialog_fragment, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        // Fetch arguments from bundle and set title
        if (getArguments() != null) {
            chatId = (String) getArguments().get(ARGS_KEY_CHAT_ID);
            // use received chatKey to create a database ref
            mChatsRef = mDatabaseRef.child("chats").child(chatId);
        }

        // get PrivateContactsList array from arguments

        mSendButton = view.findViewById(R.id.send_button);
        mCancelButton = view.findViewById(R.id.cancel_button);
        mSpinner =  view.findViewById(R.id.activation_spinner);
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

                // If forever is selected, activeTimeValue should be 0
                if(selectedTime != 0L){
                    activeTimeValue = selectedTime+ now;
                }else{
                    activeTimeValue = selectedTime;
                }
                mChatsRef.child("active").setValue(activeTimeValue).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                // Click listener to inform fragment that active forever is selected
                if(itemClickListen != null && selectedTime == 0L){
                    itemClickListen.onClick(view, 0, false);
                }

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


    }

    /*@Override
    public void onResume() {
        super.onResume();
       *//* ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);*//*

    }*/

    @Override
    public void onClick(View view, int position, boolean isLongClick) {

    }
}
