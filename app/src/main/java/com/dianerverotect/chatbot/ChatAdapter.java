package com.dianerverotect.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dianerverotect.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the chat messages in the chatbot
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    
    private final Context context;
    private final List<ChatMessage> chatMessages;
    
    /**
     * Constructor
     * @param context Context
     * @param chatMessages List of chat messages
     */
    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }
    
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.messageText.setText(message.getMessage());
            
            // Set timestamp if available
            if (userHolder.messageTimestamp != null) {
                userHolder.messageTimestamp.setText(getCurrentTime());
            }
        } else {
            BotMessageViewHolder botHolder = (BotMessageViewHolder) holder;
            botHolder.messageText.setText(message.getMessage());
            
            // Set timestamp if available
            if (botHolder.messageTimestamp != null) {
                botHolder.messageTimestamp.setText(getCurrentTime());
            }
            
            // If the message has an image, show it
            if (message.getImageResourceId() != 0) {
                botHolder.messageImage.setVisibility(View.VISIBLE);
                botHolder.messageImage.setImageResource(message.getImageResourceId());
            } else {
                botHolder.messageImage.setVisibility(View.GONE);
            }
        }
    }
    
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    
    /**
     * Get current time formatted as HH:mm
     * @return Formatted time string
     */
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    /**
     * ViewHolder for user messages
     */
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTimestamp;
        
        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.user_message_text);
            messageTimestamp = itemView.findViewById(R.id.message_timestamp);
        }
    }
    
    /**
     * ViewHolder for bot messages
     */
    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView messageImage;
        TextView messageTimestamp;
        
        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.bot_message_text);
            messageImage = itemView.findViewById(R.id.bot_message_image);
            messageTimestamp = itemView.findViewById(R.id.message_timestamp);
        }
    }
}
