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
        public TextView name;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
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

        //Todo add the profile
        /*if (!TextUtils.isEmpty(chatRoom.getImage())){
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(EndPoints.IMAGES+chatRoom.getImage()).circleCrop().into(holder.imageView);
        }else {
            Glide.with(mContext).load(EndPoints.IMAGES+"default.jpg").circleCrop().into(holder.imageView);
        }*/
        Glide.with(mContext).load(EndPoints.IMAGES+"default.jpg").circleCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
