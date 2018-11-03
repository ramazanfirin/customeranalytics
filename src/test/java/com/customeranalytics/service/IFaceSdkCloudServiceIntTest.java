package com.customeranalytics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.customeranalytics.CustomeranalyticsApp;
import com.customeranalytics.domain.Record;
import com.innovatrics.iface.Face;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomeranalyticsApp.class)
@Transactional
public class IFaceSdkCloudServiceIntTest {

    @Autowired
    private IFaceSDKService iFaceSDKService;

    Record record; 
    
    @Test
    public void insert() throws FileNotFoundException, IOException {
    	ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("face.jpeg").getFile());
    	
    	BufferedImage image = ImageIO.read(file);
    	Face[] faces = iFaceSDKService.detectFaces(image);
    	
    	assertThat(faces.length).isEqualTo(1);
    }

}
