package com.karam.visitaobra;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    String link,description,id;
    int seq;

    protected Photo(Parcel in) {
        link = in.readString();
        description = in.readString();
        id = in.readString();
        seq = in.readInt();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Photo(String id, String link, String description, int seq) {
        this.id = id;
        this.link = link;
        this.description = description;
        this.seq = seq;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeInt(seq);
    }
}
