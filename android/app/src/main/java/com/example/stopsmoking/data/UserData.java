package com.example.stopsmoking.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserData implements Parcelable{

    String userId;
    String name;
    int yellowCard;
    boolean userAuth;
    String registeredDate;
    public UserData(){

    }


    protected UserData(Parcel in) {
        userId = in.readString();
        name = in.readString();
        yellowCard = in.readInt();
        userAuth = in.readByte() != 0;
        registeredDate = in.readString();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYellowCard() {
        return yellowCard;
    }

    public void setYellowCard(int yellowCard) {
        this.yellowCard = yellowCard;
    }

    public boolean isUserAuth() {
        return userAuth;
    }

    public void setUserAuth(boolean userAuth) {
        this.userAuth = userAuth;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeInt(yellowCard);
        dest.writeByte((byte) (userAuth ? 1 : 0));
        dest.writeString(registeredDate);
    }
}