package uk.gov.ons.census.fsdr.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class CSVReader {

    String csvFile = getClass().getClassLoader().getResource("files/validAdeccoData.csv").getPath();
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";

    public Integer countCsvRows(){

        Integer expectedCount = -1;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] fields = line.split(cvsSplitBy);
                expectedCount = expectedCount + 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return expectedCount;
    }

    public String[][] readCSV() throws IOException {

        String [][] AdeccoData = new String [40000][50];
        Integer expectedCount = -1;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] fields = line.split(cvsSplitBy);
                AdeccoData[expectedCount+1][0] = fields[0].replace('"',' ').trim();
                AdeccoData[expectedCount+1][1] = fields[1].replace('"',' ').trim();
                AdeccoData[expectedCount+1][2] = fields[2].replace('"',' ').trim();
                AdeccoData[expectedCount+1][3] = fields[3].replace('"',' ').trim();
                AdeccoData[expectedCount+1][4] = fields[4].replace('"',' ').trim();
                AdeccoData[expectedCount+1][5] = fields[5].replace('"',' ').trim();
                AdeccoData[expectedCount+1][6] = fields[6].replace('"',' ').trim();
                AdeccoData[expectedCount+1][7] = fields[7].replace('"',' ').trim();
                AdeccoData[expectedCount+1][8] = fields[8].replace('"',' ').trim();
                AdeccoData[expectedCount+1][9] = fields[9].replace('"',' ').trim();
                AdeccoData[expectedCount+1][10] = fields[10].replace('"',' ').trim();
                AdeccoData[expectedCount+1][11] = fields[11].replace('"',' ').trim();
                AdeccoData[expectedCount+1][12] = fields[12].replace('"',' ').trim();
                AdeccoData[expectedCount+1][13] = fields[13].replace('"',' ').trim();
                AdeccoData[expectedCount+1][14] = fields[14].replace('"',' ').trim();
                AdeccoData[expectedCount+1][15] = fields[15].replace('"',' ').trim();
                AdeccoData[expectedCount+1][16] = fields[16].replace('"',' ').trim();
                AdeccoData[expectedCount+1][17] = fields[17].replace('"',' ').trim();
                AdeccoData[expectedCount+1][18] = fields[18].replace('"',' ').trim();
                AdeccoData[expectedCount+1][19] = fields[19].replace('"',' ').trim();
                AdeccoData[expectedCount+1][20] = fields[20].replace('"',' ').trim();
                AdeccoData[expectedCount+1][21] = fields[21].replace('"',' ').trim();
                AdeccoData[expectedCount+1][22] = fields[22].replace('"',' ').trim();
                AdeccoData[expectedCount+1][23] = fields[23].replace('"',' ').trim();
                AdeccoData[expectedCount+1][24] = fields[24].replace('"',' ').trim();
                expectedCount = expectedCount + 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return AdeccoData;
    }
}