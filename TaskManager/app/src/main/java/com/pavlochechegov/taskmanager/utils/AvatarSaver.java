package com.pavlochechegov.taskmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class AvatarSaver {

    private File mDirectoryName;
    private String mFileName;
    private Context mContext;

    public AvatarSaver(Context context) {
        mContext = context;
    }

    public AvatarSaver setFileName(String fileName) {
        mFileName = fileName;
        return this;
    }

    public AvatarSaver setDirectoryName(File directoryName) {
        mDirectoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private String createFile() {
        File directory = mDirectoryName;
        return directory + mFileName;
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}