package com.hongmei.garbagesort.declare;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DeclareInfo implements Parcelable {
    public String content;
    public String address; // 地址信息
    public ArrayList<String> photos;
    public boolean done; // 是否已完成

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.address);
        dest.writeStringList(this.photos);
        dest.writeBoolean(this.done);
    }

    public DeclareInfo() {
    }

    public DeclareInfo(String content, String address, ArrayList<String> photos, boolean done) {
        this.content = content;
        this.address = address;
        this.photos = photos;
        this.done = done;
    }

    protected DeclareInfo(Parcel in) {
        this.content = in.readString();
        this.address = in.readString();
        this.photos = in.createStringArrayList();
        this.done = in.readBoolean();
    }

    public static final Parcelable.Creator<DeclareInfo> CREATOR = new Parcelable.Creator<DeclareInfo>() {
        @Override
        public DeclareInfo createFromParcel(Parcel source) {
            return new DeclareInfo(source);
        }

        @Override
        public DeclareInfo[] newArray(int size) {
            return new DeclareInfo[size];
        }
    };
}