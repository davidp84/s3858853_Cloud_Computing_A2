package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import model.LoggedInUser;
import model.Song;
import model.User;

public class MainController {

	// Scans database with int year as parameter
	public List<Song> querySongs(int year) {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("music");

		List<Song> songs = new ArrayList<Song>();

		ScanSpec scanSpec = new ScanSpec().withProjectionExpression("artist, title, #yr")
				.withFilterExpression("#yr = :year").withNameMap(new NameMap().with("#yr", "year"))
				.withValueMap(new ValueMap().withNumber(":year", year));

		try {
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);

			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				Song song = new Song();
				song.setArtist(item.getString("artist"));
				song.setTitle(item.getString("title"));
				song.setImage(item.getString("image_url"));
				song.setWeb_url(item.getString("web_url"));
				song.setYear(item.getInt("year"));
				songs.add(song);
				System.out.println("" + item.toString());
			}

		} catch (Exception e) {
			System.err.println("Unable to scan the table:");
			System.err.println(e.getMessage());
		}

		return songs;
	}

	// Scans database with int year and HashMap as the parameters
	public List<Song> querySongs(HashMap<String, String> query, int year) {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("music");

		HashMap<String, String> nameMap = new HashMap<String, String>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();

		List<Song> songs = new ArrayList<Song>();
		
		int selector = 0;
		String expression = "";

		// Determines what the query is for
		if (query.containsKey("artist") && query.containsKey("title")) {
			selector = 1;
		} else if (query.containsKey("artist")) {
			selector = 2;
		} else if (query.containsKey("title")) {
			selector = 3;
		}

		// Builds the query based off of what it is for
		switch (selector) {
		case 0:
			break;
		case 1:
			nameMap.put("#te", "title");
			nameMap.put("#yr", "year");
			nameMap.put("#at", "artist");
			valueMap.put(":title", query.get("title"));
			valueMap.put(":year", year);
			valueMap.put(":artist", query.get("artist"));
			expression = "#te = :title and #at = :artist and #yr = :year";
			break;
		case 2:
			nameMap.put("#yr", "year");
			nameMap.put("#at", "artist");
			valueMap.put(":year", year);
			valueMap.put(":artist", query.get("artist"));
			expression = "#at = :artist and #yr = :year";
			break;
		case 3:
			nameMap.put("#te", "title");
			nameMap.put("#yr", "year");
			valueMap.put(":title", query.get("title"));
			valueMap.put(":year", year);
			expression = "#te = :title and #yr = :year";
			break;
		default:
			break;
		}

		ScanSpec scanSpec = new ScanSpec().withProjectionExpression("artist, title, #yr")
				.withFilterExpression(expression).withNameMap(nameMap).withValueMap(valueMap);

		try {
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);
			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				Song song = new Song();
				song.setArtist(item.getString("artist"));
				song.setTitle(item.getString("title"));
				song.setImage(item.getString("image_url"));
				song.setWeb_url(item.getString("web_url"));
				song.setYear(item.getInt("year"));
				songs.add(song);
				System.out.println("" + item.toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to scan the table:");
			System.err.println(e.getMessage());
		}
		return songs;
	}

	// Scans database with HashMap as the only parameter
	public List<Song> querySongs(HashMap<String, String> query) {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("music");

		HashMap<String, String> nameMap = new HashMap<String, String>();
		HashMap<String, Object> valueMap = new HashMap<String, Object>();

		List<Song> songs = new ArrayList<Song>();
		
		int selector = 0;
		String expression = "";

		// Determines what the query is for
		if (query.containsKey("artist") && query.containsKey("title")) {
			selector = 1;
		} else if (query.containsKey("artist")) {
			selector = 2;
		} else if (query.containsKey("title")) {
			selector = 3;
		}

		// Builds the query based off of what it is for
		switch (selector) {
		case 0:
			break;
		case 1:
			nameMap.put("#te", "title");
			nameMap.put("#yr", "year");
			nameMap.put("#at", "artist");
			valueMap.put(":title", query.get("title"));
			valueMap.put(":artist", query.get("artist"));
			expression = "#te = :title and #at = :artist";
			break;
		case 2:
			nameMap.put("#yr", "year");
			nameMap.put("#at", "artist");
			valueMap.put(":artist", query.get("artist"));
			expression = "#at = :artist";
			break;
		case 3:
			nameMap.put("#te", "title");
			nameMap.put("#yr", "year");
			valueMap.put(":title", query.get("title"));
			expression = "#te = :title";
			break;
		default:
			break;
		}

		ScanSpec scanSpec = new ScanSpec().withProjectionExpression("artist, title, #yr")
				.withFilterExpression(expression).withNameMap(nameMap).withValueMap(valueMap);

		try {
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);
			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				Song song = new Song();
				song.setArtist(item.getString("artist"));
				song.setTitle(item.getString("title"));
				song.setImage(item.getString("image_url"));
				song.setWeb_url(item.getString("web_url"));
				song.setYear(item.getInt("year"));
				songs.add(song);
				System.out.println("" + item.toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to scan the table:");
			System.err.println(e.getMessage());
		}
		return songs;
	}

	// Adds song to user's subscription database
	public void subscribeToSong(String title, String artist) {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("music");

//		HashMap<String, String> nameMap = new HashMap<String, String>();
//		nameMap.put("#te", "title");
//		nameMap.put("#yr", "year");
//		nameMap.put("#at", "artist");
//
//		HashMap<String, Object> valueMap = new HashMap<String, Object>();
//		valueMap.put(":title", title);
//		valueMap.put(":year", year);
//		valueMap.put(":artist", artist);

		List<String> subscriber = new ArrayList<String>();

		subscriber.add(LoggedInUser.USER_NAME);

		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("artist", artist, "title", title)
				.withUpdateExpression("set subscribed = :s")
				.withValueMap(new ValueMap().withStringSet(":s", (Set<String>) subscriber))
				.withReturnValues(ReturnValue.UPDATED_NEW);

		try {
			System.out.println("Subscribing to song...");
			UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
			System.out.println("Song subscribed:\n" + outcome.getItem().toJSONPretty());

		} catch (Exception e) {
			System.err.println("Unable to susbcribe song: " + title);
			System.err.println(e.getMessage());
		}
	}

	// Returns all songs subscribed by the user
	public List<Song> getSubscribedSongs(String user) {
		return null;		
	}
	
	// Gets Image from s3 storage
	public String getImage() {
		// Query s3 storage for link to use in img tag.
		return null;
	}
}
