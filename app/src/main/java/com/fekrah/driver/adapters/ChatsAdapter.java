package com.fekrah.driver.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.driver.R;
import com.fekrah.driver.activities.ChatActivity;
import com.fekrah.driver.helper.GetTimeAgo;
import com.fekrah.driver.models.Room;
import com.fekrah.driver.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.fekrah.driver.adapters.AcceptedOrderDriversAdapter.USER_ID;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private List<Room> rooms;
    private List<String> roomsKey;
    private Context context;
    DatabaseReference reference;
    DatabaseReference userImgRef;
    DatabaseReference userNameRef;
    Query lastMessageRef;
    Query lastQuery;

    public ChatsAdapter(List<Room> rooms, List<String> roomsKey, Context context) {
        this.rooms = rooms;
        this.context = context;
        this.roomsKey = roomsKey;
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int i) {
        final Room room = rooms.get(i);

        holder.roomTime.setText(GetTimeAgo.getTimeAgo(room.getTime(),context));
        holder.roomLastMessage.setText(room.getLast_message());

        reference.child("users").child(room.getRoom_key()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    final User user = dataSnapshot.getValue(User.class);
                    holder.roomImage.setImageURI(user.getImg());
                    holder.roomName.setText(user.getName());

                    holder.mainView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(context,ChatActivity.class);
                            intent.putExtra(USER_ID,user);
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView roomImage;
        TextView roomName;
        TextView roomTime;
        TextView roomLastMessage;
        View mainView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            mainView = itemView;
            roomLastMessage = itemView.findViewById(R.id.room_chat_last_message);
            roomImage = itemView.findViewById(R.id.room_chat_image);
            roomName = itemView.findViewById(R.id.room_chat_name);
            roomTime = itemView.findViewById(R.id.room_chat_time);
        }
    }

    public void addRoom(Room userID) {
        rooms.add(userID);
        notifyDataSetChanged();
    }

    public void addRoom(int index, Room userID) {
        rooms.add(index, userID);
        notifyDataSetChanged();
    }

    public int indexOfRoom(String  key) {
        return roomsKey.indexOf(key);
    }

    public void removeRoom(int position) {
        rooms.remove(position);
        notifyDataSetChanged();
    }

    public void addKey(int index,String key){
        roomsKey.add(index,key);
        notifyDataSetChanged();
    }

    public void removeKey(int p){
        roomsKey.remove(p);
    }
}
