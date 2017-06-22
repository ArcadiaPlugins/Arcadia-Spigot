package me.redraskal.arcadia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {

    public static void deleteDirectory(File directory) {
        if(directory.exists()) {
            File[] files;
            int j = (files = directory.listFiles()).length;
            for (int i = 0; i < j; i++) {
                File temp = files[i];
                if(temp.isDirectory()) {
                    deleteDirectory(temp);
                } else {
                    temp.delete();
                }
            }
            directory.delete();
        }
    }

    public static void copyDirectory(File from, File to) {
        try {
            if(!from.isDirectory()) {
                return;
            }
            if(!to.exists()) {
                to.mkdirs();
            }
            for (File temp : from.listFiles()) {
                if(temp.isDirectory()) {
                    copyDirectory(temp, new File(to.getCanonicalPath() + "/" + temp.getName()));
                } else {
                    copyFile(temp, new File(to.getCanonicalPath() + "/" + temp.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File from, File to) {
        FileInputStream localStream = null;
        try {
            localStream = new FileInputStream(from);
            FileOutputStream localOutputStream = null;
            if(!to.exists()) {
                to.createNewFile();
            }
            localOutputStream = new FileOutputStream(to);
            byte[] buffer = new byte[524288];
            int i;
            while ((i = localStream.read(buffer)) != -1) {
                localOutputStream.write(buffer, 0, i);
            }
            localOutputStream.close();
            localStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}