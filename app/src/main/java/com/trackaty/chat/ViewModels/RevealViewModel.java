package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.trackaty.chat.DataSources.RelationRepository;
import com.trackaty.chat.DataSources.UserRepository;
import com.trackaty.chat.models.Relation;
import com.trackaty.chat.models.Social;
import com.trackaty.chat.models.SocialObj;
import com.trackaty.chat.models.User;

import java.util.ArrayList;

public class RevealViewModel extends ViewModel {

    private final static String TAG = RevealViewModel.class.getSimpleName();
    private ArrayList<Social> mContacts;
    private String mRelationStatus;
    private  static final int SECTION_SOCIAL_REQUEST = 100;
    private  static final int SECTION_SOCIAL_APPROVE = 200;

    public RevealViewModel() {

        Log.d(TAG, "RevealViewModel init");
        mContacts = new ArrayList<>();
    }


    public void setContacts(ArrayList<Social> contacts) {
        // We need to create a new contacts arrayList because we don't want to make changes to the original contacts in ProfileFragment
        for (int i = 0; i < contacts.size(); i++) {
            Log.d(TAG, "RevealViewModel setContacts() loop = "+ contacts.get(i).getKey() + " value= "+ contacts.get(i).getValue().getPublic()+ " Section= "+contacts.get(i).getSection());
            SocialObj socialObj = new SocialObj(String.valueOf(contacts.get(i).getKey()), (Boolean) contacts.get(i).getValue().getPublic());
            mContacts.add(new Social(String.valueOf(contacts.get(i).getKey()) ,socialObj, contacts.get(i).getSection()+i , contacts.get(i).getSection()));
        }
    }

    public void setRelationStatus(String relationStatus) {
        mRelationStatus = relationStatus;
    }

    public ArrayList<Social> getContacts() {
        return mContacts;
    }

    public String getRelationStatus() {
        return mRelationStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "RevealViewModel onCleared:");
    }
}
