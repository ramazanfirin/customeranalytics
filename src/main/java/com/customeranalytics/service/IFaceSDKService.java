package com.customeranalytics.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.innovatrics.commons.geom.PointF;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;
import com.innovatrics.iface.IFaceException;
import com.innovatrics.iface.enums.AgeGenderSpeedAccuracyMode;
import com.innovatrics.iface.enums.FaceCropMethod;
import com.innovatrics.iface.enums.FacedetSpeedAccuracyMode;
import com.innovatrics.iface.enums.Parameter;

@Service
public class IFaceSDKService {

	IFace iface= null;
	FaceHandler faceHandler = null;
	
	public int minEyeDistance = 14;
	public int maxEyeDistance = 200;
	
	@PostConstruct
	public void init() throws IOException {

		iface = IFace.getInstance();
		ClassPathResource cpr = new ClassPathResource("iengine.lic");
		byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
		iface.initWithLicence(bdata);
		
		faceHandler = new FaceHandler();
		faceHandler.setParam(Parameter.FACEDET_SPEED_ACCURACY_MODE, FacedetSpeedAccuracyMode.FAST.toString());
		faceHandler.setParam(Parameter.AGEGENDER_SPEED_ACCURACY_MODE, AgeGenderSpeedAccuracyMode.FAST.toString());
	}
	
	public Face[] detectFaces(BufferedImage image) throws IFaceException, IOException {
		return faceHandler.detectFaces(convertToByteArray(image), minEyeDistance, maxEyeDistance, 3);
		
	}
	
	public Face[] detectFaces(byte[] array) throws IFaceException, IOException {
		return faceHandler.detectFaces(array, minEyeDistance, maxEyeDistance, 3);
		
	}
	public PointF[] getCropRectangle(Face face) throws IFaceException, IOException {
		return face.getCropRectangle(FaceCropMethod.FULL_FRONTAL);
		
	}
	
	private byte[] convertToByteArray(BufferedImage originalImage) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( originalImage, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	public float matchTemplate(byte[] faceTemplate1,byte[] faceTemplate2) {
		return faceHandler.matchTemplate(faceTemplate1, faceTemplate2);
	}
}
