package com.w3engineers.unicef.telemesh.ui.chat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.pager.ChatEntityListDataSource;
import com.w3engineers.unicef.telemesh.pager.MainThreadExecutor;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/10/2018 at 10:54 AM.
 *  *
 *  * Purpose: Perform message related operations
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/10/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class ChatViewModel extends AndroidViewModel {
    /**
     * <h1>Instance variable scope</h1>
     */

    private MessageSourceData messageSourceData;
    private UserDataSource userDataSource;
    private DataSource dataSource;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 70;
    private static final int PREFETCH_DISTANCE = 30;


    private CompositeDisposable compositeDisposable;
    private  MutableLiveData<PagedList<ChatEntity>> mutableMovieList = new MutableLiveData<>();




    /**
     * <h1>View model constructor</h1>
     *
     *
     */
    public ChatViewModel(Application application) {
        super(application);
        this.messageSourceData =  MessageSourceData.getInstance();

        compositeDisposable = new CompositeDisposable();
        userDataSource  = UserDataSource.getInstance();
        dataSource = Source.getDbSource();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    /**
     * <h1>Create a stream pipeline to database</h1>
     *
     * @param meshId : friends user id
     * @return : list of message
     */
    public LiveData<List<ChatEntity>> getAllMessage(String meshId) {
        return LiveDataReactiveStreams.fromPublisher(messageSourceData.getAllMessages(meshId));
    }

    /**
     * <h1>Init chatting user </h1>
     * <p>To recognize which message need to pass UI</p>
     * <p>Track read or unread message</p>
     *
     * @param userId : UserEntity obj
     */
    public void setCurrentUser(@Nullable String userId) {
        dataSource.setCurrentUser(userId);
    }

    /**
     * <h1>Send message on IO thread</h1>
     *
     * @param meshId        : Friends user id
     * @param message       : Message need to send
     * @param isTextMessage : is text or file
     */
    public void sendMessage(@NonNull String meshId, @NonNull String message, boolean isTextMessage) {


        if (isTextMessage) {

            MessageEntity messageEntity = new MessageEntity()
                    .setMessage(message);


            ChatEntity chatEntity = prepareChatEntityForText(meshId, messageEntity);

            messageInsertionProcess(chatEntity);
        }
    }

    /**
     * This method will take steps to insert chat entity to local db.
     * we didn't call to ui for update a message because
     * we use live data for continuous integrating chat messages
     * @param chatEntity : prepared chat data
     */

    @SuppressLint("CheckResult")
    private void messageInsertionProcess(ChatEntity chatEntity) {

        compositeDisposable.add(insertMessageData((MessageEntity) chatEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

    }

    private Single<Long> insertMessageData(MessageEntity messageEntity) {
        return Single.fromCallable(() ->
                messageSourceData.insertOrUpdateData(messageEntity));
    }

    /**
     * Prepare a chat entity from text message.
     * @param meshId
     * @param messageEntity
     */
    private ChatEntity prepareChatEntityForText(String meshId, MessageEntity messageEntity) {


        ChatEntity chatEntity;
        chatEntity = messageEntity;

        chatEntity.setMessageId(UUID.randomUUID().toString())
                .setFriendsId(meshId)
                .setIncoming(false)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE);


        return chatEntity;
    }

    /**
     * Mark all un read message as read
     *
     * @param friendsId : mesh id
     */
    public void updateAllMessageStatus(@NonNull String friendsId) {


        compositeDisposable.add(updateMessageSatus(friendsId)
                .subscribeOn(Schedulers.io()).subscribe());
    }

    private Single<Long> updateMessageSatus(String friendsId) {
        return Single.fromCallable(() -> messageSourceData.updateUnreadToRead(friendsId));
    }

    /**
     * This API concerned for tracking offline and online the chat current chat user
     * @param meshId
     * @return
     */
    @NonNull
    public LiveData<UserEntity> getUserById(@NonNull String meshId){
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getUserById(meshId));
    }

    @NonNull
    public LiveData<PagedList<ChatEntity>> getChatEntityWithDate() {
        return mutableMovieList;
    }

    /**
     * chunk by chunk data load will be applicable.
     * From db all the chat entity will be fetched
     * but we will not display all messages at a time.
     * Custom Paging will load chunk data and update view.
     *
     * @param chatEntityList
     * @return
     */
    public void prepareDateSpecificChat(@NonNull List<ChatEntity> chatEntityList) {

        List<ChatEntity> chatList = groupDataIntoHashMap(chatEntityList);

        ChatEntityListDataSource chatEntityListDataSource = new ChatEntityListDataSource(chatList);

        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build();


        PagedList<ChatEntity> pagedStrings = new PagedList.Builder<Integer, ChatEntity>(chatEntityListDataSource, myConfig)
                .setInitialKey(INITIAL_LOAD_KEY)
                .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        // here mutable live data is used to pass the updated value
        mutableMovieList.postValue(pagedStrings);
    }


    /**
     * group chat entities according to date.
     *  Set is used to ensure the list contain unique contents.
     * @param chatEntities
     * @return
     */
    private List<ChatEntity> groupDataIntoHashMap(List<ChatEntity> chatEntities) {

        LinkedHashMap<Long, Set<ChatEntity>> groupedHashMap = new LinkedHashMap<>();
        Set<ChatEntity> chatEntitySet = null;

        for (ChatEntity chatEntity : chatEntities) {

            if(chatEntity!= null){

                long hashMapKey = chatEntity.getTime();

                if (groupedHashMap.containsKey(hashMapKey)) {
                    // The key is already in the HashMap; add the pojo object
                    // against the existing key.
                    groupedHashMap.get(hashMapKey).add(chatEntity);
                } else {
                    // The key is not there in the HashMap; create a new key-value pair
                    chatEntitySet = new LinkedHashSet<>();
                    chatEntitySet.add(chatEntity);
                    groupedHashMap.put(hashMapKey, chatEntitySet);
                }
            }


        }

        //Generate list from map
        return generateListFromMap(groupedHashMap);

    }


    private List<ChatEntity> generateListFromMap(LinkedHashMap<Long, Set<ChatEntity>> groupedHashMap) {
        // We linearly add every item into the consolidatedList.
        Date date1 = null;
        Date date2 = null;


        date1 = TimeUtil.getInstance().getDateFromMillisecond(TimeUtil.DEFAULT_MILLISEC);



        List<ChatEntity> consolidatedList = new ArrayList<>();

        for (long millisec : groupedHashMap.keySet()) {


            date2 = TimeUtil.getInstance().getDateFromMillisecond(millisec);

            if(!TimeUtil.getInstance().isSameDay(date1, date2)){

                date1 = date2;

                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setMessageType(Constants.MessageType.DATE_MESSAGE);
                messageEntity.setTime(millisec);
                consolidatedList.add(messageEntity);

            }

            for (ChatEntity chatEntity : groupedHashMap.get(millisec)) {

                consolidatedList.add(chatEntity);
            }

        }

        return consolidatedList;
    }




}
