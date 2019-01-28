package com.w3engineers.unicef.telemesh.ui.chat;

import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityChatBinding;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.userprofile.UserProfileActivity;

import java.util.List;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/10/2018 at 10:54 AM.
 *  *
 *  * Purpose: One to one messaging page
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/10/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class ChatActivity extends RmBaseActivity implements ItemClickListener<ChatEntity> {
    /**
     * <h1>Instance variable scope</h1>
     */
    private ChatViewModel mChatViewModel;
    private UserEntity mUserEntity;
    //private ChatAdapter mChatAdapter;
    private  ChatPagedAdapter mChatPagedAdapter;
    private ActivityChatBinding mViewBinging;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar_chat;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    @Override
    protected void startUI() {
        Intent intent = getIntent();
        mUserEntity = intent.getParcelableExtra(UserEntity.class.getName());

        mViewBinging = (ActivityChatBinding) getViewDataBinding();
        setTitle("");
        initComponent();
        subscribeForMessages();
        subscribeForUserEvent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewBinging.imageProfile.setOnClickListener(this);
        mViewBinging.textViewLastName.setOnClickListener(this);

        mViewBinging.setUserEntity(mUserEntity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mUserEntity != null) {
            mChatViewModel.updateAllMessageStatus(mUserEntity.meshId);
            mChatViewModel.setCurrentUser(mUserEntity.meshId);
        }
    }

    @Override
    protected BaseServiceLocator getServiceLocator() {
        return ServiceLocator.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mChatViewModel.setCurrentUser(null);
    }

    /**
     * <h1>Init view model and recycler view</h1>
     * <p>Init adapter and listener</p>
     */
    private void initComponent() {

        mChatPagedAdapter = new ChatPagedAdapter(this);
        mViewBinging.chatRv.setAdapter(mChatPagedAdapter);


       /* mChatAdapter = new ChatAdapter(this);
        mViewBinging.chatRv.setAdapter(mChatAdapter);
        mChatAdapter.setItemClickListener(this);*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // to load messages from reverse order as in chat view
        linearLayoutManager.setStackFromEnd(true);
        mViewBinging.chatRv.setLayoutManager(linearLayoutManager);


      //  mViewBinging.emptyViewId.setOnClickListener(this);
        mViewBinging.imageViewSend.setOnClickListener(this);

        mChatViewModel = getViewModel();

        clearNotification();
    }

    /**
     * Remove current user notification
     */
    private void clearNotification() {
        int notificationId = Math.abs(mUserEntity.meshId.hashCode());
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    /**
     * Using LiveData observer here we will observe the messages
     * from local db.
     * if any new message get inserted into db with the help of LifeData
     * here we can listen that message
     */
    private void subscribeForMessages() {
        if (mUserEntity != null) {
            /*mChatViewModel.getAllMessage(mUserEntity.meshId).observe(this, chatEntities -> {

                if (chatEntities == null)
                    return;

//                Collections.reverse(chatEntities);
                mChatAdapter.addItems(chatEntities);
                mViewBinging.chatRv.scrollToPosition(mChatAdapter.getItemCount() - 1);
            });*/


            mChatViewModel.getAllMessage(mUserEntity.meshId).observe(this, new Observer<List<ChatEntity>>() {
                @Override
                public void onChanged(@Nullable List<ChatEntity> chatEntities) {

                    mChatViewModel.prepareDateSpecificChat(chatEntities).observe(ChatActivity.this, new Observer<PagedList<ChatEntity>>() {
                        @Override
                        public void onChanged(@Nullable PagedList<ChatEntity> chatEntities) {
                            mChatPagedAdapter.submitList(chatEntities);
                        }
                    });

                }
            });
        }
    }


    private void subscribeForUserEvent() {
        if (mUserEntity != null) {
            mChatViewModel.getUserById(mUserEntity.meshId).observe(this, userEntity -> {
                mUserEntity = userEntity;
                if (userEntity != null)
                    mViewBinging.setUserEntity(userEntity);
            });
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_send:
                String value = mViewBinging.editTextMessage.getText().toString().trim();
                if (TextUtils.isEmpty(value)) return;
                mChatViewModel.sendMessage(mUserEntity.meshId, value, true);
                mViewBinging.editTextMessage.setText("");
                break;
            case R.id.image_profile:
            case R.id.text_view_last_name:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(UserEntity.class.getName(), mUserEntity);
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            loadMainActivity();
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void loadMainActivity() {
        Intent newTask = new Intent(this, MainActivity.class);
        newTask.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newTask);
    }

    @Override
    public void onItemClick(View view, ChatEntity item) {
    }

    private ChatViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

                return (T) ServiceLocator.getInstance().getChatViewModel();
            }
        }).get(ChatViewModel.class);
    }
}
