package com.customeranalytics.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.customeranalytics.domain.Device;
import com.customeranalytics.domain.Record;
import com.customeranalytics.domain.Stuff;
import com.customeranalytics.domain.enumeration.Gender;
import com.customeranalytics.repository.RecordRepository;
import com.customeranalytics.repository.StuffRepository;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;

@Service
public class RecordService {

	final RecordRepository recordRepository;
	
	public RecordRepository getRecordRepository() {
		return recordRepository;
	}

	final GoogleCloudService googleCloudService;
	
	final StuffRepository stuffRepository;
	
	IFace iface= null;
	FaceHandler faceHandler = null;
	
	public RecordService(RecordRepository recordRepository,GoogleCloudService googleCloudService,StuffRepository stuffRepository) {
		super();
		this.recordRepository = recordRepository;
		this.googleCloudService = googleCloudService;
		this.stuffRepository = stuffRepository;
	}

	@Async
	public void save(Float age,Gender gender,Device device,String path,byte[] afid) throws FileNotFoundException, IOException {
		Record record  = convertToRecord(age, gender, device, path, afid); 
		record.setStuff(getStuff(afid));
		
		recordRepository.save(record);
		googleCloudService.insertToBigQuery(record);
	}
	
	private Record convertToRecord(Float age,Gender gender,Device device,String path,byte[] afid) {
		Record record = new Record();
		record.setAfid(Base64.getEncoder().encodeToString(afid));
		record.setAge(age.longValue());
		record.setDevice(device);
		record.setGender(gender);
		record.setPath(path);
		record.setInsert(Instant.now());
	
		return record;
	}
	
	public Stuff getStuff(byte[] afid) {
		HashMap<byte[],Stuff> stuffList = new HashMap<byte[],Stuff>();
		for (Iterator iterator = stuffList.keySet().iterator(); iterator.hasNext();) {
			byte[] stuffAfid = (byte[]) iterator.next();
			float f = faceHandler.matchTemplate(stuffAfid, afid);
			if(f>0.5) {
				return stuffList.get(stuffAfid);
			}
		}
		
		return null;
	}
	
	public Map<byte[],Stuff> getStuffList(){
		HashMap<byte[],Stuff> result = new HashMap<byte[],Stuff>();
		
		List<Stuff> stuffList = stuffRepository.findAll();
		for (Iterator iterator = stuffList.iterator(); iterator.hasNext();) {
			Stuff stuff = (Stuff) iterator.next();
			Face faceStuff = faceHandler.detectFaces(stuff.getImage(), 30, 100, 1)[0]; 
			byte[] stuffAfid = faceStuff.createTemplate();
			result.put(stuffAfid, stuff);
		}

		return result;
	}
	
}
	
	
