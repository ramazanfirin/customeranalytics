package com.customeranalytics.service;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.customeranalytics.domain.Record;
import com.customeranalytics.repository.RecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;
import com.innovatrics.iface.enums.AgeGenderSpeedAccuracyMode;
import com.innovatrics.iface.enums.FacedetSpeedAccuracyMode;
import com.innovatrics.iface.enums.Parameter;

//@Service
public class MqttService implements MqttCallback{
	
	
	MqttClient client;
	IFace iface= null;
	FaceHandler faceHandler = null;
	
	
	@Autowired
	RecordRepository recordRepository;
	
	public MqttService() throws MqttException, IOException {
		super();
		init();
	}

	//@PostConstruct
	public void init() throws MqttException, IOException {
		client=new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
		client.setCallback( this );
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		client.connect(options);
		
		client.subscribe("face");
		
		
	}
	
	public void publish(MqttMessage message) throws MqttPersistenceException, MqttException {
		client.publish("face", message);
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		  System.out.println("Connection to MQTT broker lost!");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		 System.out.println("Message received:"+ new String(message.getPayload()) );
		 ObjectMapper objectMapper = new ObjectMapper();
		 Record record = objectMapper.readValue(new String(message.getPayload()), Record.class);
		 recordRepository.save(record);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

}
