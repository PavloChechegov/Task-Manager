package com.pavlochechegov.taskmanager;

import android.content.Context;
import android.util.Log;
import com.pavlochechegov.taskmanager.model.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;


public class TaskJSON{
    private Context mContext;
    private String mFileName;
    private ArrayList<Task> mTaskArrayList;
    private BufferedReader reader;

    public TaskJSON(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public void saveTask(ArrayList<Task> tasks) throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray();

        for (Task task : tasks) {
            jsonArray.put(task.toJSON());
        }

        Writer writer = null;
        FileOutputStream outputStream;

        try {
            outputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(outputStream);
            writer.write(jsonArray.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(writer != null) writer.close();
        }
    }


    public ArrayList<Task> loadTask() throws  IOException, JSONException {
        mTaskArrayList = new ArrayList<>();
        JSONObject jsonObject;

        try {
            InputStream inputStream = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer jsonString = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null){
                jsonString.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < jsonArray.length(); i++) {

                jsonObject = jsonArray.getJSONObject(i);
                Task task = new Task(jsonObject);
                mTaskArrayList.add(task);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }

        return mTaskArrayList;
    }
}
