package com.customeranalytics.service;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.customeranalytics.CustomeranalyticsApp;
import com.customeranalytics.domain.Record;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomeranalyticsApp.class)
@Transactional
public class FaceRecognitionServiceIntTest {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    Record record; 
    
    @Test
    public void insert() throws FileNotFoundException, IOException, MqttPersistenceException, MqttException, InterruptedException {
    	
//    	byte[] bytes = "hello world".getBytes();
//    	 
//    	//Convert byte[] to String
//    	String s = Base64.getEncoder().encodeToString(bytes);
//    	 
//    	System.out.println(s);
//    	
//    	ClassLoader classLoader = getClass().getClassLoader();
//		File file = new File(classLoader.getResource("face.jpeg").getFile());
//    	
    	String path = "/home/ramazan/Desktop/90900fc7-ef04-40f2-a33e-1f2d28f98944.jpg";
    	faceRecognitionService.analize(path);
    	Thread.sleep(20000);
    } 

}
