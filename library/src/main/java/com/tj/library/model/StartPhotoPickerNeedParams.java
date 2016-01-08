package com.tj.library.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StartPhotoPickerNeedParams implements Parcelable {
	
	private String pickerPhotoCacheSavePath;// 相册图片处理后保存在本地的路径(当前项目路径下)
	private int maxSelectCount; //最大选择上传数量

	public String getPickerPhotoCacheSavePath() {
		return pickerPhotoCacheSavePath;
	}

	public void setPickerPhotoCacheSavePath(String pickerPhotoCacheSavePath) {
		this.pickerPhotoCacheSavePath = pickerPhotoCacheSavePath;
	}

	public int getMaxSelectCount() {
		return maxSelectCount;
	}

	public void setMaxSelectCount(int maxSelectCount) {
		this.maxSelectCount = maxSelectCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(pickerPhotoCacheSavePath);
		dest.writeInt(maxSelectCount);
	}

	public StartPhotoPickerNeedParams() {
		super();
	}

	public StartPhotoPickerNeedParams(Parcel in) {
		pickerPhotoCacheSavePath = in.readString();
		maxSelectCount = in.readInt();
	}

	public static Creator<StartPhotoPickerNeedParams> getCreator() {
		return CREATOR;
	}

	public static void setCreator(
			Creator<StartPhotoPickerNeedParams> creator) {
		CREATOR = creator;
	}

	public static Creator<StartPhotoPickerNeedParams> CREATOR = new Creator<StartPhotoPickerNeedParams>() {
		public StartPhotoPickerNeedParams createFromParcel(Parcel in) {
			return new StartPhotoPickerNeedParams(in);
		}

		public StartPhotoPickerNeedParams[] newArray(int size) {
			return new StartPhotoPickerNeedParams[size];
		}
	};

	@Override
	public String toString() {
		return "StartPhotoPickerNeedParams [pickerPhotoCacheSavePath="
				+ pickerPhotoCacheSavePath + ", maxSelectCount="
				+ maxSelectCount +"]";
	}

}
