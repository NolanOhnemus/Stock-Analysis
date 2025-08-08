package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PortfolioTracker {

    // MySQL
    static String dbdriver = "com.mysql.jdbc.Driver";
    static String dburl = "jdbc:mysql://localhost";
    // static String dburl = "jdbc:mysql://ec2-107-20-10-136.compute-1.amazonaws.com";
    static String dbname = "data_analytics_2017";//"Video";
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

        Connection connection = null;
        try {

            connection = DriverManager.getConnection((dburl + "/" + dbname+"?useSSL=false"),
                    login, password);
            connection.setClientInfo("autoReconnect", "true");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println("Enter 1 to find max sharpe ratio, enter 2 to use the given weights");
        String option = scanner.nextLine();
        int input = Integer.parseInt(option);
        String[] stocks = new String[]{"goog", "celg", "nvda", "fb", "spy"};
        if(input == 1){
            float[][] maxResults = findMaxSharpe(stocks, connection);
            float[] results = maxResults[1];
            float[] weightResults = maxResults[0];
            System.out.println("Weights for maximum were: "  + weightResults[0] +  ", " + weightResults[1] +  ", "
                    + weightResults[2] +  ", " + weightResults[3]);
            System.out.println("Standard Deviation is: " + results[0]);
            System.out.println("Average of Daily returns is: " + results[1]);
            System.out.println("Sharpe Ratio is: " + results[2]);
            System.out.println("Cumulative return is : " + results[3]);
            updatePortValue(stocks, connection);
            updateCumPortRet(connection);
        }
        else if(input == 2){
            float[] weights = new float[]{0.5f, 0.0f, 0.5f, 0.0f, 1.0f};
            String[] dates = new String[]{"2016-10-06", "2017-10-03"};
            float[] results = simulatePortfolio(stocks, weights, dates, connection);
            System.out.println("Standard Deviation is: " + results[0]);
            System.out.println("Average of Daily returns is: " + results[1]);
            System.out.println("Sharpe Ratio is: " + results[2]);
            System.out.println("Cumulative return is : " + results[3]);
            updatePortValue(stocks, connection);
            updateCumPortRet(connection);
        }
        else throw new InputMismatchException("Input not accepted");
    }

    private static float[][] findMaxSharpe(String[] stocks, Connection connection){
        int numIterations = 4000;
        float[] maxWeights = new float[]{0.3f, 0.3f, 0.2f, 0.2f, 1.0f};
        String[] dates = new String[]{"2016-10-06", "2017-10-03"};
        float[] maxResults =  simulatePortfolio(stocks, maxWeights, dates, connection);
        float maxSharpeRatio = maxResults[2];
        float[] tmpWeights = new float[5];
        while(numIterations > 0){
            float weightAllocation = 1.0f;
            for(int i = 0; i < 3; i++){
                float random = round(random(0, weightAllocation),1);
                tmpWeights[i] = random;
                weightAllocation -= random;
            }
            tmpWeights[3] = round(weightAllocation,1);
            tmpWeights[4] = 1.0f;

            float[] newResults = simulatePortfolio(stocks, tmpWeights, dates, connection);
            float newSharpe = newResults[2];
            if(newSharpe > maxSharpeRatio) {
                maxSharpeRatio = newSharpe;
                maxResults = newResults;
                maxWeights = tmpWeights;
            }
            System.out.println("Finished Iteration " + (4001 - numIterations));
            numIterations--;
        }
        simulatePortfolio(stocks, maxWeights, dates, connection);
        return new float[][]{maxWeights, maxResults};
    }

    private static float random(float min, float max){
        return (float)(min + Math.random() * (max - min));
    }

    private static float[] simulatePortfolio(String[] stocks, float[] weights, String[] dates, Connection connection){
        float sum = 0;
        for(float weight: weights){
            sum += weight;
        }
        if(sum != 2.0f){
            throw new IllegalArgumentException("Weights array does not equal one");
        }

        for(int i = 0; i < 5; i++){
            updateStockValue(stocks[i], weights[i], connection);
        }

        float[] returnArray = new float[4];
        returnArray[2] = sharpeRatio(stocks, weights, connection, dates);
        returnArray[0] = getSTDDEV(stocks, connection, dates);
        returnArray[1] = getAVG(stocks, connection, dates);
        returnArray[3] = cumPortRet(stocks, connection, dates);
        return returnArray;
    }

    private static float cumPortRet(String[] stocks, Connection connection, String[] dates){
        float firstDaySum = 0;
        float lastDaySum = 0;
        for(int i = 0; i < 4; i++){
            firstDaySum += getValueAtDate(stocks[i], connection, dates[0]);
            lastDaySum += getValueAtDate(stocks[i], connection, dates[1]);
        }
        return (lastDaySum - firstDaySum) / firstDaySum;
    }

    private static void updatePortValue(String[] stocks, Connection connection){
        String query = "Update portfolio, (Select date, (" + stocks[0]+"_value + " + stocks[1]+"_value + "
                + stocks[2]+"_value + "
                + stocks[3]+"_value) as result from portfolio) as subquery set " +
                "port_value=subquery.result where portfolio.date=subquery.date;";
        int results = -1;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeUpdate();
            if(results == 0){
                System.out.println("Did not update values in the table");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static void updateCumPortRet(Connection connection) {
        String query = "Update portfolio, (Select date, ((port_value - first_value(port_value) " +
                "over(order by portfolio.date)) / first_value(port_value) over(order by portfolio.date)) " +
                "as result from portfolio) as subquery set " +
                "port_cum_return=subquery.result where portfolio.date=subquery.date;";
        int results = -1;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeUpdate();
            if(results == 0){
                System.out.println("Did not update values in the table");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static float getValueAtDate(String stock, Connection connection, String date){
        float returnable = -1;
        String query = "Select " + stock + "_value from portfolio where date='" + date + "';";

        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeQuery();
            if(results.next()){
                returnable = results.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return returnable;
    }

    private static void updateStockValue(String stock, float weight, Connection connection){
        String query = "Update portfolio, ( Select date, (" + weight + " * 1 * " + stock + "_cum_close) as result " +
                "from portfolio) as subquery set " + stock + "_value=subquery.result where portfolio.date=subquery.date;";
        int results = -1;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeUpdate();
            if(results == 0){
                System.out.println("Did not update values in the table");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private static float sharpeRatio(String[] stocks, float[] weights, Connection connection, String[] dates){
        int numDates = getNumDates(dates, connection);
        float average = getAVG(stocks, connection, dates);
        float stddev = getSTDDEV(stocks, connection, dates);
        double sqrtN = Math.sqrt(numDates);
        return (float)(sqrtN * average) / stddev;
    }

    private static int getNumDates(String[] dates, Connection connection){
        int returnable = -1;
        String query = "Select count(date) from portfolio where date between '" + dates[0] + "' and '" + dates[1] + "';";
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeQuery();
            if(results.next()){
                returnable = results.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return returnable;
    }

    private static float getAVG(String[] stocks, Connection connection, String[] dates){
        float returnable = -1;
        String query = "Select AVG((" + stocks[0]+"_value + " + stocks[1]+"_value + " + stocks[2]+"_value + "
                                      + stocks[3]+"_value) - " + stocks[4]+"_value) from portfolio where date between '"
                                      + dates[0] + "' and '" + dates[1] + "';";
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeQuery();
            if(results.next()){
                returnable = results.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return returnable;
    }

    private static float getSTDDEV(String[] stocks, Connection connection, String[] dates){
        float returnable = -1;
        String query = "Select (STDDEV((" + stocks[0]+"_value + " + stocks[1]+"_value +" + stocks[2]+"_value + "
                + stocks[3]+"_value) - " + stocks[4] + "_value)) from portfolio where date between '"
                + dates[0] + "' and '" + dates[1] + "';";
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            results = preparedStatement.executeQuery();
            if(results.next()){
                returnable = results.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return returnable;
    }

    private static float round (float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float)Math.round(value * scale) / scale;
    }

}
