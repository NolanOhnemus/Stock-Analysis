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

import java.sql.*;
import java.util.Scanner;


// import javax.sql.DataSource;

public class SQLInjection {
	
	// MySQL
	static String dbdriver = "com.mysql.jdbc.Driver";
	static String dburl = "jdbc:mysql://localhost";

	static String dbname = "video_db";//"data_analytics_2017_2019_021";


	// DataSource ds = null;

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		// scanner.useDelimiter("\n");

		String login = "root";//scanner.nextLine();
		String password = "password";//scanner.nextLine();
		// String password = PasswordField.readPassword("Enter password: ");

		System.out.println("Connecting as user '" + login + "' . . .");

		// Load the JDBC driver.
		// Library (.jar file) must be added to project build path.
		try {
			Class.forName(dbdriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection((dburl + "/" + dbname),
					login, password);
			connection.setClientInfo("autoReconnect", "true");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}


		String name = null;
		String pass = null;
		System.out.println("first regular statement");
		System.out.print("Enter username: ");
		name = scanner.nextLine();
		System.out.print("Enter password: ");
		pass = scanner.nextLine();
		//while ((name = scanner.nextLine()).length() > 0) {

			// Set up query.
			// First we'll do standard query
			String query = "select third "
					+ "from users "
					+ "where username='" + name + "'"
					+ "and password='" + pass + "'";

			ResultSet results = null;
			Statement statement = null;
			try {
				statement = connection.createStatement();
				results = statement.executeQuery(query);
				//ResultSetMetaData resultSetMetaData = (ResultSetMetaData) results
				//		.getMetaData();
				/*System.out.print("id");
				System.out.print("|");
				System.out.println("Actor name");
				System.out.print("|");
				System.out.println("Rec ID");
				*/
				if (results.next()) {
					System.out.print("Code: " + results.getString(1));

					//System.out.print("|");
					//System.out.println(results.getString(3));
				}
				else {
					System.out.println("invalid username/password");
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

			System.out.println("\n\nnext prepared statement");
			// Next will issue the same query using a prepatedStatement
			// Table and column names are quoted, even though it is not
			// necessary if all are in lower case.

		System.out.print("try again: ");

			query  = "select third "
				+ "from users "
				+ "where username=?"
				+ "and password=?";

			// For each row in the result set, print some columns.
			// Note: query can use "AS" to rename columns as needed.
			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(query);
				preparedStatement.setString(1, name);
				preparedStatement.setString(2, pass);
				System.out.println(preparedStatement);
				results = preparedStatement.executeQuery();
				System.out.print("\n\n");

				if (results.next()) {
					System.out.print("Code: " + results.getString(1));

					//System.out.print("|");
					//System.out.println(results.getString(3));
				}
				else {
					System.out.println("invalid username/password");
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


		System.out.println("\nDone");

	}
}
