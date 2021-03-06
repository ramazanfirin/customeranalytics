package com.customeranalytics.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guichaguri.minimalftp.FTPServer;
import com.guichaguri.minimalftp.impl.NativeFileSystem;
import com.guichaguri.minimalftp.impl.NoOpAuthenticator;

import com.guichaguri.minimalftp.impl.UserbaseAuthenticator;;

@Service
public class FtpService {
	
	@Autowired
	FaceRecognitionService faceRecognitionService;

	@PostConstruct
	public void init() throws IOException {
		//startFtpServer();
	}
	
	
	public void startFtpServer() throws IOException {
		// Uses the current working directory as the root
		File root = new File(System.getProperty("user.dir"));

		// Creates a native file system
		NativeFileSystem fs = new NativeFileSystem(root);

		// Creates a noop authenticator, which allows anonymous authentication
		NoOpAuthenticator auth = new NoOpAuthenticator(fs);

		// Create our custom authenticator
        UserbaseAuthenticator auth2 = new UserbaseAuthenticator();

        // Register a few users
        auth2.registerUser("ramazan", "ra5699mo");
        auth2.registerUser("alex", "abcd123");
        auth2.registerUser("hannah", "98765");
		
		
		// Creates the server with the authenticator
		FTPServer server = new FTPServer(auth2,faceRecognitionService);
      // faceRecognitionService.analize("324234"); 
		// Start listening synchronously
		System.out.println("ftp server startted");
		server.listenSync(14147);

	}
}
