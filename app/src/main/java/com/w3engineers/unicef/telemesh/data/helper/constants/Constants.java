package com.w3engineers.unicef.telemesh.data.helper.constants;

import android.os.Environment;

import java.io.File;

public class Constants {


    //bottom navigation item position
    public interface MenuItemPosition {
        int POSITION_FOR_CONTACT = 0;
        int POSITION_FOR_MESSAGE_FEED = 1;
        int POSITION_FOR_SURVEY = 2;
        int POSITION_FOR_SETTINGS = 3;
        float MENU_ITEM_WIDTH = 50f;
        float MENU_ITEM_HEIGHT = 50f;
        int MAXIMUM_MENU_ITEMS = 4;
    }

    public interface DefaultValue {
        int INTEGER_VALUE_ZERO = 0;
        int MAXIMUM_BADGE_VALUE = 9;
        int NEG_INTEGER_ONE = -1;
        int DELAY_INTERVAL = 3000;
        int DOUBLE_PRESS_INTERVAL = 2000;
        int MINIMUM_TEXT_LIMIT = 2;
        int MAXIMUM_TEXT_LIMIT = 20;
    }

    public interface ButtonOpacity {
        float DISABLE_EFFECT = 0.5f;
        float ENABLE_EFFECT = 1.0f;
    }

    public interface preferenceKey {
        String FIRST_NAME = "first_name";
        String LAST_NAME = "last_name";
        String IMAGE_INDEX = "image_index";
        String IS_USER_REGISTERED = "user_registered";
        String IS_NOTIFICATION_ENABLED = "notification_enable";
        String APP_LANGUAGE = "app_language";
        String APP_LANGUAGE_DISPLAY = "app_language_display";
    }

    public interface drawables {
        String AVATER_IMAGE = "avatar";
        String AVATER_DRAWABLE_DIRECTORY = "mipmap";
    }

    public interface MessageStatus {
        int STATUS_UNREAD = 1;
        int STATUS_READ = 2;
        int STATUS_SENDING = 3;
        int STATUS_DELIVERED = 4;
        int STATUS_FAILED = 5;
    }

    public interface DataType {
        //RM data type
        int USER = 1;
        int MESSAGE = 2;
        int SURVEY = 3;
        int MESSAGE_FEED = 4;
        int BROADCAST_MESSAGE = 5;
        int SUPER_PEER_MESSAGE = 70;
    }


    public interface MessageType {
        int TEXT_MESSAGE = 100;
        int DATE_MESSAGE = 101;
    }

    public interface UserStatus {
        int OFFLINE = 0;
        int ONLINE = 1;
    }

    public interface Directory {
        String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        String ROOT_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/TeleMesh/";
        String FEED_PROVIDER_LOGO_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/TeleMesh/.feed_provide_logo/";
    }
}
