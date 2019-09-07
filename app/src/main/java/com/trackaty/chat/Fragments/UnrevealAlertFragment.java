package com.trackaty.chat.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trackaty.chat.Interface.ItemClickListener;
import com.trackaty.chat.R;

import org.jetbrains.annotations.NotNull;

public class UnrevealAlertFragment extends DialogFragment implements ItemClickListener {
    private final static String TAG = UnrevealAlertFragment.class.getSimpleName();

    private final static String PRIVET_CONTACTS_KEY = "privateContacts";


    private Context mActivityContext;
    private Activity activity;

    // click listener to pass click events to parent fragment
    private static ItemClickListener itemClickListen;

    public UnrevealAlertFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static UnrevealAlertFragment newInstance(ItemClickListener itemClickListener) {

        // instantiate click listener to pass click events to parent fragment
        itemClickListen = itemClickListener;

        UnrevealAlertFragment fragment = new UnrevealAlertFragment();
        Bundle args = new Bundle();
        //args.putParcelableArrayList(PRIVET_CONTACTS_KEY, privateContacts);
        fragment.setArguments(args);
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
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivityContext = context;
        if (context instanceof Activity){// check if fragmentContext is an activity
            activity =(Activity) context;
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // created options to select from
        CharSequence[] options = new CharSequence[]{getString(R.string.alert_dialog_edit), getString(R.string.alert_dialog_unreveal)};
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle(getString(R.string.edit_unreveal_alert_dialog_title));
        //alertDialogBuilder.setMessage("Are you sure?");

        alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Click Event for each item.
                // if first option is selected, trigger edit event
                if(i == 0){
                    Log.i(TAG, "Edit option selected");
                    /*ItemClickListener  listener = (ItemClickListener ) getTargetFragment();
                    ItemClickListener  listener = (EditNameDialogListener) getActivity();
                    listener.onClick(getView(),3,false);*/
                    if(itemClickListen != null){
                        itemClickListen.onClick(null, 3, false);
                    }
                }
                // if second option is selected, trigger confirmation dialog then un-reveal
                if(i == 1){
                    Log.i(TAG, "Unreveal option selected");
                    /*ItemClickListener  listener = (ItemClickListener ) getTargetFragment();
                    ItemClickListener  listener = (EditNameDialogListener) getActivity();
                    listener.onClick(getView(),4,false);*/
                    if(itemClickListen != null){
                        //Show confirmation dialog first
                        itemClickListen.onClick(null, 5, false);
                    }

                }
            }
        });

        return alertDialogBuilder.create();
    }

    /*@Override
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
    }*/

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
