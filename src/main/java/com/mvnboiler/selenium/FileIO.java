package com.mvnboiler.selenium;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import java.util.Random;

import java.nio.file.Files;

public class FileIO {
    private static final FileIO instance = new FileIO();
    Constants constant;

    public FileIO() {
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

	public File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(directory + File.separator + filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("function a() { alert('pankaj') }");
            writer.close();
        } catch( IOException ex) {
        }
        
        return file;
    }

	public File fileWithDirectoryAssurance(String directory, String filename, String fileContent) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(directory + File.separator + filename);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(fileContent);
            writer.close();
        } catch( IOException ex) {
          System.out.println(ex.getMessage());
        }
        
        return file;
    }

    public boolean deleteFile(String directory, String filename) {
        boolean deleted = false;
        try {
            File file = new File(directory + File.separator + filename);
            deleted = Files.deleteIfExists(file.toPath());
        } catch(IOException e ) {

        }
        return deleted;
    }
}
