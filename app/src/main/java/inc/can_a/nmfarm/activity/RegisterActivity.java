package inc.can_a.nmfarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.ApiClient;
import inc.can_a.nmfarm.app.ApiInterface;
import inc.can_a.nmfarm.app.MyApplication;
import inc.can_a.nmfarm.model.ErrorMsgResponse;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputName, inputEmail, inputPassword, inputActivity;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Check for register session. It user is already logged in
         * redirect him to main activity
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, Main2Activity.class));
            finish();
        }

        setContentView(R.layout.activity_register);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutEmail =  findViewById(R.id.input_layout_email);
        inputLayoutPassword =  findViewById(R.id.input_layout_password);

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputActivity = findViewById(R.id.input_activity);

        btnEnter = findViewById(R.id.btn_enter);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    /**
     * logging in user. Will make http post request with title, email
     * as parameters
     */
    /**    private void register() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        final String title = inputName.getText().toString();
        final String email = inputEmail.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(
                                userObj.getString("user_id"),
                                userObj.getString("title"),
                                userObj.getString("email"));

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), TopicsActivity.class));
                        finish();

                    } else {
                        // register error - simply toast the owner
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("owner"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("email", email);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }*/
    private void register() {

        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        final String name = inputName.getText().toString();
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final String activity = inputActivity.getText().toString();

        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("activities", activity);

        Call<ErrorMsgResponse> call = apiService.register(params);
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "onResponse: error returned false"+response.body() );
                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(response.body().getUser());

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                        finish();

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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating title
    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    // Validating email
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }
    }

}
