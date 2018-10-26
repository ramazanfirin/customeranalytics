package com.customeranalytics.service;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class TypingStompFrameHandler implements StompFrameHandler{
	CompletableFuture<String> completableFuture;
	
	public TypingStompFrameHandler(CompletableFuture<String> completableFuture) {
		super();
		this.completableFuture = completableFuture;
	}

	@Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        this.completableFuture.complete((String) o);
    }
}
