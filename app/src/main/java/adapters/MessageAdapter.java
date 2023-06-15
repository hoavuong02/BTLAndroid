package adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.Personal_interface;
import com.example.btl.R;
import com.example.btl.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import methods.FileHelper;
import methods.FireStoreMethod;
import models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Activity activity;
    private List<Message> messageList;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public MessageAdapter(List<Message> messageList, Activity activity) {
        this.messageList = messageList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_right, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_left, parent, false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        FireStoreMethod fireStoreMethod = new FireStoreMethod();
        fireStoreMethod.getUserByUid(message.getSender(), new FireStoreMethod.DataCallback() {
            @Override
            public void onDataLoaded(User user) {
                Glide.with(activity)
                        .load(user.getPhotoUrl())
                        .into(holder.imageViewSender);
                holder.textViewSender.setText(user.getUsername());
                // Đặt sự kiện nhấp vào imageViewSender

                Personal_interface(holder, position, user.getUsername(), user.getPhotoUrl());

            }

            @Override
            public void onError(Exception e) {
                holder.textViewSender.setText("");
            }
        });

        if(!message.getText().toString().isEmpty()){
            holder.textViewContent.setText(message.getText());
            holder.imageView.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
        } else if (!message.getmFileURL().toString().isEmpty()) {

            holder.textViewContent.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.btnDownload.setText(message.getmFileName());
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FileHelper fileHelper;
                    fileHelper = new FileHelper( activity, holder.btnDownload);
                    fileHelper.startDownload(message.getmFileURL());

                }
            });
        } else if (!message.getmPhotoURL().isEmpty()) {
            holder.textViewContent.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(message.getmPhotoURL())
                    .into(holder.imageView);
        }



    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageViewSender;
        TextView textViewSender;
        TextView textViewContent;
        ImageView imageView;
        Button btnDownload;

        public MessageViewHolder(View itemView) {
            super(itemView);
            imageViewSender = itemView.findViewById(R.id.imageViewSender);
            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            imageView = itemView.findViewById(R.id.imageView);
            btnDownload=itemView.findViewById(R.id.buttonDownload);
        }
    }
    public int getItemViewType(int position){
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //sender is current user
        if(messageList.get(position).getSender().equals(currentUserUid)){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }


    //Phan giao dien trang ca nhan BDT

    public void Personal_interface(@NonNull MessageViewHolder holder, int position, String userName, String photoUrl) {
        holder.imageViewSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện khi người dùng nhấp vào hình ảnh imageViewSender

                // Tạo Intent để chuyển đến màn hình trang cá nhân và truyền thông tin người dùng
                Intent profileIntent = new Intent(activity, Personal_interface.class);
                profileIntent.putExtra("userName", userName);
                profileIntent.putExtra("photoUrl", photoUrl);
                activity.startActivity(profileIntent);
            }
        });
    }


    }




