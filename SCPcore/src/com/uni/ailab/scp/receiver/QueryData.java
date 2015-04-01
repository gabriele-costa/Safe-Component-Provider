package com.uni.ailab.scp.receiver;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabriele on 01/04/15.
 */
public class QueryData implements Parcelable {

    // TODO: should include IDs
    public String query;

    public QueryData(String q){
        query = q;
    }

    public QueryData(Parcel in){
        query = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
    }
}
