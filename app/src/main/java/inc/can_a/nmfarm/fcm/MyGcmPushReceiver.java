/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package inc.can_a.nmfarm.fcm;

//import com.google.android.gms.gcm.GcmListenerService;


//public class MyGcmPushReceiver extends GcmListenerService {
//
//    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
//
//    private NotificationUtils notificationUtils;
//
//    /**
//     * Called when owner is received.
//     *
//     * @param from   SenderID of the sender.
//     * @param bundle Data bundle containing owner data as key/value pairs.
//     *               For Set of keys use data.keySet().
//     */
//
//    @Override
//    public void onMessageReceived(String from, Bundle bundle) {
//        String title = bundle.getString("title");
//        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
//        String flag = bundle.getString("flag");
//        String data = bundle.getString("data");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "title: " + title);
//        Log.d(TAG, "isBackground: " + isBackground);
//        Log.d(TAG, "flag: " + flag);
//        Log.d(TAG, "data: " + data);
//
//        if (flag == null)
//            return;
//
//        if(MyApplication.getInstance().getPrefManager().getUser() == null){
//            // user is not logged in, skipping push notification
//            Log.e(TAG, "user is not logged in, skipping push notification");
//            return;
//        }
//
//        if (from.startsWith("/topics/")) {
//            // owner received from some topic.
//        } else {
//            // normal downstream owner.
//        }
//
//        switch (Integer.parseInt(flag)) {
//            case Config.PUSH_TYPE_CHATROOM:
//                // push notification belongs to a chat room
//                processChatRoomPush(title, isBackground, data);
//                break;
//            case Config.PUSH_TYPE_USER:
//                // push notification is specific to user
//                processUserMessage(title, isBackground, data);
//                break;
//        }
//    }
//
//    /**
//     * Processing chat room push owner
//     * this owner will be broadcasts to all the activities registered
//     * */
//    private void processChatRoomPush(String title, boolean isBackground, String data) {
//        if (!isBackground) {
//
//            try {
//                JSONObject datObj = new JSONObject(data);
//
//                String chatRoomId = datObj.getString("chat_room_id");
//
//                JSONObject mObj = datObj.getJSONObject("owner");
//                Message owner = new Message();
//                owner.setMessage(mObj.getString("owner"));
//                owner.setId(mObj.getString("message_id"));
//                owner.setCreatedAt(mObj.getString("created_at"));
//
//                JSONObject uObj = datObj.getJSONObject("user");
//
//                // skip the owner if the owner belongs to same user as
//                // the user would be having the same owner when he was sending
//                // but it might differs in your scenario
//                if (uObj.getString("user_id").equals(MyApplication.getInstance().getPrefManager().getUser().getId())) {
//                    Log.e(TAG, "Skipping the push owner as it belongs to same user");
//                    return;
//                }
//
//                User user = new User();
//                user.setId(uObj.getString("user_id"));
//                user.setEmail(uObj.getString("email"));
//                user.setName(uObj.getString("title"));
//                owner.setUser(user);
//
//                // verifying whether the app is in background or foreground
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//                    // app is in foreground, broadcast the push owner
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
//                    pushNotification.putExtra("owner", owner);
//                    pushNotification.putExtra("chat_room_id", chatRoomId);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils();
//                    notificationUtils.playNotificationSound();
//                } else {
//
//                    // app is in background. show the owner in notification try
//                    Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
//                    resultIntent.putExtra("chat_room_id", chatRoomId);
//                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + owner.getMessage(), owner.getCreatedAt(), resultIntent);
//                }
//
//            } catch (JSONException e) {
//                Log.e(TAG, "json parsing error: " + e.getMessage());
//                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//        } else {
//            // the push notification is silent, may be other operations needed
//            // like inserting it in to SQLite
//        }
//    }
//
//    /**
//     * Processing user specific push owner
//     * It will be displayed with / without image in push notification tray
//     * */
//    private void processUserMessage(String title, boolean isBackground, String data) {
//        if (!isBackground) {
//
//            try {
//                JSONObject datObj = new JSONObject(data);
//
//                String imageUrl = datObj.getString("image");
//
//                JSONObject mObj = datObj.getJSONObject("owner");
//                Message owner = new Message();
//                owner.setMessage(mObj.getString("owner"));
//                owner.setId(mObj.getString("message_id"));
//                owner.setCreatedAt(mObj.getString("created_at"));
//
//                JSONObject uObj = datObj.getJSONObject("user");
//                User user = new User();
//                user.setId(uObj.getString("user_id"));
//                user.setEmail(uObj.getString("email"));
//                user.setName(uObj.getString("title"));
//                owner.setUser(user);
//
//                // verifying whether the app is in background or foreground
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//                    // app is in foreground, broadcast the push owner
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
//                    pushNotification.putExtra("owner", owner);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils();
//                    notificationUtils.playNotificationSound();
//                } else {
//
//                    // app is in background. show the owner in notification try
//                    Intent resultIntent = new Intent(getApplicationContext(), TopicsActivity.class);
//
//                    // check for push notification image attachment
//                    if (TextUtils.isEmpty(imageUrl)) {
//                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + owner.getMessage(), owner.getCreatedAt(), resultIntent);
//                    } else {
//                        // push notification contains image
//                        // show it with the image
//                        showNotificationMessageWithBigImage(getApplicationContext(), title, owner.getMessage(), owner.getCreatedAt(), resultIntent, imageUrl);
//                    }
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, "json parsing error: " + e.getMessage());
//                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//        } else {
//            // the push notification is silent, may be other operations needed
//            // like inserting it in to SQLite
//        }
//    }
//
//    /**
//     * Showing notification with text only
//     * */
//    private void showNotificationMessage(Context context, String title, String owner, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, owner, timeStamp, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     * */
//    private void showNotificationMessageWithBigImage(Context context, String title, String owner, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, owner, timeStamp, intent, imageUrl);
//    }
//}
