package com.w3engineers.unicef.telemesh.data.local.feed;

import android.widget.Toast;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;

public class FeedDataSource {

    private FeedDao feedDao;
    private static FeedDataSource feedDataSource = new FeedDataSource();

    //ExecutorService is a construct that allows you to pass a task to be executed by a thread asynchronously.
    private ExecutorService mIoExecutor;

    private FeedDataSource() {
        feedDao = AppDatabase.getInstance().feedDao();
        mIoExecutor = Executors.newSingleThreadExecutor();
    }



    public static FeedDataSource getInstance() {
        return feedDataSource;
    }

    public long insertOrUpdateData(FeedEntity feedEntity) {

        Callable<Long> insertCallable = () -> feedDao.insertFeed(feedEntity);

        try {
            return mIoExecutor.submit(insertCallable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }

    }

    public Flowable<List<FeedEntity>> getAllFeed() {
        return feedDao.getAllFeed();
    }




}
