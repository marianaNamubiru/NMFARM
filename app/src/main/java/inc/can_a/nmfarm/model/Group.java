package inc.can_a.nmfarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Group implements Parcelable {

    @SerializedName("gid")
    String gid;
    @SerializedName("owner_id")
    String owner_id;
    @SerializedName("title")
    String  title;
    @SerializedName("image")
    String  image;
    @SerializedName("created_on")
    String created_on;

    public Group(String owner_id, String title,String image, String created_on) {

        this.owner_id = owner_id;
        this.title = title;
        this.image = image;
        this.created_on = created_on;
    }


    protected Group(Parcel in) {
        gid = in.readString();
        owner_id = in.readString();
        title = in.readString();
        image = in.readString();
        created_on = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gid);
        dest.writeString(owner_id);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(created_on);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
