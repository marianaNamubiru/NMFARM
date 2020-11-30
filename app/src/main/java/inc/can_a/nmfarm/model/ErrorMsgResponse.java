package inc.can_a.nmfarm.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ErrorMsgResponse {

        @SerializedName("error")
        private String error;
        @SerializedName("msg")
        private String message;
        @SerializedName("chat_rooms")
        private ArrayList<ChatRoom> chatRooms;
        @SerializedName("messages")
        private ArrayList<Message> messages;
        @SerializedName("chat_room")
        private ChatRoom chatRoom;
        @SerializedName("message")
        private Message messageObj;
        @SerializedName("user")
        private User user;
        @SerializedName("feeds")
        private ArrayList<Feeds> feeds;
        @SerializedName("groups")
        private ArrayList<Group> groups;
        @SerializedName("members")
        private ArrayList<GroupMember> groupMembers;

        public String getError() {
            return error;
        }
        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<ChatRoom> getChatRooms() {
            return chatRooms;
        }
        public void setChatRooms(ArrayList<ChatRoom> chatRooms) {
            this.chatRooms = chatRooms;
        }

        public ArrayList<Message> getMessages() {
            return messages;
        }
        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        public ChatRoom getChatRoom() {
            return chatRoom;
        }
        public void setChatRoom(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
        }

        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }

        public Message getMessageObj() {
            return messageObj;
        }

        public void setMessageObj(Message messageObj) {
            this.messageObj = messageObj;
        }

        public ArrayList<Feeds> getFeeds() {
            return feeds;
        }

        public void setFeeds(ArrayList<Feeds> feeds) {
            this.feeds = feeds;
        }

        public ArrayList<Group> getGroups() {
            return groups;
        }

        public void setGroups(ArrayList<Group> groups) {
            this.groups = groups;
        }

        public ArrayList<GroupMember> getGroupMembers() {
            return groupMembers;
        }

        public void setGroupMembers(ArrayList<GroupMember> groupMembers) {
            this.groupMembers = groupMembers;
        }
}
