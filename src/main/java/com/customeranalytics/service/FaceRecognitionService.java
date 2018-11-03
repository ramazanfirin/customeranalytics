package com.customeranalytics.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
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
import com.innovatrics.commons.geom.PointF;
import com.innovatrics.commons.img.RawBGRImage;
import com.innovatrics.commons.img.RawImage;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;
import com.innovatrics.iface.IFaceException;
import com.innovatrics.iface.enums.AgeGenderSpeedAccuracyMode;
import com.innovatrics.iface.enums.FaceAttributeId;
import com.innovatrics.iface.enums.FaceCropMethod;
import com.innovatrics.iface.enums.FaceFeatureId;
import com.innovatrics.iface.enums.FacedetSpeedAccuracyMode;
import com.innovatrics.iface.enums.Parameter;
import com.innovatrics.iface.enums.SegmentationImageType;

@Service
public class FaceRecognitionService {


	@Autowired
	RecordService recordService;

	@Autowired
	StuffRepository stuffRepository;

	@Autowired
	IFaceSDKService iFaceSDKService;
	 
	
	private BufferedImage loadImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		if(image==null) {
			throw new RuntimeException("no file loaded");
		}
		
		return image;
	}
	 
	private Face[] getFaces(BufferedImage image) throws IFaceException, IOException {
		Face[] faces = iFaceSDKService.detectFaces(image);
		if (faces.length == 0) {
			throw new RuntimeException("no face detected");
		}
		return faces;
	}
	
	private Gender getGender(Float genderValue) {
		Gender gender=null ;
		if(genderValue<0)
	    	 gender = Gender.MALE;
	     else
	    	 gender = Gender.FEMALE;
		
		return gender;
	}
	
	private String recordFaceImage(BufferedImage image,Face face,String path) {
		String filename;
		try {
			PointF[] points =iFaceSDKService.getCropRectangle(face);
			filename = "/tmp/testimages/"+UUID.randomUUID()+".png";
			
			float w = points[1].getX()-points[0].getX(); 
			float h = points[3].getY()-points[0].getY(); 
			
			BufferedImage subImg = image.getSubimage((int) points[0].getX(), (int) points[0].getY(), (int) w, (int) h);
			ImageIO.write(subImg, "png", new File(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			filename = path;
		}
		
		return filename;
	}
	
	//@Async
	public void analize(String path) throws MqttPersistenceException, MqttException, IOException {
	
		BufferedImage image = loadImage(path);
		Face[] faces = getFaces(image);
		
		for (int i = 0; i < faces.length; i++) {
			Face face = faces[i];
			Float age = face.getAttribute(FaceAttributeId.AGE);
			Float genderValue = face.getAttribute(FaceAttributeId.GENDER);
			Gender gender  = getGender(genderValue);
	        String tempPath = recordFaceImage(image, face, path);
			byte[] afid = face.createTemplate();
			recordService.save(age, gender, null, tempPath, afid);
		} 

		
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
		
		// deleteFile(path);
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
