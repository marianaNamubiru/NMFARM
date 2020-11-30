package inc.can_a.nmfarm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.EndPoints;
import inc.can_a.nmfarm.app.MyApplication;
import inc.can_a.nmfarm.model.Group;

import static inc.can_a.nmfarm.app.MyApplication.TAG;


public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Group> chatRoomArrayList;
    private static String today;
    private GroupAdapterListener listner;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, owner, timestamp;
        Button chat, join;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            owner = view.findViewById(R.id.created_by);
            timestamp = view.findViewById(R.id.timestamp);
            chat = view.findViewById(R.id.chat);
            join = view.findViewById(R.id.join);
            imageView = view.findViewById(R.id.imageview);

            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onChatPressed(chatRoomArrayList.get(getAdapterPosition()));
                }
            });

            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listner.onJoinPressed(chatRoomArrayList.get(getAdapterPosition()));
                }
            });
        }
    }


    public GroupsAdapter(Context mContext, ArrayList<Group> chatRoomArrayList, GroupAdapterListener listner) {
        this.mContext = mContext;
        this.chatRoomArrayList = chatRoomArrayList;
        this.listner = listner;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group chatRoom = chatRoomArrayList.get(position);
        holder.title.setText(chatRoom.getTitle());
        holder.owner.setText(chatRoom.getOwner_id());

        if (MyApplication.getInstance().getPrefManager().getUser()!=null){
            String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
            Log.e(TAG, "onBindViewHolder: my user id is "+selfUserId );
            //Todo also if not member of group is check in fg and if not exist then return that field too
            if (chatRoom.getOwner_id().equals(selfUserId) || chatRoom.isMember() ){
                //holder.join.setVisibility(View.GONE);
                holder.join.setText("Member");
                //Todo or if u are a group member
            }
        }

        holder.timestamp.setText(getTimeStamp(chatRoom.getCreated_on()));

        if (!TextUtils.isEmpty(chatRoom.getImage())){
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(EndPoints.IMAGES+chatRoom.getImage()).circleCrop().into(holder.imageView);
        }else {
            Glide.with(mContext).load(EndPoints.IMAGES+"default.jpg").circleCrop().into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public interface GroupAdapterListener {
        void onChatPressed(Group group);
        void onJoinPressed(Group group);
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
