package inc.can_a.nmfarm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.EndPoints;
import inc.can_a.nmfarm.model.GroupMember;


public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<GroupMember> groupMembers;
    private static String today;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,activity;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            activity = view.findViewById(R.id.activity);
            imageView = view.findViewById(R.id.imageview);
        }
    }


    public GroupMembersAdapter(Context mContext, ArrayList<GroupMember> groupMembers) {
        this.mContext = mContext;
        this.groupMembers = groupMembers;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroupMember groupMember = groupMembers.get(position);
        holder.name.setText(groupMember.getName());
        holder.activity.setText(groupMember.getActivity());

        Glide.with(mContext).load(EndPoints.IMAGES+"default.jpg").circleCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

}
