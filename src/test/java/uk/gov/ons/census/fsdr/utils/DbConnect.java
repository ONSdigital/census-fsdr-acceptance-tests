package uk.gov.ons.census.fsdr.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnect {

    private ReadPropertyFile readPropertyFile = new ReadPropertyFile();
    private CurrentDateTime currentDateTime = new CurrentDateTime();

    public Integer queryRecordCount(String sqlCount){

        Connection conn = null;
        Statement totalRowCount = null;
        Integer fsdrRowCount = null;

        try {
            Class.forName(readPropertyFile.loadAndReadPropertyFile("driver"));
            System.out.println(currentDateTime.dateTime() + " Connecting to postgress for getting rowcount " +  readPropertyFile.loadAndReadPropertyFile("envname") + " database...");

            // connecting to db
            conn = DriverManager.getConnection(readPropertyFile.loadAndReadPropertyFile("url"), readPropertyFile.loadAndReadPropertyFile("username"), "password");
            System.out.println(currentDateTime.dateTime() + " Creating statement...");
            totalRowCount = conn.createStatement();

            // getting total rowcount
            ResultSet rs = totalRowCount.executeQuery(sqlCount);
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

    public String[][] queryAndRetrieveRecords (String dataMapping){
        Connection conn = null;
        Statement actualResult = null;
        String [][] FSDRData = new String [40000][50];
        Integer RowCount = 0;
        String sqlData = null;
        ResultSet actualRS = null;
        String sqlCount;

        try {
            // DB connection details
            Class.forName(readPropertyFile.loadAndReadPropertyFile("driver"));
            System.out.println(currentDateTime.dateTime() + " Connecting to postgress for getting records " +  readPropertyFile.loadAndReadPropertyFile("envname") + " database...");

            // connecting to db
            conn = DriverManager.getConnection(readPropertyFile.loadAndReadPropertyFile("url"), readPropertyFile.loadAndReadPropertyFile("username"), "password");
            System.out.println(currentDateTime.dateTime() + " Creating statement...");
            actualResult = conn.createStatement();

        // Getting record count.
        switch(dataMapping) {
            case "Adecco":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_adecco_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_adecco_data");
                RowCount = this.queryRecordCount(sqlCount);
                actualRS = actualResult.executeQuery(sqlData);
                break;
            case "AirWatch":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_airwatch_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_airwatch_data");
                RowCount = this.queryRecordCount(sqlCount);
                actualRS = actualResult.executeQuery(sqlData);
                break;
            case "Logisctics":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_logistics_record_count");
                sqlData = readPropertyFile.loadAndReadPropertyFile("sql_for_new_logistics_data");
                RowCount = this.queryRecordCount(sqlCount);
                actualRS = actualResult.executeQuery(sqlData);
                break;
        }
            //getting the value of each record.
            for (Integer iteration = 0; iteration <RowCount; iteration++) {
                actualRS.next();
                switch(dataMapping) {
                    case "Adecco":
                        FSDRData[iteration][0] = actualRS.getString("unique_employee_id");
                        FSDRData[iteration][1] = actualRS.getString("first_name");
                        FSDRData[iteration][2] = actualRS.getString("surname");
                        FSDRData[iteration][3] = actualRS.getString("preferred_name");
                        FSDRData[iteration][4] = actualRS.getString("address_1");
                        FSDRData[iteration][5] = actualRS.getString("address_2");
                        FSDRData[iteration][6] = actualRS.getString("town");
                        FSDRData[iteration][7] = actualRS.getString("county");
                        FSDRData[iteration][8] = actualRS.getString("postcode");
                        FSDRData[iteration][9] = actualRS.getString("personal_email_address");
                        FSDRData[iteration][10] = actualRS.getString("telephone_number_contact_1");
                        FSDRData[iteration][11] = actualRS.getString("telephone_number_contact_2");
                        FSDRData[iteration][12] = actualRS.getString("emergency_contact_first_name");
                        FSDRData[iteration][13] = actualRS.getString("emergency_contact_mobile_no");
                        FSDRData[iteration][14] = actualRS.getString("dob");
                        FSDRData[iteration][15] = actualRS.getString("driving_information");
                        FSDRData[iteration][16] = actualRS.getString("age");
                        FSDRData[iteration][17] = actualRS.getString("ethnicity");
                        FSDRData[iteration][18] = actualRS.getString("ethnicity_notes");
                        FSDRData[iteration][19] = actualRS.getString("disability");
                        FSDRData[iteration][20] = actualRS.getString("disability_notes");
                        FSDRData[iteration][21] = actualRS.getString("nationality");
                        FSDRData[iteration][22] = actualRS.getString("gender");
                        FSDRData[iteration][23] = actualRS.getString("sexual_orientation");
                        FSDRData[iteration][24] = actualRS.getString("sexual_orientation_notes");
                        FSDRData[iteration][25] = actualRS.getString("religion");
                        FSDRData[iteration][26] = actualRS.getString("religion_notes");
                        break;

                    case "AirWatch":
                        FSDRData[iteration][0] = actualRS.getString("ons_email_address");
                        FSDRData[iteration][1] = "Active";   //actualRS.getString("status");
                        FSDRData[iteration][2] = "2";   //actualRS.getString("type");
                        FSDRData[iteration][3] = actualRS.getString("first_name");
                        FSDRData[iteration][4] = actualRS.getString("surname");
                        FSDRData[iteration][5] = actualRS.getString("ons_email_address");
                        break;

                    case "Logistics":
                        FSDRData[iteration][0] = actualRS.getString("first_name");
                        FSDRData[iteration][1] = actualRS.getString("surname");
                        FSDRData[iteration][2] = actualRS.getString("preferred_name");
                        FSDRData[iteration][3] = actualRS.getString("address_1");
                        FSDRData[iteration][4] = actualRS.getString("address_2");
                        FSDRData[iteration][5] = actualRS.getString("town");
                        FSDRData[iteration][6] = actualRS.getString("county");
                        FSDRData[iteration][7] = actualRS.getString("postcode");
                        FSDRData[iteration][8] = actualRS.getString("personal_email_address");
                        FSDRData[iteration][9] = actualRS.getString("ons_email_address");
                        FSDRData[iteration][10] = actualRS.getString("telephone_number_contact_1");
                        FSDRData[iteration][11] = actualRS.getString("field_device_phone_number");
                        FSDRData[iteration][12] = actualRS.getString("job_role");
                        FSDRData[iteration][13] = actualRS.getString("unique_role_id");
                        FSDRData[iteration][14] = actualRS.getString("id_badge_no");
                        FSDRData[iteration][15] = actualRS.getString("status");
                        break;
                }
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
