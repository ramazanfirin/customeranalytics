package com.customeranalytics.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.customeranalytics.CustomeranalyticsApp;
import com.customeranalytics.domain.Record;
import com.customeranalytics.domain.Stuff;
import com.customeranalytics.domain.enumeration.Gender;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomeranalyticsApp.class)
@Transactional
public class GoogleCloudServiceIntTest {

    @Autowired
    private GoogleCloudService googleCloudService;

    Record record; 
    
    @Before
    public void initTest() {
        record = createEntity();
    }
    
    public static Record createEntity() {
        Record record = new Record();
        record.setAfid("afid");
        record.setAge(30l);
        record.device(null);
        record.setGender(Gender.FEMALE);
        record.setInsert(Instant.now());
        record.setPath("/asdasd/asdasd");
        
        Stuff stuff = new Stuff();
        stuff.setName("sdf");
        stuff.setSurname("vvvv");
        
        record.setStuff(stuff);
        return record;
        
    }
    
    @Test
    public void insert() throws FileNotFoundException, IOException {
    	googleCloudService.insertToBigQuery(record);
    }

    //@Test
    public void insert2() throws FileNotFoundException, IOException {
    	Record[] list = new Record[100];
    	for (int i = 0; i < 100; i++) {
			list[i]=record;
		}
    	googleCloudService.insertToBigQuery(list);
    }
    
}
