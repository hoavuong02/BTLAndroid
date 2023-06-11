package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.MainActivity;
import com.example.btl.R;
import com.example.btl.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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
        if(viewType==MSG_TYPE_LEFT){
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
                holder.textViewSender.setText(user.getUsername());
            }

            @Override
            public void onError(Exception e) {
                holder.textViewSender.setText("");
            }
        });

        if(!message.getText().toString().isEmpty()){
            holder.textViewContent.setText(message.getText());
            holder.textViewContent.setVisibility(View.GONE);
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
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSender;
        TextView textViewContent;
        ImageView imageView;
        Button btnDownload;

        public MessageViewHolder(View itemView) {
            super(itemView);
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
}

