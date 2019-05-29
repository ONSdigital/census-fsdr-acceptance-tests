package uk.gov.ons.census.fsdr.utils;

import org.junit.Assert;

public class CompareCsvAndDbRecords {
    private CSVReader csvReader = new CSVReader();
    private DbConnect dbConnect = new DbConnect();
    private CurrentDateTime currentDateTime = new CurrentDateTime();
    private ReadPropertyFile readPropertyFile = new ReadPropertyFile();

    public void checkRecords(String [][] fsdrData, String [][] csvData, String csvPath, Integer fsdrRowCount){

        String sqlCount = null;

        // Getting CSV record count
        Integer csvRowCount = csvReader.countCsvRows(csvPath);

        // Getting FSDR record count.
        /*switch(dataMapping) {
            case "Adecco":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_adecco_record_count");
                break;
            case "AirWatch":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_airwatch_record_count");
                break;
            case "Logistics":
                sqlCount = readPropertyFile.loadAndReadPropertyFile("sql_for_new_logistics_record_count");
                break;
        }
        Integer fsdrRowCount = dbConnect.queryRecordCount(sqlCount);*/
        Assert.assertEquals("The total record in CSV file (" + csvPath + ") and FSDR does not match", csvRowCount, fsdrRowCount);
        System.out.println(currentDateTime.dateTime() + " The total record count in CSV file (" + csvPath + ") & FSDR are correct and is " + csvRowCount);
        for (int i=0; i < fsdrRowCount; i++) {
            for(int j=0; j < 25; j++) {
                if (csvData[i+1][j].equals("")) {
                    csvData[i+1][j] = null;
                }
                Assert.assertEquals("The value '" + csvData[0][j] + "' in CSV file  (" + csvPath + ") & FSDR does not match for the record where employee id is "+ csvData[i+1][0], csvData[i+1][j], fsdrData[i][j]);
            }
        }
    }
}