package com.dianerverotect.chatbot;

/**
 * Model class for chat messages
 */
public class ChatMessage {
    private final String message;
    private final boolean isUser;
    private int imageResourceId;
    
    /**
     * Constructor for a message without an image
     * @param message Message text
     * @param isUser Whether the message is from the user
     */
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.imageResourceId = 0;
    }
    
    /**
     * Constructor for a bot message with an image
     * @param message Message text
     * @param imageResourceId Resource ID of the image
     */
    public ChatMessage(String message, int imageResourceId) {
        this.message = message;
        this.isUser = false;
        this.imageResourceId = imageResourceId;
    }
    
    /**
     * Get the message text
     * @return Message text
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Check if the message is from the user
     * @return True if the message is from the user, false otherwise
     */
    public boolean isUser() {
        return isUser;
    }
    
    /**
     * Get the image resource ID
     * @return Image resource ID
     */
    public int getImageResourceId() {
        return imageResourceId;
    }
    
    /**
     * Set the image resource ID
     * @param imageResourceId Image resource ID
     */
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
