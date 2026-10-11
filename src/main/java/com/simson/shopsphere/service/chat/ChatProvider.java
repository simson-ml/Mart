package com.simson.shopsphere.service.chat;

public interface ChatProvider {

    /**
     * Generates an assistant reply for the given user message and context.
     *
     * @param userMessage the validated user question or instruction
     * @param context safe, non-sensitive store catalog/navigation context
     * @return the assistant response text
     */
    String getReply(String userMessage, String context);
}
