package inc.can_a.nmfarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class AddProblemActivity extends AppCompatActivity {

    private static final String TAG = "AddProblemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_problem);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Problem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText et_problem = findViewById(R.id.editTextProblem);

        Button post = findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String problem = et_problem.getText().toString();
                if (!TextUtils.isEmpty(problem)){
                    postProblem(problem);
                }else {
                    Toast.makeText(getApplicationContext(),"Fill all the fields",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void postProblem(final String name) {
        //Todo add the refreshing progress
        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<>();
        //name either company or person
        //params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
        params.put("name", name);

        Call<ErrorMsgResponse> call = apiService.addProblem(params);
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                        startActivity(intent);
                        Log.e(TAG, "onResponse: succesfully posted " );

                    }else {
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "onResponse: error returned true" );
                    }

                }catch(NullPointerException n){
                    Log.e(TAG, "onResponse: Exception "+n );
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
