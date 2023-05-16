package data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/*
100ferhas, “Java Web Application (JSP/Servlets) startup script,” Stack Overflow, 14-Aug-2018. [Online].
Available: https://stackoverflow.com/questions/51842659/java-web-application-jsp-servlets-startup-script.
[Accessed: 20-Apr-2022].
*/
@WebListener
public class WebAppServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			// Create's the music table
			CreateTable();
			// uploads the .json data into the table
			SeedData();
			// creates an s3 bucket
			CreateBucket();
			// uploads images into bucket
			UploadImages();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

	// Only creates the table if it doesn't exist
	private void CreateTable() throws Exception {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		String tableName = "music";

		try {
			System.out.println("Attempting to create table; please wait...");
			Table table = dynamoDB.createTable(tableName, Arrays.asList(new KeySchemaElement("artist", KeyType.HASH), // Partition
																														// key
					new KeySchemaElement("title", KeyType.RANGE)), // Sort key
					Arrays.asList(new AttributeDefinition("artist", ScalarAttributeType.S),
							new AttributeDefinition("title", ScalarAttributeType.S)),
					new ProvisionedThroughput(10L, 10L));
			table.waitForActive();
			System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

		} catch (Exception e) {
			System.err.println("Unable to create table: ");
			System.err.println(e.getMessage());
		}
	}

	// Seeds the data in the table if required
	private void SeedData() throws Exception {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("music");

		GetItemSpec spec = new GetItemSpec().withPrimaryKey("artist", "Elton John", "title", "Rocket Man");
		Item outcome = null;
		try {
			System.out.println("Attempting to read the item...");
			outcome = table.getItem(spec);

		} catch (Exception e) {
			System.err.println("Unable to read item: ");
			System.err.println(e.getMessage());
		}

		if (outcome != null) {
			System.out.println("Table already seeded!");
			return;
		}
		JsonParser parser = new JsonFactory().createParser(new File("a2.json"));
		JsonNode rootNode = new ObjectMapper().readTree(parser);
		Iterator<JsonNode> iter = rootNode.iterator();

		ArrayNode currentNode;

		while (iter.hasNext()) {
			currentNode = (ArrayNode) iter.next();
			for (int i = 0; i < currentNode.size(); i++) {
				JsonNode song = currentNode.get(i);
				String title = song.findValue("title").asText();
				String artist = song.findValue("artist").asText();
				int year = song.findValue("year").asInt();
				String web_url = song.findValue("web_url").asText();
				String img_url = song.findValue("img_url").asText();

				try {
					table.putItem(new Item().withPrimaryKey("artist", artist, "title", title).withNumber("year", year)
							.withString("web_url", web_url).withString("image_url", img_url));
					System.out.println("PutItem succeeded: " + year + " " + title);
				} catch (Exception e) {
					System.err.println("Unable to add music: " + year + " " + title);
					System.err.println(e.getMessage());
					break;
				}
			}
		}
		parser.close();
	}

	private void CreateBucket() throws Exception {
		Regions clientRegion = Regions.US_EAST_1;
		String bucketName = "s3858853-a2-images";

		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider())
					.withRegion(clientRegion).build();

			if (!s3Client.doesBucketExist(bucketName)) {
				// Because the CreateBucketRequest object doesn't specify a region, the
				// bucket is created in the region specified in the client.
				s3Client.createBucket(new CreateBucketRequest(bucketName));

				// Verify that the bucket was created by retrieving it and checking its
				// location.
				String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
				System.out.println("Bucket location: " + bucketLocation);
			}
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it and returned an error response.
			e.printStackTrace();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}
	}

	private void UploadImages() throws Exception {
		Regions clientRegion = Regions.US_EAST_1;
		String bucketName = "s3858853-a2-images";
		String fileObjKeyName = "";// This part can be empty

		JsonParser parser = new JsonFactory().createParser(new File("a2.json"));
		JsonNode rootNode = new ObjectMapper().readTree(parser);
		Iterator<JsonNode> iter = rootNode.iterator();

		ArrayNode currentNode;

		AmazonS3 s3Client = null;

		try {

			s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			e.printStackTrace();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}

		S3Object fullObject = null;
		String key = "https://raw.githubusercontent.com/davidpots/songnotes_cms/master/public/images/artists/ArcadeFire.jpg";
		String outcome = null;
		// Get an object and print its contents.
		fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
		outcome = fullObject.getObjectMetadata().getContentType();

		if (outcome.equals(".jpg")) {
			System.out.println("Images already uploaded!");
			return;
		}

		while (iter.hasNext()) {
			currentNode = (ArrayNode) iter.next();
			for (int i = 0; i < currentNode.size(); i++) {
				JsonNode song = currentNode.get(i);
				String img_url = song.findValue("img_url").asText();
				String title = song.findValue("title").asText();
				fileObjKeyName = img_url;

				try {
					/*
					 * Alex - GlassEditor.com, “How to download image from any web page in Java,”
					 * Stack Overflow, 09-Sep-2015. [Online]. Available:
					 * https://stackoverflow.com/questions/5882005/how-to-download-image-from-any-
					 * web-page-in-java. [Accessed: 24-Apr-2022].
					 */
					URL url = new URL(img_url);
					InputStream in = new BufferedInputStream(url.openStream());
					/*
					 * Shehan Simen and Gary, “Is it possible to create a file object from
					 * InputStream,” Stack Overflow, 26-Jun-2018. [Online]. Available:
					 * https://stackoverflow.com/questions/11501418/is-it-possible-to-create-a-file-
					 * object-from-inputstream. [Accessed: 24-Apr-2022].
					 */
					File tempFile = File.createTempFile(title, ".jpg");
					tempFile.deleteOnExit();
					FileOutputStream out = new FileOutputStream(tempFile);
					IOUtils.copy(in, out);
					out.close();
					in.close();

					System.out.println("Uploading to Bucket: " + img_url);
					// Upload a file as a new object with ContentType and title specified.
					PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, tempFile);
					ObjectMetadata metadata = new ObjectMetadata();
					metadata.setContentType(".jpg");
					metadata.addUserMetadata("title", title);
					request.setMetadata(metadata);
					s3Client.putObject(request);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		parser.close();
	}

}