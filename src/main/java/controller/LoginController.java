package controller;

import java.util.HashMap;
import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import model.LoggedInUser;
import model.User;

public class LoginController {

	public User checkLogin(String email, String password) throws Exception {

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("login");

		HashMap<String, String> nameMap = new HashMap<String, String>();
		nameMap.put("#em", "email");

		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":email", email);

		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#em = :email").withNameMap(nameMap)
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = null;
		Iterator<Item> iterator = null;
		Item item = null;
		User user = new User();

		try {
			System.out.println("Checking login details!");
			items = table.query(querySpec);

			iterator = items.iterator();
			while (iterator.hasNext()) {
				item = iterator.next();
				System.out.println(item.getString("email") + ": " + item.getString("user_name"));
				if (password.equals(item.getString("password"))) {
					user.setEmail(item.getString("email"));
					user.setPassword(item.getString("password"));
					user.setUser_name(item.getString("user_name"));
					return user;
				}
			}

		} catch (Exception e) {
			System.err.println("Unable to query login details");
			System.err.println(e.getMessage());
		}
		System.out.println("No match found!");
		return null;
	}
}
