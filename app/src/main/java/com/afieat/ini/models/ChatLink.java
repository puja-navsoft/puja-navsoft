package com.afieat.ini.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatLink {

    @SerializedName("chat_link")
    @Expose
    private String chatLink;

    public String getChatLink() {
        return chatLink;
    }

    public void setChatLink(String chatLink) {
        this.chatLink = chatLink;
    }

}
