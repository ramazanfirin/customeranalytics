package com.customeranalytics.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

@Service
public class PublishService {
	
	Publisher publisher;
	ObjectMapper objectMapper = new ObjectMapper();
	List<ApiFuture<String>> futures = new ArrayList<ApiFuture<String>>();
	
	
public PublishService() throws FileNotFoundException, IOException {
		super();

		String projectId = "quixotic-sol-216511" ;
		String topicId ="projects/quixotic-sol-216511/topics/testtopic";
		
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("googlecredits.json").getFile());
		
		// Batch settings control how the publisher batches messages
		long requestBytesThreshold = 5000L; // default : 1kb
		long messageCountBatchSize = 10L; // default : 100

		Duration publishDelayThreshold = Duration.ofMillis(100); // default : 1 ms

		// Publish request get triggered based on request size, messages count & time since last publish
		BatchingSettings batchingSettings = BatchingSettings.newBuilder()
		    .setElementCountThreshold(messageCountBatchSize)
//		    .setRequestByteThreshold(requestBytesThreshold)
//		    .setDelayThreshold(publishDelayThreshold)
		    .build();

		
		ExecutorProvider executorProvider =
			    InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(5).build();

		
		publisher = Publisher.newBuilder(topicId)
				.setCredentialsProvider(FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(file))))
				.setBatchingSettings(batchingSettings)
				.setExecutorProvider(executorProvider)
				.build()
				;
}


public void writeToQuene(String message) throws FileNotFoundException, IOException {
	ByteString data = ByteString.copyFromUtf8(message);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
        .setData(data)
        .build();
    
    ApiFuture<String> future = publisher.publish(pubsubMessage);
    futures.add(future);
    
    
}

@PreDestroy
public void checkAllPublishedMessages() throws Exception {
	List<String> messageIds = ApiFutures.allAsList(futures).get();

    for (String messageId : messageIds) {
      System.out.println(messageId);
    }

    if (publisher != null) {
      // When finished with the publisher, shutdown to free up resources.
    	publisher.shutdown();
    }
}
	
//public static void main(String[] args) throws Exception {
//	Publish testTemp = new Publish();
//	
//	ObjectMapper objectMapper = new ObjectMapper();
//	for (int i = 0; i < 20; i++) {
//		RecordData recordData = new RecordData(String.valueOf(i));
//		testTemp.writeToQuene(objectMapper.writeValueAsString(recordData));
//		
//	}
//	
//	testTemp.checkAllPublishedMessages();
//	
//	System.out.println("bitti");
//}
}

