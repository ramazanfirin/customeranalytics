package com.customeranalytics.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.customeranalytics.web.rest.UserJWTController;
import com.customeranalytics.web.rest.vm.LoginVM;

import Luxand.FSDK;

@Service
public class FaceRecognitionService {
	
	@Autowired private SimpMessageSendingOperations simpMessagingTemplate;
	@Autowired private MqttService mqttService;
	
	
	//private final SimpMessageSendingOperations messagingTemplate;

	@PostConstruct
	public void init() {
		
//		StompSession stompSession =  prepareSocketConnectionsForTyping(URL,userJWTController,"user", "user",SUBSCRIBE_TYPING_MESSAGE_ENDPOINT,completableFuture);

		
		
		try {
            int r = FSDK.ActivateLibrary("FgsONyEWyINCBz0lbgccL7LMjLMsgAbHxwgdNLt0Q1j8UTmTgZyeaeCoXno1HBydmshM4ygfBO+6/qlKhFZAF5BvVaSKx7NaV0fIFPtkRie2h1DMmKIYa15N7qBP/DsclEoom67W6fXIyRo4yBPhemiu53rXVsqfvmMOuiu7KhQ=");
            if (r != FSDK.FSDKE_OK){
                 System.err.println("sdk initiaize error");
                 return;
            }
		}catch(java.lang.UnsatisfiedLinkError e) {
            e.printStackTrace();
			System.exit(1);
		} 
		
		 FSDK.Initialize();
	}
	
//	 public static StompSession prepareSocketConnectionsForTyping(String URL,UserJWTController userJWTController,String username,String password,String subscribeEndpoint) throws InterruptedException {
//	    	LoginVM loginVM = new LoginVM();
//	    	loginVM.setUsername(username);
//	    	loginVM.setPassword(password);
//	        String tokenAdmin =userJWTController.authorizeGetToken(loginVM);
//
//	        
//	        WebSocketStompClient stompClient = new WebSocketStompClient((WebSocketClient) new SockJsClient(createTransportClient()));
//	        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//	        String url=URL+ "?access_token=" + tokenAdmin;
//	        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {}).get();
//	        
//	        stompSession.subscribe(subscribeEndpoint, new TypingStompFrameHandler(completableFuture));
//	        
//	        return stompSession;
//	    }
	
	 public static List<Transport> createTransportClient() {
	        List<Transport> transports = new ArrayList<>(1);
	        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
	        return transports;
	    }
	 
	@SubscribeMapping("/user/")
	public void listen(String message) {
		System.out.println("message from websocket="+message);
	}
	
	//@Async
	public void analize(String path) throws MqttPersistenceException, MqttException {
		 System.out.println("istek geldi");
		 Long start = System.currentTimeMillis();
		 
		 //simpMessagingTemplate.convertAndSend("/user/admin/exchange/amq.direct/chat.message", path);
		 MqttMessage mqttMessage = new MqttMessage();
		 mqttMessage.setPayload(new String(path).getBytes());
		 mqttService.publish(mqttMessage);
		 
		 
//		 HImage imageHandle = new HImage();
//		
//		 if (FSDK.LoadImageFromFileW(imageHandle, path) == FSDK.FSDKE_OK){
//			 TFaces faceArray = new TFaces();
//			 FSDK.TFacePosition.ByReference facePosition = new FSDK.TFacePosition.ByReference();
//			 
//			 if (FSDK.DetectMultipleFaces( imageHandle,faceArray) == FSDK.FSDKE_OK){
//			 
//				 if(faceArray.faces.length==0)
//					 System.out.println("no face detected");
//					 
//					 
//				 	for (int i = 0; i < faceArray.faces.length; i++) {
//						TFacePosition position =faceArray.faces[i];
//					  
//						String [] AttributeValues = new String[1];
//                       String [] AttributeValuesAge = new String[1];
//					    
//                       FSDK_Features.ByReference facialFeatures = new FSDK_Features.ByReference();
//                       FSDK.DetectFacialFeaturesInRegion(imageHandle, position, facialFeatures);
//                       
//                       //facialFeatures.;
//                       
//                       System.out.println("sdfsdf");
//                       
//					    int res =  FSDK.DetectFacialAttributeUsingFeatures(
//					    		imageHandle,
//					    		facialFeatures, 
//					    		"Gender", 
//					    		AttributeValues, 1024);
//					    
//					    int res2 =  FSDK.DetectFacialAttributeUsingFeatures(
//					    		imageHandle,
//					    		facialFeatures, 
//					    		"Age", 
//					    		AttributeValuesAge, 1024);
//					    
//					    String[] result = AttributeValues[0].split(";");
//					    System.out.println("male rate is"+result[0]);
//					    System.out.println("female rate is"+result[1]);
//					    
//					    System.out.println(AttributeValuesAge[0]);
//					
//					   
//				
//				 	}
//				 
//			 }
//		 }else {
//			 System.out.println("file readErrod");
//		 }
		 deleteFile(path);
	}
	
	public void deleteFile(String path) {
		File file = new File(path);
		if(file.delete()){
			System.out.println(file.getName() + " is deleted!");
		}else{
			System.out.println("Delete operation is failed.");
		}
	}
}
