package inc.can_a.nmfarm.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.adapter.GroupMembersAdapter;
import inc.can_a.nmfarm.adapter.GroupsAdapter;
import inc.can_a.nmfarm.app.ApiClient;
import inc.can_a.nmfarm.app.ApiInterface;
import inc.can_a.nmfarm.model.ErrorMsgResponse;
import inc.can_a.nmfarm.model.GroupMember;
import retrofit2.Call;
import retrofit2.Callback;

public class GroupMembersActivity extends AppCompatActivity {

    private static final String TAG = "GroupMemAct";

    private ArrayList<GroupMember> groupMembers;
    private GroupMembersAdapter mAdapter;
    private RecyclerView recyclerView;
    private String gid,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent i = getIntent();
        gid = i.getStringExtra("gid");
        title = i.getStringExtra("title");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);

        groupMembers = new ArrayList<>();
        mAdapter = new GroupMembersAdapter(this, groupMembers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new GroupsAdapter.RecyclerTouchListener(this, recyclerView, new GroupsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
//                GroupMember groupMember = groupMembers.get(position);
//                Intent intent = new Intent(this, GroupMembersActivity.class);
//                intent.putExtra("gid", group.getGid());
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchGroupMembers();
    }

    private void fetchGroupMembers() {
        //Todo add the refreshing progress
        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ErrorMsgResponse> call = apiService.fetchGroupMembers(gid);
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        groupMembers.addAll(response.body().getGroupMembers() );
                        mAdapter.notifyDataSetChanged();
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
}
