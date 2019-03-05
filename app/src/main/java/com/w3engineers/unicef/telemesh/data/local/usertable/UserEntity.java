package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.util.collections.Matchable;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.TeleMeshUser.*;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;


@Entity(tableName = TableNames.USERS, indices = {@Index(value = {ColumnNames.COLUMN_USER_MESH_ID}, unique = true)})
public class UserEntity extends DbBaseEntity implements Matchable<String>, Cloneable {

    /************************************************************/
    /*************** User table property ************************/
    /************************************************************/

    @NonNull
    @ColumnInfo(name = ColumnNames.COLUMN_USER_MESH_ID)
    public String meshId;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_CUSTOM_ID)
    public String customId;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_FIRST_NAME)
    public String userFirstName;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_LAST_NAME)
    public String userLastName;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_AVATAR)
    public int avatarIndex;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_LAST_ONLINE_TIME)
    public long lastOnlineTime;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_IS_ONLINE)
    public boolean isOnline;

    public int hasUnreadMessage;

    /**************************************************************/
    /****************** Only user model property ******************/
    /**************************************************************/
    //@Ignore
    //private String userLastName;
    public UserEntity() {
    }

    @NonNull
    public String getMeshId() {
        return meshId;
    }

    public UserEntity setMeshId(@NonNull String meshId) {
        this.meshId = meshId;
        return this;
    }

    public String getCustomId() {
        return customId;
    }

    public UserEntity setCustomId(String customId) {
        this.customId = customId;
        return this;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public UserEntity setUserFirstName(String userName) {
        this.userFirstName = userName;
        return this;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public UserEntity setUserLastName(String userName) {
        this.userLastName = userName;
        return this;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public UserEntity setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
        return this;
    }

    public long getLastOnlineTime() {
        return lastOnlineTime;
    }

    public UserEntity setLastOnlineTime(long lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public UserEntity setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userFirstName);
        dest.writeString(this.userLastName);
        dest.writeString(this.meshId);
        dest.writeString(this.customId);
        dest.writeInt(this.avatarIndex);
        dest.writeLong(this.lastOnlineTime);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeInt(this.hasUnreadMessage);
    }

    protected UserEntity(Parcel in) {
        super(in);
        this.userFirstName = in.readString();
        this.userLastName = in.readString();
        this.meshId = in.readString();
        this.customId = in.readString();
        this.avatarIndex = in.readInt();
        this.lastOnlineTime = in.readLong();
        this.isOnline = in.readByte() != 0;
        this.hasUnreadMessage = in.readInt();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    /**
     * <h1>Build user proto data</h1>
     *
     * @return : byte[]
     *//*
    public static byte[] toProtoUser() {
        Context context = TeleMeshApplication.getContext();
        SharedPref sharedPref = SharedPref.getSharedPref(context);
        return RMUserModel.newBuilder()
                .setUserFirstName(sharedPref.read(Constants.preferenceKey.FIRST_NAME))
                .setUserLastName(sharedPref.read(Constants.preferenceKey.LAST_NAME))
                .setImageIndex(sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX))
                .build().toByteArray();

    }*/

    public RMUserModel getProtoUser() {
        return RMUserModel.newBuilder()
                .setUserFirstName(getUserFirstName())
                .setUserLastName(getUserLastName())
                .setImageIndex(getAvatarIndex())
                .build();
    }

    // if lots of similar task holds in entity then ti should be used in util class
    public String getFullName() {
        return userFirstName + " " + userLastName;
    }

    public UserEntity toUserEntity(RMUserModel rmUserModel) {
        return setUserFirstName(rmUserModel.getUserFirstName())
                .setUserLastName(rmUserModel.getUserLastName())
                .setAvatarIndex(rmUserModel.getImageIndex())
                .setMeshId(rmUserModel.getUserId());
    }

    @Override
    public String getMatcher() {
        return meshId;
    }
}