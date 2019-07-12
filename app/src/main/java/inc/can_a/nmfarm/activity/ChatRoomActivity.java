package inc.can_a.nmfarm.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.adapter.ChatRoomThreadAdapter;
import inc.can_a.nmfarm.app.ApiClient;
import inc.can_a.nmfarm.app.ApiInterface;
import inc.can_a.nmfarm.app.Config;
import inc.can_a.nmfarm.app.MyApplication;
import inc.can_a.nmfarm.fcm.NotificationUtils;
import inc.can_a.nmfarm.helper.FileUtils;
import inc.can_a.nmfarm.model.ErrorMsgResponse;
import inc.can_a.nmfarm.model.Message;
import inc.can_a.nmfarm.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static inc.can_a.nmfarm.helper.ImageCompressorUtil.getCompressed;

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;
    private ImageButton btn_attach_image;
    private ImageView imageView;
    String selfUserId;
    private static Uri filePath;
    private static File file;
    private Bitmap bitmap;
    private MultipartBody.Part part;
    private final int PICK_IMAGE_REQUEST = 1;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputMessage = findViewById(R.id.created_by);
        btn_attach_image = findViewById(R.id.btn_attach_image);
        btnSend =  findViewById(R.id.btn_send);


        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("title");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        recyclerView =  findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        if (MyApplication.getInstance().getPrefManager().getUser()!=null){
            // self user id is to identify the owner owner
            selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        }else {
            selfUserId = "1";
        }


        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push owner is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = inputMessage.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Enter a owner", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(MyApplication.getInstance().getPrefManager().getUser()!=null){
                    sendMessage(message);
                }else {
                    Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(in);
                }

            }
        });

        btn_attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        fetchChatThread();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push owner, will add the owner to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //on getting result if we are looking for bitmap two then set it
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);


                final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cardview_dark_background)));
                // it dismiss the dialog when click outside the dialog frame
                dialog.setCanceledOnTouchOutside(true);
                // Get the layout inflater
                dialog.setContentView(R.layout.dialog_image);

                imageView = dialog.findViewById(R.id.image);
                final EditText edit_desc = dialog.findViewById(R.id.edit_desc);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                Button btn_send_dialog = dialog.findViewById(R.id.btn_ok);

                //add caption
                imageView.setImageBitmap(bitmap);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // dismiss the dialog
                        dialog.dismiss();
                        dialog.cancel();
                    }
                });
                btn_send_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = edit_desc.getText().toString();
                        if(MyApplication.getInstance().getPrefManager().getUser()!=null){
                            sendMessage(msg);
                            dialog.dismiss();
                            dialog.cancel();
                        }else {
                            Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(in);
                        }
                    }
                });

                 dialog.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatRoomActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatRoomActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ChatRoomActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ChatRoomActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }

        }else {
            showFileChooser(PICK_IMAGE_REQUEST);
        }
    }

    public static RequestBody createRequestBody(String value){
        return RequestBody.create(MediaType.parse("text/plain"),value);
    }


    /**
     * Fetching all the messages of a single chat room
     * */
    /**    private void fetchChatThread() {

        String endPoint = EndPoints.CHAT_THREAD.replace("_ID_", chatRoomId);
        Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("owner");
                            String createdAt = commentObj.getString("created_at");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            String userId = userObj.getString("user_id");
                            String userName = userObj.getString("username");
                            User user = new User(userId, userName, null);

                            Message owner = new Message();
                            owner.setId(commentId);
                            owner.setMessage(commentText);
                            owner.setCreatedAt(createdAt);
                            owner.setUser(user);

                            messageArrayList.add(owner);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("owner"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }*/
    private void fetchChatThread() {
        //Todo add the refreshing progress
        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ErrorMsgResponse> call = apiService.fetchChatThread(chatRoomId);
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        messageArrayList.addAll(response.body().getMessages());
                        //Log.e(TAG, "onResponse: arraylist "+chatRoomArrayList );
                        /*mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCou-nt() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }*/

                    }else {
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "onResponse: error returned true" );
                        Toast.makeText(getApplicationContext(), "" + response.body().getError(), Toast.LENGTH_LONG).show();
                    }

                }catch(NullPointerException n){
                    Log.e(TAG, "onResponse: Exception "+n );

                    //mSwipeRefreshLayout.setRefreshing(false);
                   /* if (response.body().getMessage().equals("No Results")){
                        no_ads_for_me.setVisibility(View.VISIBLE);
                        no_ads_for_me.setText("No vehicles Available");
                        recyclerView.setVisibility(View.GONE);
                    }*/
                }

            }

            @Override
            public void onFailure(Call<ErrorMsgResponse>call, Throwable t) {
                //mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "onResponse: on failure  "+t.getMessage() );

            }
        });
    }

    /**
     * Posting a new owner in chat room
     * will make an http call to our server. Our server again sends the owner
     * to all the devices as push notification
     * */
    /**    private void sendMessage() {
     final String owner = this.inputMessage.getText().toString().trim();

     if (TextUtils.isEmpty(owner)) {
     Toast.makeText(getApplicationContext(), "Enter a owner", Toast.LENGTH_SHORT).show();
     return;
     }

     String endPoint = EndPoints.CHAT_ROOM_MESSAGE.replace("_ID_", chatRoomId);

     Log.e(TAG, "endpoint: " + endPoint);

     this.inputMessage.setText("");

     StringRequest strReq = new StringRequest(Request.Method.POST,
     endPoint, new Response.Listener<String>() {

    @Override
    public void onResponse(String response) {
    Log.e(TAG, "response: " + response);

    try {
    JSONObject obj = new JSONObject(response);

    // check for error
    if (obj.getBoolean("error") == false) {
    JSONObject commentObj = obj.getJSONObject("owner");

    String commentId = commentObj.getString("message_id");
    String commentText = commentObj.getString("owner");
    String createdAt = commentObj.getString("created_at");

    JSONObject userObj = obj.getJSONObject("user");
    String userId = userObj.getString("user_id");
    String userName = userObj.getString("title");
    User user = new User(userId, userName, null);

    Message owner = new Message();
    owner.setId(commentId);
    owner.setMessage(commentText);
    owner.setCreatedAt(createdAt);
    owner.setUser(user);

    messageArrayList.add(owner);

    mAdapter.notifyDataSetChanged();
    if (mAdapter.getItemCount() > 1) {
    // scrolling to bottom of the recycler view
    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
    }

    } else {
    Toast.makeText(getApplicationContext(), "" + obj.getString("owner"), Toast.LENGTH_LONG).show();
    }

    } catch (JSONException e) {
    Log.e(TAG, "json parsing error: " + e.getMessage());
    Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    }
    }, new Response.ErrorListener() {

    @Override
    public void onErrorResponse(VolleyError error) {
    NetworkResponse networkResponse = error.networkResponse;
    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
    Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    inputMessage.setText(owner);
    }
    }) {

    @Override
    protected Map<String, String> getParams() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
    params.put("owner", owner);

    Log.e(TAG, "Params: " + params.toString());

    return params;
    };
    };


     // disabling retry policy so that it won't make
     // multiple http calls
     int socketTimeout = 0;
     RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
     DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
     DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

     strReq.setRetryPolicy(policy);

     //Adding request to request queue
     MyApplication.getInstance().addToRequestQueue(strReq);
     }*/
    private void sendMessage(final String message) {

        //Todo add the refreshing progress
        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, RequestBody> params = new HashMap<>();

        params.put("user_id",createRequestBody(MyApplication.getInstance().getPrefManager().getUser().getId()) );
        params.put("message", createRequestBody(message));
        if (filePath!=null){

            try{
                //Compress the file
                file = getCompressed(this, FileUtils.getPath(this,filePath) );
            }catch (final IOException ex) {
                ex.printStackTrace();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("**/*//*"), file);
            part = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }
        Log.e(TAG, "sendMessage: posting  "+params );
        Call<ErrorMsgResponse> call = apiService.sendMessage(chatRoomId,part,params);
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        //messageArrayList.addAll(response.body().getMessages());
                        //Log.e(TAG, "onResponse: arraylist "+chatRoomArrayList );
                        Message msg = response.body().getMessageObj();
                        User user = response.body().getUser();
                        msg.setUser(user);
                        messageArrayList.add(msg);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    }else {
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "onResponse: error returned true" );
                        Toast.makeText(getApplicationContext(), "" + response.body().getError(), Toast.LENGTH_LONG).show();
                    }

                }catch(NullPointerException n){
                    Log.e(TAG, "onResponse: Exception "+n );

                    //mSwipeRefreshLayout.setRefreshing(false);
                   /* if (response.body().getMessage().equals("No Results")){
                        no_ads_for_me.setVisibility(View.VISIBLE);
                        no_ads_for_me.setText("No vehicles Available");
                        recyclerView.setVisibility(View.GONE);
                    }*/
                }

            }

            @Override
            public void onFailure(Call<ErrorMsgResponse>call, Throwable t) {
                //mSwipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "onResponse: on failure  "+t.getMessage() );

            }
        });
    }

}
