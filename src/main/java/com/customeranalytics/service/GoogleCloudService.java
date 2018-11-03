package com.customeranalytics.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.customeranalytics.config.ApplicationProperties;
import com.customeranalytics.domain.Record;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.common.collect.ImmutableList;

@Service
public class GoogleCloudService {
	
	private BigQuery bigquery;
	
	private final ApplicationProperties applicationProperties;

	public GoogleCloudService(ApplicationProperties applicationProperties) throws FileNotFoundException, IOException {
		super();
		this.applicationProperties = applicationProperties;
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("googlecredits.json").getFile());
		
		bigquery =BigQueryOptions.newBuilder()
			    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(file)))
			    .build()
			    .getService();
		
	}

	public void insertToBigQuery(Record... recordlist) throws FileNotFoundException, IOException {
			
		List<RowToInsert> input = convertTolist(recordlist);
		InsertAllRequest insertRequest = InsertAllRequest.newBuilder(applicationProperties.getDatasetId(),applicationProperties.getTableId())
			    .setRows(input)
			    .build();
		
		Long start = System.currentTimeMillis();
		InsertAllResponse insertResponse = bigquery.insertAll(insertRequest);
		Long end = System.currentTimeMillis();
		
		System.out.println("duration="+ (end-start));
		
		if (insertResponse.hasErrors()) {
			  throw new RuntimeException("bigguqery insert error");
		}
	}
	
	private List<RowToInsert> convertTolist(Record... recordlist) {
		
		List<RowToInsert> result  = new ArrayList<RowToInsert>();
		for (int i = 0; i < recordlist.length; i++) {
			Record record = recordlist[i];
			
			Map<String, Object> firstRow = new HashMap<>();
			firstRow.put("uuid", UUID.randomUUID().toString());
			firstRow.put("age", recordlist[0].getAge());
			firstRow.put("gender", recordlist[0].getGender().toString());
			firstRow.put("afid", recordlist[0].getAfid());
			firstRow.put("company", "test");
			firstRow.put("branch", "merkez");
			firstRow.put("device", "Giris Kapisi");
			firstRow.put("insertdate", recordlist[0].getInsert().getEpochSecond());
			if(recordlist[0].getStuff()!=null)
				firstRow.put("stuff", recordlist[0].getStuff().getName()+" "+recordlist[0].getStuff().getSurname());
			
			result.add(RowToInsert.of(firstRow));
		}
		
		return result;
	}
}
	
	
