package com.hongmei.garbagesort.declare;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DeclareInfo implements Parcelable {
    public String content;
    public ArrayList<String> photos;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeStringList(this.photos);
    }

    public DeclareInfo() {
    }

    public DeclareInfo(String content, ArrayList<String> photos) {
        this.content = content;
        this.photos = photos;
    }

    protected DeclareInfo(Parcel in) {
        this.content = in.readString();
        this.photos = in.createStringArrayList();
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