package inc.can_a.nmfarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class HowToDo implements Parcelable {

//    @SerializedName("gid")
//    String gid;
    @SerializedName("crop")
    String crop;
    @SerializedName("title")
    String title;
    @SerializedName("details")
    String  details;

    public HowToDo(String crop, String title, String details) {

        this.crop = crop;
        this.title = title;
        this.details = details;
    }


    protected HowToDo(Parcel in) {
        crop = in.readString();
        title = in.readString();
        details = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(crop);
        dest.writeString(title);
        dest.writeString(details);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HowToDo> CREATOR = new Creator<HowToDo>() {
        @Override
        public HowToDo createFromParcel(Parcel in) {
            return new HowToDo(in);
        }

        @Override
        public HowToDo[] newArray(int size) {
            return new HowToDo[size];
        }
    };


    public String getCrop() {
        return crop;
    }
    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
}
