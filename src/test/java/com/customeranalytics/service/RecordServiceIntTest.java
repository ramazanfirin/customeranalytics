package com.customeranalytics.service;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.customeranalytics.CustomeranalyticsApp;
import com.customeranalytics.domain.Record;
import com.customeranalytics.domain.enumeration.Gender;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomeranalyticsApp.class)
@Transactional
public class RecordServiceIntTest {

    @Autowired
    private RecordService recordService;

    Record record; 
    
    @Test
    public void insert() throws FileNotFoundException, IOException {
    	
    	recordService.save(25f, Gender.MALE, null,"/asd/asd", "afidsfd".getBytes());
    	List<Record> list = recordService.getRecordRepository().findAll();
    	
    	assertThat(list.size()).isEqualTo(1);
    } 

}
