package inc.can_a.nmfarm.app;

import java.util.List;
import java.util.Map;

import inc.can_a.nmfarm.model.ChatRoom;
import inc.can_a.nmfarm.model.ErrorMsgResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

//Todo when going live change serve_app to serve_app
public interface ApiInterface {

    @FormUrlEncoded
    @POST("nm_farm/v1/login")
    Call<ErrorMsgResponse> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("nm_farm/v1/user/register")
    Call<ErrorMsgResponse> register(@FieldMap Map<String, String> params);

    @GET("nm_farm/v1/chat_rooms")
    Call<ErrorMsgResponse> fetchChatRooms();

    @GET("nm_farm/v1/feeds")
    Call<ErrorMsgResponse> fetchFeeds();

    @FormUrlEncoded
    @POST("nm_farm/v1/groups")
    Call<ErrorMsgResponse> fetchGroups(@FieldMap Map<String, String> params);

    @GET("nm_farm/v1/groups/{id}")
    Call<ErrorMsgResponse> fetchGroupMembers(@Path("id") String id);


    @GET("nm_farm/v1/chat_rooms/{id}")
    Call<ErrorMsgResponse> fetchChatThread(@Path("id") String id);

    @Multipart
    @POST("nm_farm/v1/chat_rooms/{id}/message")
    Call<ErrorMsgResponse> sendMessage(
            @Path("id") String id,
            @Part MultipartBody.Part parts,
            @PartMap Map<String, RequestBody> params);

    @Multipart
    @POST("nm_farm/v1/join/{id}/group")
    Call<ErrorMsgResponse> joinGroup(
            @Path("id") String id,
            @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST("nm_farm/v1/add_feed")
    Call<ErrorMsgResponse> addFeed(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("nm_farm/v1/add_group")
    Call<ErrorMsgResponse> addGroup(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("nm_farm/v1/add_problem")
    Call<ErrorMsgResponse> addProblem(@FieldMap Map<String, String> params);

   /* @GET
    Call<MessageThreadsResponse> getChatThread(@Url String url, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("serve_app/v1/msgs_inbox")
    Call<MessageThreadsResponse> getChatThreadUsingIds(@FieldMap Map<String, String> params, @Header("Authorization") String authorization);
*/

//    @GET("api/trlist/")
//    Call<ErrorMsgResponse> getAllPhoneRepairRequests(@QueryMap Map<String, String> param);
//    //Call<ErrorMsgResponse> getAllPhoneRepairRequests(@Url String url, @Header("Authorization") String authorization);
//
//    //@FormUrlEncoded
//    //@POST("clad/v1/get_all_device_repair_requests")
//    //Call<ErrorMsgResponse> getAllPhoneRepairRequests(@FieldMap Map<String, String> params);
//
//    //@FormUrlEncoded
//    //@POST("trlist/")
//    //Call<ErrorMsgResponse> acceptCustomerRequest(@FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @PATCH("api/trlist/{id}/")
//    Call<ResponseBody> acceptCustomerRequest(@Path("id") String id, @FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST("clad/v1/get_specific_tech_device_requests")
//    Call<ErrorMsgResponse> getMyDeviceRepairRequests(@FieldMap Map<String, String> params);
//
//    @FormUrlEncoded
//    @POST("clad/v1/update_customer_request_status")
//    Call<ErrorMsgResponse> updateCustomerRequestStatus(@FieldMap Map<String, String> params);


}