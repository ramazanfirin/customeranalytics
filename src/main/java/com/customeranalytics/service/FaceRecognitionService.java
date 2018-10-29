package com.customeranalytics.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.customeranalytics.domain.Record;
import com.customeranalytics.domain.Stuff;
import com.customeranalytics.domain.enumeration.Gender;
import com.customeranalytics.repository.RecordRepository;
import com.customeranalytics.repository.StuffRepository;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;
import com.innovatrics.iface.enums.AgeGenderSpeedAccuracyMode;
import com.innovatrics.iface.enums.FaceAttributeId;
import com.innovatrics.iface.enums.FacedetSpeedAccuracyMode;
import com.innovatrics.iface.enums.Parameter;

@Service
public class FaceRecognitionService {
	
	@Autowired private SimpMessageSendingOperations simpMessagingTemplate;
//	@Autowired private MqttService mqttService;
	
	IFace iface= null;
	FaceHandler faceHandler = null;
	
	 public int minEyeDistance = 30;
	    public int maxEyeDistance = 200;
	    
	    @Autowired
		RecordRepository recordRepository;
	
	    @Autowired
		StuffRepository stuffRepository;

//	public FaceRecognitionService() {
//			super();
//			// TODO Auto-generated constructor stub
//		}

	@PostConstruct
	public void init() throws IOException {
		
//		try {
//            int r = FSDK.ActivateLibrary("FgsONyEWyINCBz0lbgccL7LMjLMsgAbHxwgdNLt0Q1j8UTmTgZyeaeCoXno1HBydmshM4ygfBO+6/qlKhFZAF5BvVaSKx7NaV0fIFPtkRie2h1DMmKIYa15N7qBP/DsclEoom67W6fXIyRo4yBPhemiu53rXVsqfvmMOuiu7KhQ=");
//            if (r != FSDK.FSDKE_OK){
//                 System.err.println("sdk initiaize error");
//                 return;
//            }
//		}catch(java.lang.UnsatisfiedLinkError e) {
//            e.printStackTrace();
//			System.exit(1);
//		} 
//		
//		 FSDK.Initialize();
		
		iface = IFace.getInstance();
		ClassPathResource cpr = new ClassPathResource("iengine.lic");
		byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
		iface.initWithLicence(bdata);
		
		faceHandler = new FaceHandler();
		faceHandler.setParam(Parameter.FACEDET_SPEED_ACCURACY_MODE, FacedetSpeedAccuracyMode.FAST.toString());
		faceHandler.setParam(Parameter.AGEGENDER_SPEED_ACCURACY_MODE, AgeGenderSpeedAccuracyMode.FAST.toString());
	
		
		
		
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
	 
	 private byte[] convertToByteArray(BufferedImage originalImage) throws IOException{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, "jpg", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		}
	
	//@Async
	public void analize(String path) throws MqttPersistenceException, MqttException, IOException {
		 System.out.println("istek geldi");
		 Long start = System.currentTimeMillis();
		 
		BufferedImage image = ImageIO.read(new File(path));
		if(image==null) {
			System.out.println("no file loaded");
			return;
			
		}
		
		 Face[] faces = faceHandler.detectFaces(convertToByteArray(image), minEyeDistance, maxEyeDistance, 3);
			if(faces.length==0){
				System.out.println("No Face Detected");
				return;
			}
			
		Face face = faces[0];
		Float age = face.getAttribute(FaceAttributeId.AGE);
	    Float genderValue = face.getAttribute(FaceAttributeId.GENDER);
	    ;
		
	    Gender gender=null ;
	     if(genderValue<0)
	    	 gender = Gender.MALE;
	     else
	    	 gender = Gender.FEMALE;
	        
		 MqttMessage mqttMessage = new MqttMessage();
		 
		 Record record = new Record();
		 //record.setAfid(new String(face.createTemplate()));
		 record.setAge(age.longValue());
		 record.setDevice(null);
		 record.setGender(gender);
		 record.setStuff(null);
		 record.setPath(path);
		 record.setInsert(Instant.now());
		 byte[] uploadedAfid = face.createTemplate();
		 
		 
		 List<Stuff> stuffList = stuffRepository.findAll();
		 for (Iterator iterator = stuffList.iterator(); iterator.hasNext();) {
			Stuff stuff = (Stuff) iterator.next();
			Face faceStuff = faceHandler.detectFaces(stuff.getImage(), 30, 100, 1)[0]; 
			byte[] stuffAfid = faceStuff.createTemplate();
			float f = faceHandler.matchTemplate(uploadedAfid, stuffAfid);
			if(f>0.5) {
				System.out.println("match value ="+f);
				record.setStuff(stuff);
			}
		}
		 
//		 ObjectMapper objectMapper = new ObjectMapper();
//		 String message = objectMapper.writeValueAsString(record);
//		 
//		 mqttMessage.setPayload(new String(message).getBytes());
//		 mqttService.publish(mqttMessage);
		 
		 recordRepository.save(record);
		 
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
		
		 //deleteFile(path);
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
