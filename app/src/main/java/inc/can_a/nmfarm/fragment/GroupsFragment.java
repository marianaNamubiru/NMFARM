package inc.can_a.nmfarm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.activity.GroupMembersActivity;
import inc.can_a.nmfarm.adapter.GroupsAdapter;
import inc.can_a.nmfarm.app.ApiClient;
import inc.can_a.nmfarm.app.ApiInterface;
import inc.can_a.nmfarm.model.ErrorMsgResponse;
import inc.can_a.nmfarm.model.Group;
import retrofit2.Call;
import retrofit2.Callback;


public class GroupsFragment extends Fragment {

    private String TAG = GroupsFragment.class.getSimpleName();
    private ArrayList<Group> groups;
    private GroupsAdapter mAdapter;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //phoneRepairRequestArrayList = new ArrayList<>();

        /*if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_topic, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        groups = new ArrayList<>();
        mAdapter = new GroupsAdapter(getContext(), groups);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new GroupsAdapter.RecyclerTouchListener(getContext(), recyclerView, new GroupsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                Group group = groups.get(position);
                Intent intent = new Intent(getActivity(), GroupMembersActivity.class);
                intent.putExtra("gid", group.getGid());
                intent.putExtra("title", group.getTitle());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchGroups();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    private void fetchGroups() {
        //Todo add the refreshing progress
        //mSwipeRefreshLayout.setRefreshing(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ErrorMsgResponse> call = apiService.fetchGroups();
        call.enqueue(new Callback<ErrorMsgResponse>() {

            @Override
            public void onResponse(@NonNull Call<ErrorMsgResponse> call, @NonNull retrofit2.Response<ErrorMsgResponse> response) {
                Log.e(TAG, "onResponse: "+response );
                try{
                    if(response.isSuccessful()){
                        //mSwipeRefreshLayout.setRefreshing(false);
                        groups.addAll(response.body().getGroups());
                        mAdapter.notifyDataSetChanged();
                        Log.e(TAG, "onResponse: arraylist "+groups );

                    }else {
                        //mSwipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "onResponse: error returned true" );
                        Toast.makeText(getContext(), "" + response.body().getError(), Toast.LENGTH_LONG).show();
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
