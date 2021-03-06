package com.customeranalytics.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.customeranalytics.domain.Device;
import com.customeranalytics.domain.Record;
import com.customeranalytics.domain.Stuff;
import com.customeranalytics.domain.enumeration.Gender;
import com.customeranalytics.repository.RecordRepository;
import com.customeranalytics.repository.StuffRepository;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.IFaceException;

@Service
public class RecordService {
	
    private final Logger log = LoggerFactory.getLogger(UserService.class);
	
    final RecordRepository recordRepository;
	
	public RecordRepository getRecordRepository() {
		return recordRepository;
	}

	final GoogleCloudService googleCloudService;
	
	final StuffRepository stuffRepository;
	
	final IFaceSDKService iFaceSDKService;
	
	List<Record> safeList = Collections.synchronizedList(new ArrayList<Record>());
	
	public RecordService(IFaceSDKService iFaceSDKService,RecordRepository recordRepository,GoogleCloudService googleCloudService,StuffRepository stuffRepository) {
		super();
		this.recordRepository = recordRepository;
		this.googleCloudService = googleCloudService;
		this.stuffRepository = stuffRepository;
		this.iFaceSDKService = iFaceSDKService;
	}

	@Async
	public void save(Float age,Gender gender,Device device,String path,byte[] afid) throws FileNotFoundException, IOException {
		Record record  = convertToRecord(age, gender, device, path, afid); 
		record.setStuff(getStuff(afid));
		//safeList.add(record);
		recordRepository.save(record);
		
//		if(safeList.size()>999) {
//			List<Record> tempList = safeList.stream()
//					  .collect(Collectors.toList());
//			tempList.addAll(safeList);
//			recordRepository.save(tempList);
//			googleCloudService.insertToBigQuery(tempList.toArray(new Record[tempList.size()]));
//			safeList.clear();
//			log.info("insert tamamlandı."+safeList.size());
//		}	
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
	
	public Stuff getStuff(byte[] afid) throws IFaceException, IOException {
		HashMap<byte[],Stuff> stuffList = getStuffList();
		for (Iterator iterator = stuffList.keySet().iterator(); iterator.hasNext();) {
			byte[] stuffAfid = (byte[]) iterator.next();
			float f = iFaceSDKService.matchTemplate(stuffAfid, afid);
			if(f>750) {
				return stuffList.get(stuffAfid);
			}
		}
		
		return null;
	}
	
	public HashMap<byte[],Stuff> getStuffList() throws IFaceException, IOException{
		HashMap<byte[],Stuff> result = new HashMap<byte[],Stuff>();
		
		List<Stuff> stuffList = stuffRepository.findAll();
		for (Iterator iterator = stuffList.iterator(); iterator.hasNext();) {
			Stuff stuff = (Stuff) iterator.next();
			Face faceStuff = iFaceSDKService.detectFaces(stuff.getImage())[0]; 
			byte[] stuffAfid = faceStuff.createTemplate();
			result.put(stuffAfid, stuff);
		}

		return result;
	}
	
}
	
	
