package com.trackaty.chat.ViewModels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.trackaty.chat.DataSources.ReportRepository;
import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

public class ReportViewModel extends ViewModel {

    private final static String TAG = ReportViewModel.class.getSimpleName();

    private ReportRepository mReportRepository;


    public ReportViewModel() {
        Log.d(TAG, "RevealViewModel init");
        mReportRepository = new ReportRepository();

    }

    public void sendReport(String userId, String currentUserId, User user, User currentUser, String issue) {
        // send profile report
        mReportRepository.sendReport(userId, currentUserId, user , currentUser, issue);
    }

    public void sendReport(String userId, String currentUserId, User user, User currentUser, String issue, Message message) {
        // send message report
        mReportRepository.sendReport(userId, currentUserId, user , currentUser, issue, message);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "RevealViewModel onCleared:");
    }


}
