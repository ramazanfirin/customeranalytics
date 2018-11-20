package com.customeranalytics.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.customeranalytics.domain.enumeration.Gender;
import com.customeranalytics.repository.StuffRepository;
import com.customeranalytics.service.dto.AfidDto;
import com.innovatrics.commons.geom.PointF;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.IFaceException;
import com.innovatrics.iface.enums.FaceAttributeId;

@Service
public class FaceRecognitionService {


	@Autowired
	RecordService recordService;

	@Autowired
	StuffRepository stuffRepository;

	@Autowired
	IFaceSDKService iFaceSDKService;
	 
	final BlockingQueue<AfidDto> linkedBlockingQueue = new LinkedBlockingQueue<AfidDto>(20);
	
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
			filename = "/home/ramazan/testimages/"+UUID.randomUUID()+".png";
			
			float w = points[1].getX()-points[0].getX(); 
			float h = points[3].getY()-points[0].getY(); 
			
			
			int startx=0,starty=0,width=0,heigth=0;
			if(points[0].getX()>0)
				startx = (int) points[0].getX();
			if(points[0].getY()>0)
				starty = (int) points[0].getY();
			if(w>=image.getWidth())
				width=image.getWidth();
			else {
				width = (int)w;
			}
			if(h>image.getHeight()) {
				heigth=image.getHeight();
			}else {
				heigth = (int)h;
			}
			
			BufferedImage subImg = image.getSubimage(startx, starty, width, heigth);
			ImageIO.write(subImg, "png", new File(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			filename = path;
		}
		
		return filename;
	}
	
	public Boolean checkOldRecord(byte[] currentAfid) throws InterruptedException {
		for (Iterator iterator = linkedBlockingQueue.iterator(); iterator.hasNext();) {
			AfidDto afidDto = (AfidDto) iterator.next();
			
			if(iFaceSDKService.matchTemplate(afidDto.getAfid(), currentAfid)> 750) {
				Date currentDate = new Date();
				if(currentDate.getTime()-afidDto.getDate().getTime()<60000) {
					System.out.println("afid detected but not expired");
					return false;
				}
			}
		}
		if(linkedBlockingQueue.size()>20)
			linkedBlockingQueue.take();
		
		linkedBlockingQueue.put(new AfidDto(currentAfid, new Date()));
		return true;
	}
	
	//@Async
	public void analize(String path) throws MqttPersistenceException, MqttException, IOException, InterruptedException {
	
		BufferedImage image = loadImage(path);
		Face[] faces = getFaces(image);
		
		for (int i = 0; i < faces.length; i++) {
			Face face = faces[i];
			Float age = face.getAttribute(FaceAttributeId.AGE);
			Float genderValue = face.getAttribute(FaceAttributeId.GENDER);
			Gender gender  = getGender(genderValue);
	        String tempPath = recordFaceImage(image, face, path);
			byte[] afid = face.createTemplate();
			if(checkOldRecord(afid))
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
