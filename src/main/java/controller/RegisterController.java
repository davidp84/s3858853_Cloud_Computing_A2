package controller;

import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import model.User;

public class RegisterController {

	public String checkUser(String email, String username, String password) throws Exception {

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new ProfileCredentialsProvider("default")).build();

		DynamoDB dynamoDB = new DynamoDB(client);

		Table table = dynamoDB.getTable("login");

		ScanSpec scanSpec = new ScanSpec().withProjectionExpression("email, user_name")
				.withFilterExpression("#em = :email or #un = :user_name")
				.withNameMap(new NameMap().with("#em", "email").with("#un", "user_name"))
				.withValueMap(new ValueMap().withString(":email", email).withString(":user_name", username));

		String message = null;

		try {
			System.out.println("Checking existing user details!");
			ItemCollection<ScanOutcome> items = table.scan(scanSpec);

			Iterator<Item> iter = items.iterator();
			while (iter.hasNext()) {
				Item item = iter.next();
				message = item.toString();
				System.out.println(message);
				if (email.equals(item.getString("email"))) {
					message = "Email already exists!";
					return message;
				} else if (username.equals(item.getString("user_name"))) {
					message = "Username already exists!";
					return message;
				}
			}

		} catch (Exception e) {
			System.err.println("Unable to query user details");
			System.err.println(e.getMessage());
		}

		if (message == null) {

			User user = new User();
			user.setEmail(email);
			user.setUser_name(username);
			user.setPassword(password);

			try {
				table.putItem(new Item().withPrimaryKey("email", email).withString("user_name", username)
						.withString("password", password));
				System.out.println("PutItem succeeded: " + email + " " + username);
			} catch (Exception e) {
				System.err.println("Unable to register user: " + email + " " + username);
				System.err.println(e.getMessage());
			}

		}
		return message;
	}
}
