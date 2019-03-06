package com.w3engineers.unicef.telemesh.ui.chat;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.util.LiveDataTestUtil;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.telemesh.util.TestObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [13-Feb-2019 at 1:47 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [13-Feb-2019 at 1:47 PM].
 * --> <Second Editor> on [13-Feb-2019 at 1:47 PM].
 * Reviewed by :
 * --> <First Reviewer> on [13-Feb-2019 at 1:47 PM].
 * --> <Second Reviewer> on [13-Feb-2019 at 1:47 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class ChatViewModelTest {

    @Rule
    public ActivityTestRule<MainActivity> rule  = new ActivityTestRule<>(MainActivity.class);

    private AppDatabase appDatabase;

    ChatViewModel SUT;
    private RandomEntityGenerator randomEntityGenerator;

    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;

    @Before
    public void setUp() throws Exception {
        randomEntityGenerator = new RandomEntityGenerator();
        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());

        SUT = new ChatViewModel(rule.getActivity().getApplication());
    }

    @Test
    public void testMessageSetAndGet_getMessageObject_setMessage() {
        try {
            UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
            userDataSource.insertOrUpdateData(userEntity);

            String message = "Hi";

            SUT.sendMessage(userEntity.getMeshId(), message, true);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TestObserver<List<ChatEntity>> listTestObserver = LiveDataTestUtil.testObserve(SUT.getAllMessage(userEntity.getMeshId()));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SUT.prepareDateSpecificChat(listTestObserver.observedvalues.get(0));

            TestObserver<PagedList<ChatEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getChatEntityWithDate());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertThat(testObserver.observedvalues.get(0).size(), greaterThan(listTestObserver.observedvalues.get(0).size()));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertThat(((MessageEntity)listTestObserver.observedvalues.get(0).get(0)).getMessage(), is(message));

            ChatEntity receiverChat = randomEntityGenerator.createReceiverChatEntity(userEntity.getMeshId());
            messageSourceData.insertOrUpdateData(receiverChat);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SUT.updateAllMessageStatus(userEntity.getMeshId());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ChatEntity retrieveReceiverChat = messageSourceData.getMessageEntityById(receiverChat.getMessageId());
            assertThat(retrieveReceiverChat.getStatus(), is(Constants.MessageStatus.STATUS_READ));

            userEntity.setOnline(false);
            userDataSource.insertOrUpdateData(userEntity);

            TestObserver<UserEntity> entityTestObserver = LiveDataTestUtil.testObserve(SUT.getUserById(userEntity.getMeshId()));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            assertFalse(userEntity.isOnline());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Test
    public void testPrepareDateSpecificChat_checkListSize_setListOfMessage() throws Exception {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        ChatEntity receiverChat = randomEntityGenerator.createReceiverChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(receiverChat);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TestObserver<List<ChatEntity>> listTestObserver = LiveDataTestUtil
                .testObserve(SUT.getAllMessage(userEntity.getMeshId()));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SUT.prepareDateSpecificChat(listTestObserver.observedvalues.get(0));

        TestObserver<PagedList<ChatEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getChatEntityWithDate());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(testObserver.observedvalues.get(0).size(), greaterThan(listTestObserver.observedvalues.get(0).size()));
    }*/

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }
}