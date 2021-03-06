package inc.can_a.nmfarm.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class Message implements Serializable {
    @SerializedName("message_id")
    String id;
    @SerializedName("message")
    String message;
    @SerializedName("image")
    String image;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("user")
    User user;

    public Message() {
    }

    public Message(String id, String message, String createdAt, User user) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
