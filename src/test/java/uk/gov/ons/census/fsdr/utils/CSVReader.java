package uk.gov.ons.census.fsdr.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class CSVReader {

    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";

    public Integer countCsvRows(String csvPath){
        String csvFile = getClass().getClassLoader().getResource(csvPath).getPath();
        Integer csvRowCount = -1;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] fields = line.split(cvsSplitBy);
                csvRowCount = csvRowCount + 1;
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
        return csvRowCount;
    }

    public String[][] readAdeccoData(String csvPath) throws IOException {
        String csvFile = getClass().getClassLoader().getResource(csvPath).getPath();
        String [][] csvData = new String [40000][50];
        Integer csvRowCount = -1;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] fields = line.split(cvsSplitBy);
                csvData[csvRowCount+1][0] = fields[0].replace('"',' ').trim();
                csvData[csvRowCount+1][1] = fields[1].replace('"',' ').trim();
                csvData[csvRowCount+1][2] = fields[2].replace('"',' ').trim();
                csvData[csvRowCount+1][3] = fields[3].replace('"',' ').trim();
                csvData[csvRowCount+1][4] = fields[4].replace('"',' ').trim();
                csvData[csvRowCount+1][5] = fields[5].replace('"',' ').trim();
                csvData[csvRowCount+1][6] = fields[6].replace('"',' ').trim();
                csvData[csvRowCount+1][7] = fields[7].replace('"',' ').trim();
                csvData[csvRowCount+1][8] = fields[8].replace('"',' ').trim();
                csvData[csvRowCount+1][9] = fields[9].replace('"',' ').trim();
                csvData[csvRowCount+1][10] = fields[10].replace('"',' ').trim();
                csvData[csvRowCount+1][11] = fields[11].replace('"',' ').trim();
                csvData[csvRowCount+1][12] = fields[12].replace('"',' ').trim();
                csvData[csvRowCount+1][13] = fields[13].replace('"',' ').trim();
                csvData[csvRowCount+1][14] = fields[14].replace('"',' ').trim();
                csvData[csvRowCount+1][15] = fields[15].replace('"',' ').trim();
                csvData[csvRowCount+1][16] = fields[16].replace('"',' ').trim();
                csvData[csvRowCount+1][17] = fields[17].replace('"',' ').trim();
                csvData[csvRowCount+1][18] = fields[18].replace('"',' ').trim();
                csvData[csvRowCount+1][19] = fields[19].replace('"',' ').trim();
                csvData[csvRowCount+1][20] = fields[20].replace('"',' ').trim();
                csvData[csvRowCount+1][21] = fields[21].replace('"',' ').trim();
                csvData[csvRowCount+1][22] = fields[22].replace('"',' ').trim();
                csvData[csvRowCount+1][23] = fields[23].replace('"',' ').trim();
                csvData[csvRowCount+1][24] = fields[24].replace('"',' ').trim();
                csvData[csvRowCount+1][25] = fields[25].replace('"',' ').trim();
                csvData[csvRowCount+1][26] = fields[26].replace('"',' ').trim();
                csvRowCount = csvRowCount + 1;
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
        return csvData;
    }

    public String[][] readAirWatchData(String csvPath) throws IOException {
        String csvFile = getClass().getClassLoader().getResource(csvPath).getPath();
        String [][] csvData = new String [40000][15];
        Integer csvRowCount = -1;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] fields = line.split(cvsSplitBy);
                csvData[csvRowCount+1][0] = fields[0].replace('"',' ').trim();
                csvData[csvRowCount+1][1] = fields[1].replace('"',' ').trim();
                csvData[csvRowCount+1][2] = fields[2].replace('"',' ').trim();
                csvData[csvRowCount+1][3] = fields[5].replace('"',' ').trim();
                csvData[csvRowCount+1][4] = fields[7].replace('"',' ').trim();
                csvData[csvRowCount+1][5] = fields[8].replace('"',' ').trim();
                csvRowCount = csvRowCount + 1;
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
        return csvData;
    }
}