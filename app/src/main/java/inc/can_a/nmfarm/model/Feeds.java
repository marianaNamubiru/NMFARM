package inc.can_a.nmfarm.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Feeds implements Parcelable {
    //[{"user_id":32,"title":"Test feed","description":"Test feed","created_on":"Sample test desciption"}]
    @SerializedName("user_id")
    String user_id;
    @SerializedName("title")
    String  title;
    @SerializedName("description")
    String  description;
    @SerializedName("image")
    String  image;
    @SerializedName("created_on")
    String created_on;

    public Feeds(String user_id, String title, String description, String image, String created_on) {

        this.user_id = user_id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.created_on = created_on;
    }


    protected Feeds(Parcel in) {
        user_id = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        created_on = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(created_on);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Feeds> CREATOR = new Creator<Feeds>() {
        @Override
        public Feeds createFromParcel(Parcel in) {
            return new Feeds(in);
        }

        @Override
        public Feeds[] newArray(int size) {
            return new Feeds[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
