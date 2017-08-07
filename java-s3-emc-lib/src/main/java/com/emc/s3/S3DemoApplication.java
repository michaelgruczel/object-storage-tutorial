package com.emc.s3;

import com.amazonaws.auth.BasicAWSCredentials;
import com.emc.vipr.services.s3.ViPRS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.URI;

@SpringBootApplication
public class S3DemoApplication {

	@Value("${url}")
	String url;

	@Value("${accessKey}")
	String accessKey;

	@Value("${secretKey}")
	String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(S3DemoApplication.class, args);
	}

	@Bean
	public ViPRS3Client getViPRS3Client() {
		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		ViPRS3Client client = new ViPRS3Client(url, creds);
		return client;
	}

}
