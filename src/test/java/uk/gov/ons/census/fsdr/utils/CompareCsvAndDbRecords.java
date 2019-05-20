package uk.gov.ons.census.fsdr.utils;

import org.junit.Assert;

public class CompareCsvAndDbRecords {
    private CSVReader csvReader = new CSVReader();
    private DbConnect dbConnect = new DbConnect();
    private CurrentDateTime currentDateTime = new CurrentDateTime();

    public void checkRecords(String [][] FSDRData, String [][] AdeccoData, String csvPath){
        Integer adeccoRowCount = csvReader.countCsvRows(csvPath);
        Integer fsdrRowCount = dbConnect.queryRecordCount();
        Assert.assertEquals("The total record in Adecco and FSDR does not match", adeccoRowCount, fsdrRowCount);
        System.out.println(currentDateTime.dateTime() + " The total record count in adecco & fsdr are correct and is " + adeccoRowCount);
        for (int i=0; i < fsdrRowCount; i++) {
            for(int j=0; j < 25; j++) {
                if (AdeccoData[i+1][j].equals("")) {
                    AdeccoData[i+1][j] = null;
                }
                Assert.assertEquals("The value '" + AdeccoData[0][j] + "' in Adecco and FSDR does not match for the record where employee id is "+ AdeccoData[i+1][0], AdeccoData[i+1][j], FSDRData[i][j]);
            }
        }
    }
}
