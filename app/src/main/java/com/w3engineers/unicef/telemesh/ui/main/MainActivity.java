package com.w3engineers.unicef.telemesh.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.viper.application.data.BaseServiceLocator;
import com.w3engineers.ext.viper.application.ui.base.rm.RmBaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedCallBackToUI;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMainBinding;
import com.w3engineers.unicef.telemesh.databinding.NotificationBadgeBinding;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactsFragment;
import com.w3engineers.unicef.telemesh.ui.messagefeed.MessageFeedFragment;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsFragment;
import com.w3engineers.unicef.telemesh.ui.survey.SurveyFragment;

public class MainActivity extends RmBaseActivity implements NavigationView.OnNavigationItemSelectedListener, FeedCallBackToUI {
    private ActivityMainBinding binding;
    private BottomNavigationMenuView bottomNavigationMenuView;
    private MainActivityViewModel mViewModel;
    private ServiceLocator serviceLocator;
    private boolean doubleBackToExitPressedOnce = false;
    public static MainActivity mainActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    @Override
    protected void startUI() {
        mainActivity = this;
        binding = (ActivityMainBinding) getViewDataBinding();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        initBottomBar();
        mViewModel = getViewModel();
        //when  counting need to add
        /*
        mViewModel.getMessageCount().observe(this, messageCount -> {
            //call createBadgeCount() put necessary params (count, position)
        });
        mViewModel.getSurveyCount().observe(this, surveyCount ->{
            //call createBadgeCount() put necessary params (count, position)
        });*/

        mViewModel.makeSendingMessageAsFailed();

    }

    private MainActivityViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                serviceLocator = ServiceLocator.getInstance();
                serviceLocator.setFeedCallBack(MainActivity.this);
                return (T) serviceLocator.getMainActivityViewModel();
            }
        }).get(MainActivityViewModel.class);
    }

    private void initBottomBar() {
        loadFragment(new MeshContactsFragment(), getString(R.string.title_contacts_fragment));

        bottomNavigationMenuView =
                (BottomNavigationMenuView) binding.bottomNavigation
                        .getChildAt(Constants.MenuItemPosition.POSITION_FOR_CONTACT);

        /*binding.bottomNavigation
                .setIconSize(Constants.MenuItemPosition.MENU_ITEM_WIDTH
                        , Constants.MenuItemPosition.MENU_ITEM_HEIGHT);
        binding.bottomNavigation.enableShiftingMode(false);
        binding.bottomNavigation.enableItemShiftingMode(false);
        binding.bottomNavigation.enableAnimation(false);*/

/*
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);
        addBadgeToBottomBar(Constants.MenuItemPosition.POSITION_FOR_SURVEY);
*/


        //its for checking. must be removed later
 /*       createBadgeCount(6, Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);
        createBadgeCount(12, Constants.MenuItemPosition.POSITION_FOR_SURVEY);*/

    }

    // Again this api will be enable when its functionality will be added
    /*private void addBadgeToBottomBar(int menuItemPosition) {
        NotificationBadgeBinding notificationBadgeBinding = NotificationBadgeBinding.inflate(getLayoutInflater());
        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);
        if (itemView != null) {
            itemView.addView(notificationBadgeBinding.getRoot());
        }
    }*/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() > Constants.DefaultValue.NEG_INTEGER_ONE) {
            setFragmentsOnPosition(item);
        }
        return true;
    }

    public void setFragmentsOnPosition(MenuItem item) {
        Fragment mFragment = null;
        String toolbarTitle = "";
        switch (item.getItemId()) {
            case R.id.action_contact:
                toolbarTitle = getString(R.string.title_contacts_fragment);
                mFragment = new MeshContactsFragment();
                break;
            case R.id.action_message_feed:
                toolbarTitle = getString(R.string.title_message_feed_fragment);
              /*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_MESSAGE_FEED);*/
                mFragment = new MessageFeedFragment();
                break;
            case R.id.action_survey:
                toolbarTitle = getString(R.string.title_survey_fragment);
              /*  createBadgeCount(Constants.DefaultValue.INTEGER_VALUE_ZERO
                        , Constants.MenuItemPosition.POSITION_FOR_SURVEY);*/
                mFragment = new SurveyFragment();
                break;
            case R.id.action_setting:
                toolbarTitle = getString(R.string.title_settings_fragment);
                mFragment = new SettingsFragment();
                break;
        }
        if (mFragment != null && !toolbarTitle.equals("")) {
            loadFragment(mFragment, toolbarTitle);
        }
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(title);
    }

    // Again this api will be enable when its functionality will be added
    /*public void createBadgeCount(int latestCount, int menuItemPosition) {

        BottomNavigationItemView itemView =
                (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(menuItemPosition);

        if (itemView == null) {
            return;
        }
        ConstraintLayout constraintLayoutContainer = itemView.findViewById(R.id.constraint_layout_badge);
        TextView textViewBadgeCount = itemView.findViewById(R.id.text_view_badge_count);

        if (latestCount > Constants.DefaultValue.INTEGER_VALUE_ZERO) {
            if (latestCount <= Constants.DefaultValue.MAXIMUM_BADGE_VALUE) {
                textViewBadgeCount.setText(String.valueOf(latestCount));
            } else {
                textViewBadgeCount.setText(R.string.badge_count_more_than_9);
            }
        } else {
            constraintLayoutContainer.setVisibility(View.GONE);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.userOfflineProcess();
    }

    @Override
    protected BaseServiceLocator getServiceLocator() {
        return serviceLocator;
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toaster.showShort(getString(R.string.double_press_exit));
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, Constants.DefaultValue.DOUBLE_PRESS_INTERVAL);
    }

    @Override
    public void sendToUi(String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }
}
