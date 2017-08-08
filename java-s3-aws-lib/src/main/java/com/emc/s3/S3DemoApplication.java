package com.emc.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


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
	public AmazonS3Client getAmazonS3Client() {

		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);
		clientConfig.setSignerOverride("AWSS3V4SignerType");
		AmazonS3Client client = new AmazonS3Client(creds, clientConfig);
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		client.setRegion(usEast1);
		client.setEndpoint("http://localhost:9000");
		final S3ClientOptions clientOptions = S3ClientOptions.builder().setPathStyleAccess(true).build();
		client.setS3ClientOptions(clientOptions);
		return client;
	}

}
