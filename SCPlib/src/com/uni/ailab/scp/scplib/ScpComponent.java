package com.uni.ailab.scp.scplib;


import android.os.Parcel;
import android.os.Parcelable;

public class ScpComponent implements Parcelable{
	private String name;
	private String type;
	private String[] policies;
	private String[] permissions;
	private String[] actions;
	
	public ScpComponent(){
		super();
	}
	
	public ScpComponent(Parcel in){
		this.name = in.readString();
		this.type = in.readString();
		this.policies = new String[in.readInt()];
		in.readStringArray(this.policies);
		this.permissions = new String[in.readInt()];
		in.readStringArray(this.permissions);
		this.actions = new String[in.readInt()];
		in.readStringArray(this.actions);
	}
	
	public ScpComponent(String n, String t, String[] po, String[] pe){
		name = n;
		type = t;
		policies = po;
		permissions = pe;
		actions = new String[0];
	}
	
	public ScpComponent(String n, String t, String[] po, String[] pe, String[] ac){
		name = n;
		type = t;
		policies = po;
		permissions = pe;
		actions = ac;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String[] getPolicies() {
		return policies;
	}

	public void setPolicies(String[] policy) {
		this.policies = policy;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}
	
	public String[] getActions() {
		return actions;
	}

	public void setActions(String[] actions) {
		this.actions = actions;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
		dest.writeInt(policies.length);
        dest.writeStringArray(policies);
		dest.writeInt(permissions.length);
	    dest.writeStringArray(permissions);
		dest.writeInt(actions.length);
		dest.writeStringArray(actions);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<ScpComponent> CREATOR = new Parcelable.Creator<ScpComponent>() {
		@Override
		public ScpComponent createFromParcel(Parcel in) {
			return new ScpComponent(in);
		}

		@Override
		public ScpComponent[] newArray(int size) {
			return new ScpComponent[size];
		}
	};

}
