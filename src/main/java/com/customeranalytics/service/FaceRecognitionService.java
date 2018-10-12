package com.customeranalytics.service;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import Luxand.FSDK;
import Luxand.FSDK.FSDK_Features;
import Luxand.FSDK.HImage;
import Luxand.FSDK.TFacePosition;
import Luxand.FSDK.TFaces;

@Service
public class FaceRecognitionService {

	@PostConstruct
	public void init() {
		try {
            int r = FSDK.ActivateLibrary("FgsONyEWyINCBz0lbgccL7LMjLMsgAbHxwgdNLt0Q1j8UTmTgZyeaeCoXno1HBydmshM4ygfBO+6/qlKhFZAF5BvVaSKx7NaV0fIFPtkRie2h1DMmKIYa15N7qBP/DsclEoom67W6fXIyRo4yBPhemiu53rXVsqfvmMOuiu7KhQ=");
            if (r != FSDK.FSDKE_OK){
                 System.exit(r);
            }
		}catch(java.lang.UnsatisfiedLinkError e) {
          System.exit(1);
		} 
		
		 FSDK.Initialize();
	}
	
	//@Async
	public void analize(String path) {
		  System.out.println("istek geldi");
		 Long start = System.currentTimeMillis();
		 HImage imageHandle = new HImage();
		
		 if (FSDK.LoadImageFromFileW(imageHandle, path) == FSDK.FSDKE_OK){
			 TFaces faceArray = new TFaces();
			 FSDK.TFacePosition.ByReference facePosition = new FSDK.TFacePosition.ByReference();
			 
			 if (FSDK.DetectMultipleFaces( imageHandle,faceArray) == FSDK.FSDKE_OK){
			 
				 if(faceArray.faces.length==0)
					 System.out.println("no face detected");
					 
					 
				 	for (int i = 0; i < faceArray.faces.length; i++) {
						TFacePosition position =faceArray.faces[i];
					  
						String [] AttributeValues = new String[1];
                       String [] AttributeValuesAge = new String[1];
					    
                       FSDK_Features.ByReference facialFeatures = new FSDK_Features.ByReference();
                       FSDK.DetectFacialFeaturesInRegion(imageHandle, position, facialFeatures);
                       
                       //facialFeatures.;
                       
                       System.out.println("sdfsdf");
                       
					    int res =  FSDK.DetectFacialAttributeUsingFeatures(
					    		imageHandle,
					    		facialFeatures, 
					    		"Gender", 
					    		AttributeValues, 1024);
					    
					    int res2 =  FSDK.DetectFacialAttributeUsingFeatures(
					    		imageHandle,
					    		facialFeatures, 
					    		"Age", 
					    		AttributeValuesAge, 1024);
					    
					    String[] result = AttributeValues[0].split(";");
					    System.out.println("male rate is"+result[0]);
					    System.out.println("female rate is"+result[1]);
					    
					    System.out.println(AttributeValuesAge[0]);
					
					   
				
				 	}
				 
			 }
		 }else {
			 System.out.println("file readErrod");
		 }
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
