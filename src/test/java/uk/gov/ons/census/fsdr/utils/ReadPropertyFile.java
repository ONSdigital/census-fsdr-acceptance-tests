package uk.gov.ons.census.fsdr.utils;

import java.io.*;
import java.util.Properties;

public class ReadPropertyFile {

    public String loadAndReadPropertyFile(String propParameter ){
        Properties prop = new Properties();
        InputStream input = null;
        String propVal = null;

        try {
            // load a properties file
            input = new FileInputStream(getClass().getClassLoader().getResource("files/config.properties").getPath());
            prop.load(input);

            // get the property value and print it out
            propVal = prop.getProperty(propParameter);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(propVal);
        return propVal;
    }
}