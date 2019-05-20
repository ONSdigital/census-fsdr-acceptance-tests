package uk.gov.ons.census.fsdr.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnect {

    private ReadPropertyFile readPropertyFile = new ReadPropertyFile();
    private CurrentDateTime currentDateTime = new CurrentDateTime();

    public Integer queryRecordCount(){

        Connection conn = null;
        Statement totalRowCount = null;
        Integer fsdrRowCount = null;

        try {
            Class.forName(readPropertyFile.loadAndReadPropertyFile("driver"));
            System.out.println(currentDateTime.dateTime() + " Connecting to postgress " +  readPropertyFile.loadAndReadPropertyFile("envname") + " database...");

            // connecting to db
            conn = DriverManager.getConnection(readPropertyFile.loadAndReadPropertyFile("url"), readPropertyFile.loadAndReadPropertyFile("username"), "password");
            System.out.println(currentDateTime.dateTime() + " Creating statement...");
            totalRowCount = conn.createStatement();

            // getting total rowcount
            ResultSet rs = totalRowCount.executeQuery(readPropertyFile.loadAndReadPropertyFile("sql_for_new_data_pull_count"));
            rs.next();
            fsdrRowCount = rs.getInt(1);

            // closing the result set.
            rs.close();

            // closing db connection
            conn.close();

            } catch (SQLException se) {
                se.printStackTrace();
                } catch (Exception e) {
                e.printStackTrace();
            }
        return fsdrRowCount;
    }

    public String[][] queryAndRetrieveRecords (){
        Connection conn = null;
        Statement actualResult = null;
        String [][] FSDRData = new String [40000][50];
        // Getting record count.
        Integer fsdrRowCount = this.queryRecordCount();

        try {

            // DB connection details
            Class.forName(readPropertyFile.loadAndReadPropertyFile("driver"));
            System.out.println(currentDateTime.dateTime() + " Connecting to postgress " +  readPropertyFile.loadAndReadPropertyFile("envname") + " database...");

            // connecting to db
            conn = DriverManager.getConnection(readPropertyFile.loadAndReadPropertyFile("url"), readPropertyFile.loadAndReadPropertyFile("username"), "password");
            System.out.println(currentDateTime.dateTime() + " Creating statement...");
            actualResult = conn.createStatement();

            // executing the SQL
            ResultSet actualRS = actualResult.executeQuery(readPropertyFile.loadAndReadPropertyFile("sql_for_new_data_pull"));

            //getting the value of each record.
            for (Integer iteration = 0; iteration <fsdrRowCount; iteration++) {
                actualRS.next();
                FSDRData[iteration][0]= actualRS.getString("unique_employee_id");
                FSDRData[iteration][1]= actualRS.getString("first_name");
                FSDRData[iteration][2] = actualRS.getString("surname");
                FSDRData[iteration][3] = actualRS.getString("address_1");
                FSDRData[iteration][4] = actualRS.getString("address_2");
                FSDRData[iteration][5] = actualRS.getString("town");
                FSDRData[iteration][6] = actualRS.getString("county");
                FSDRData[iteration][7] = actualRS.getString("postcode");
                FSDRData[iteration][8]= actualRS.getString("personal_email_address");
                FSDRData[iteration][9] = actualRS.getString("telephone_number_contact_1");
                FSDRData[iteration][10] = actualRS.getString("telephone_number_contact_2");
                FSDRData[iteration][11] = actualRS.getString("emergency_contact_first_name");
                FSDRData[iteration][12] = actualRS.getString("emergency_contact_mobile_no");
                FSDRData[iteration][13] = actualRS.getString("dob");
                FSDRData[iteration][14] = actualRS.getString("driving_information");
                FSDRData[iteration][15] = actualRS.getString("age");
                FSDRData[iteration][16] = actualRS.getString("ethnicity");
                FSDRData[iteration][17] = actualRS.getString("ethnicity_notes");
                FSDRData[iteration][18] = actualRS.getString("disability");
                FSDRData[iteration][19] = actualRS.getString("disability_notes");
                FSDRData[iteration][20] = actualRS.getString("nationality");
                FSDRData[iteration][21] = actualRS.getString("gender");
                FSDRData[iteration][22] = actualRS.getString("sexual_orientation");
                FSDRData[iteration][23] = actualRS.getString("sexual_orientation_notes");
                FSDRData[iteration][24] = actualRS.getString("religion");
                FSDRData[iteration][25] = actualRS.getString("religion_notes");
           }

            // closing the result set.
            actualRS.close();

            // closing db connection
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FSDRData;
    }
}
