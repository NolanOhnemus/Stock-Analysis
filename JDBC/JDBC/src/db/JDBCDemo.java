/**
 * @author Jay Urbain
 *
 * Updated 10/3/2016, update 9/29/2018
 * JDBC example to demonstrate how to connect, 
 * create a statement, issue a query, and process a results
 * set.
 * 
 * Query is first issued with standard java.sql.Statement, then
 * with java.sql.PrepatedStatement
 */

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.mysql.jdbc.*;

// import javax.sql.DataSource;

public class JDBCDemo {
	
	// MySQL
	static String dbdriver = "com.mysql.jdbc.Driver";
	static String dburl = "jdbc:mysql://localhost";
	// static String dburl = "jdbc:mysql://ec2-107-20-10-136.compute-1.amazonaws.com";
	static String dbname = "video_db";//"Video";
	//	static String dbname = "i2b2riskindex2";

	// DataSource ds = null;

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		// scanner.useDelimiter("\n");

		//System.out.print("Enter login name: ");
		String login = "root";//scanner.nextLine();

		// Note: password will be echoed to console;
		System.out.print("Enter password: ");
		String password = scanner.nextLine();//"root";//
		//String password = PasswordField.readPassword("Enter password: ");

		System.out.println("Connecting as user '" + login + "' . . .");

		// Load the JDBC driver.
		// Library (.jar file) must be added to project build path.
		try {
			Class.forName(dbdriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Connect to the database
		// You can also connect with Properties for user and password as follows
		// java.util.Properties p = new Properties();
		// p.put("user", login);
		// p.put("password", password);
		// Connection connection = DriverManager.getConnection(dbdriver, p);

		Connection connection = null;
		try {

			connection = DriverManager.getConnection((dburl + "/" + dbname+"?useSSL=false"),
					login, password);
			connection.setClientInfo("autoReconnect", "true");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Note: could also pass multiple database parameters when creating
		// Connection using Properties
		// Properties props = new Properties();
		// props.setProperty("user", login);
		// props.setProperty("password",password);
		// props.setProperty("autoReconnect",true);
		// Connection connection = DriverManager.getConnection(url,props);

		String name = null;
		System.out.println("first regular statement");
		System.out.print("Enter actor name: ");
		name = scanner.nextLine();
		//while ((name = scanner.nextLine()).length() > 0) {

			// Set up query.
			// First we'll do standard query
			String query = "select va.name, vr.title as 'Movie Title' "
					+ "from Video_Actors va, Video_Recordings vr "
					+ "where va.recording_id=vr.recording_id "
					+ "and va.name like '%" + name + "%' "
					+ "group by va.name, vr.title";

			ResultSet results = null;
			Statement statement = null;
			try {
				statement = connection.createStatement();
				results = statement.executeQuery(query);
				ResultSetMetaData resultSetMetaData = (ResultSetMetaData) results
						.getMetaData();
				System.out.print("Actor Name");
				System.out.print("|");
				System.out.println("Movie Title");
				while (results.next()) {
					System.out.print(results.getString(1));
					System.out.print("|");
					System.out.println(results.getString("Movie Title"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				try {
					results.close();
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}

			System.out.println("next prepared statement");
			// Next will issue the same query using a prepatedStatement
			// Table and column names are quoted, even though it is not
			// necessary if all are in lower case.

		System.out.print("Enter actor name: ");
		name = scanner.nextLine();
			query = "select va.name as Actor, vr.title as 'Movie Title' "
					+ "from Video_Actors va, Video_Recordings vr "
					+ "where va.recording_id=vr.recording_id "
					+ "and va.name=? "
					+ "group by va.name, vr.title";

			// For each row in the result set, print some columns.
			// Note: query can use "AS" to rename columns as needed.
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, name);
				System.out.println(preparedStatement);
				results = preparedStatement.executeQuery();
				System.out.print("\n\n");
				System.out.print("Actor");
				System.out.print("|");
				System.out.println("Movie Title");
				while (results.next()) {
					System.out.print(results.getString("Actor"));
					System.out.print("|");
					System.out.println(results.getString("Movie Title"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			} finally {
				try {
					results.close();
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}

		//	System.out.print("\nEnter actor name: ");
		//}

		// Close the database connection.
		try {
			connection.close();
			scanner.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Done");

	}

	private float calculations(String[] stocks, float[] weights, Connection connection,
							   ResultSet results, Statement statement){
		float sum = 0;
		for(float weight: weights){
			sum += weight;
		}
		if(sum != 1){
			throw new IllegalArgumentException("Weights array does not equal one");
		}
		return -1;
	}

	private float cumPortRet(String[] stocks, float[] weights, Connection connection){
		String[] stock_cum_strings = new String[4];
		for(int i = 0; i < 4; i++){
			stock_cum_strings[i] = stocks[i] + "_cum_close";
		}
		String query = "Select (? * " + weights[0] + "? * " + weights[1] + "? * " + weights[2] + "? * " + weights[3]
				 + ")/4 from portfolio, ?, ?, ?, ? where ?.date=?.date and ?.date = ?.date " +
				"and ?.date = ?.date";
		ResultSet results = null;
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		try{
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, stock_cum_strings[0]);
			preparedStatement.setString(2, stock_cum_strings[1]);
			preparedStatement.setString(3, stock_cum_strings[2]);
			preparedStatement.setString(4, stock_cum_strings[3]);
			preparedStatement.setString(5, stocks[0]);
			preparedStatement.setString(6, stocks[1]);
			preparedStatement.setString(7, stocks[2]);
			preparedStatement.setString(8, stocks[3]);
			preparedStatement.setString(9, stocks[0]);
			preparedStatement.setString(10, stocks[1]);
			preparedStatement.setString(11, stocks[2]);
			preparedStatement.setString(12, stocks[3]);

			results = preparedStatement.executeQuery();
			System.out.print("\n\n");

			if (results.next()) {
				System.out.print("Code: " + results.getString(1));
			}
			else {
				System.out.println("invalid stock information");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				results.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return -1;
	}

	private float stockValue(String stock, float weight){
		return -1;
	}

}
