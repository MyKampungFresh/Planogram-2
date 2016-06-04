package course.examples.helloandroid;

//TODO: This class will write a temporary file which have this structure:
/*
 * [database file name 0]:[progress]
 * [database file name 1]:[progress]
 * [etc for each planogram which has been opened once]
 */

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CacheFileHandler {

    private final static String FILENAME = "pgm_progress";
    private final static boolean IS_TEXT_APPENDED = true;
    private final static String SEPARATOR = ":";
    private final static String NEWLINE = System.getProperty("line.separator");

    private FileWriter mCacheWriter;
    private FileReader mCacheReader;

    public CacheFileHandler(Context context) {

        try {
            File mCacheFile = new File(context.getCacheDir() + "/" + FILENAME);

            if (!mCacheFile.exists()) {
                mCacheFile.createNewFile();
            }

            mCacheWriter = new FileWriter(mCacheFile.getAbsoluteFile(), IS_TEXT_APPENDED);
            mCacheReader = new FileReader(mCacheFile);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(String dbName, int currentPos) {

        try {
            mCacheWriter.write(dbName + SEPARATOR + currentPos + NEWLINE);
            mCacheWriter.flush();
            mCacheWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void read() {

        String line;
        String[] planoNameAndProgress;

        try {
            BufferedReader bufferedReader = new BufferedReader(mCacheReader);

            while ((line = bufferedReader.readLine()) != null)
            {
                planoNameAndProgress = line.split(":");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
