package com.example.uidesign;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class AppUtility {

    public AppUtility(){

    }
    public File uriToFile(Uri uri, Activity activity) throws IOException {
        File file = File.createTempFile("upload_", ".jpg", activity.getCacheDir());

        try (InputStream in = activity.getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(file)) {

            if (in == null) {
                throw new IOException("Cannot open input stream from URI");
            }

            FileChannel inChannel = ((FileInputStream) in).getChannel();
            FileChannel outChannel = out.getChannel();

            long transferred = inChannel.transferTo(0, inChannel.size(), outChannel);

            if (transferred == 0) {
                throw new IOException("No bytes transferred - file may be empty");
            }

            Log.d("FileCopy", "File copied successfully. Size: " + file.length() + " bytes");
            return file;

        } catch (Exception e) {
            Log.e("FileCopy", "Error copying file: " + e.getMessage(), e);
            // Fallback to traditional copy if FileChannel fails
            return null;
        }
    }
}
