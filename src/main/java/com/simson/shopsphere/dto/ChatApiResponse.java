package com.simson.shopsphere.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ChatApiResponse {

    private boolean success;
    private ChatReplyData data;
    private ChatErrorData error;

    public ChatApiResponse() {}

    public ChatApiResponse(boolean success, ChatReplyData data, ChatErrorData error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static ChatApiResponse success(String reply) {
        return new ChatApiResponse(true, new ChatReplyData(reply), null);
    }

    public static ChatApiResponse error(String code, String message) {
        return new ChatApiResponse(false, null, new ChatErrorData(code, message));
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ChatReplyData getData() {
        return data;
    }

    public void setData(ChatReplyData data) {
        this.data = data;
    }

    public ChatErrorData getError() {
        return error;
    }

    public void setError(ChatErrorData error) {
        this.error = error;
    }
}
