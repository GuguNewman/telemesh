package com.w3engineers.unicef.telemesh.ui.messagefeed;


import android.widget.Toast;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;

import java.util.Date;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [11-Oct-2018 at 12:28 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [11-Oct-2018 at 12:28 PM].
 * * --> <Second Editor> on [11-Oct-2018 at 12:28 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [11-Oct-2018 at 12:28 PM].
 * * --> <Second Reviewer> on [11-Oct-2018 at 12:28 PM].
 * * ============================================================================
 **/
public class MessageFeedViewModel extends BaseRxViewModel {

    private FeedDataSource mFeedDataSource;

    public MessageFeedViewModel(FeedDataSource feedDataSource) {
        this.mFeedDataSource = feedDataSource;

        getMayBeObserver();
    }



    public void insertFeed(){

        FeedEntity feedEntity = new FeedEntity();
        feedEntity.setFeedId("0x34567");
        feedEntity.setFeedProviderName("Unicef");
        feedEntity.setFeedTitle("What is Lorem Ipsum?");
        feedEntity.setFeedDetail("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.");

        feedEntity.setFeedTime(new Date());

        mFeedDataSource.insertOrUpdateData(feedEntity);

    }


    public void broadcastMessageTest(){

        byte [] msg = "HelloWorld".getBytes();
        RightMeshDataSource.getRmDataSource().broadcastMessage(msg);
    }

    //receive broadcast message here by the Listener/observer
    public MaybeObserver<String> getMayBeObserver(){

        return new MaybeObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String s) {
                Toast.makeText(TeleMeshApplication.getContext(), "Msg: " + s,  Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

}
