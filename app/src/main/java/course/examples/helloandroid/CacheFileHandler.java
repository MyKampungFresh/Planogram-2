package course.examples.helloandroid;

//TODO: This class is stupid

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheFileHandler {

    private final static String FILENAME = "pgm_progress";
    private final static String FILE_EXTENSION = ".cache";
    private final static boolean IS_TEXT_APPENDED = true;
    private final static String SEPARATOR = ":";
    private final static String NEWLINE = System.getProperty("line.separator");

    private File mCacheFile;

    List<String> mPlanoNames;
    List<String> mPlanoLastPos;

    public CacheFileHandler(Context context) {

        try {
            mCacheFile = new File(context.getCacheDir() + "/" + FILENAME + FILE_EXTENSION);

            if (!mCacheFile.exists()) {
                mCacheFile.createNewFile();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(String dbName, int lastPos) {

        try {
            FileWriter cacheWriter = new FileWriter(mCacheFile.getAbsoluteFile(), true);

            cacheWriter.write(dbName + SEPARATOR + lastPos + NEWLINE);
            cacheWriter.flush();
            cacheWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void read() {

        String line;
        String[] planoNameAndProgress;

        mPlanoNames = new ArrayList<>();
        mPlanoLastPos = new ArrayList<>();

        try {

            FileReader cacheReader = new FileReader(mCacheFile);

            BufferedReader bufferedReader = new BufferedReader(cacheReader);

            while ((line = bufferedReader.readLine()) != null)
            {
                planoNameAndProgress = line.split(":");

                mPlanoNames.add(planoNameAndProgress[0]);
                mPlanoLastPos.add(planoNameAndProgress[1]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkIfPlanoExists(String planoName) {

        return mPlanoNames.contains(planoName);
    }

    //TODO....
    public void setPlanoLastPos(String planoName, int lastPos) {

        try {
            FileWriter cacheWriter = new FileWriter(mCacheFile.getAbsoluteFile(), false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        int planoIndexInList = mPlanoNames.indexOf(planoName);
        if(planoIndexInList != -1) {
            mPlanoLastPos.set(planoIndexInList,String.valueOf(lastPos));
        }

    }

    public int getPlanoLastPos(String planoName) {

        int lastPos = 0;
        int planoIndexInList = mPlanoNames.indexOf(planoName);

        if(planoIndexInList != -1) {
            lastPos = Integer.parseInt(mPlanoLastPos.get(planoIndexInList));
        }

        return lastPos;
    }
}
