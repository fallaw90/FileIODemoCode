package com.fallntic.fileiodemocode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected static final String [] EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS ={Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected static final int EXTERNAL_STORAGE_PERMISSION =10;


    protected File externalStorageDirectory;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * This method reads the text content for internal memory
     * @param fileName
     * @return String represented the read text
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected String readFromFile(String fileName) throws FileNotFoundException, IOException {
        String readString="";

        FileInputStream fileInputStream=openFileInput(fileName);
        InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder=new StringBuilder(readString);
        while ((readString=bufferedReader.readLine())!=null){
            stringBuilder.append(readString);
        }
        inputStreamReader.close();
        return stringBuilder.toString();
    }

    /**
     * This file writes the text to an internal file
     * @param fileName - name of the file
     * @param sourceText - The text that needs to be written to the file
     * @param MODE
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void writeToFile(String fileName,String sourceText,int MODE) throws
            FileNotFoundException,IOException{

        FileOutputStream fileOutputStream=openFileOutput(fileName,MODE);
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(sourceText);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    /**
     * Overloaded writeToFile which will be used by writeToExternalStorage method
     * @param file
     * @param sourceText
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private File writeToFile(File file,String sourceText) throws
            FileNotFoundException, IOException{
        FileWriter fileWriter=new FileWriter(file);
        fileWriter.write(sourceText);
        fileWriter.flush();
        fileWriter.close();
        return file;
    }

    /**
     * A utilitarian method that returns true if String is not empty
     * @param string
     * @return
     */
    protected boolean isStringEmpty(String string){
        if(string!=null && !string.equals("") && string.length()>0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Utilitarian method can be used to request any kind of permission
     *
     * @param activity - Activity on which the permission check is being done and where the call back will go
     * @param permissions - the permissions that needs to asked for
     * @param customPermissionConstant - Developer defined constant for a particular kind of permission
     * @param reason - String representing the reasoning why app needs this permission
     */
    protected void requestRunTimePermissions(final Activity activity, final String [] permissions, final int customPermissionConstant, String reason){
        if(permissions.length==1){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[0])){

                Snackbar.make(findViewById(android.R.id.content),"App needs permission to work",Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(activity,permissions,customPermissionConstant);
                            }
                        }).show();
            }else {
                ActivityCompat.requestPermissions(this,new String[]{permissions[0]},customPermissionConstant);
            }
        }else if(permissions.length>1){
            Snackbar.make(findViewById(android.R.id.content),"App needs multiple permissions to "+reason,Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(activity,permissions,customPermissionConstant);
                        }
                    }).show();
        }
    }

    /**
     * Utilitarian method to check whether a set of permissions are granted - returns false even if
     * of teh permissions for an operation is missing
     * @param permissions
     * @return
     */
    protected boolean arePermissionsGranted(String [] permissions){
        for(String permission: permissions){
            if(ActivityCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    /**
     * Writes a text to file on external storage
     * @param filename
     * @param text
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void writeToExternalStorageFile(String filename,String text) throws
            FileNotFoundException, IOException {
        if(isExternalStorageWritable()){
            externalStorageDirectory=new File(Environment.getExternalStorageDirectory(),getPackageName());
            externalStorageDirectory.mkdir();
            writeToFile(new File(externalStorageDirectory,filename),text);
        }
    }

    /**
     *  Reads the text from external Storage
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected String readTextFromExternalStorage(String fileName) throws
            FileNotFoundException, IOException{
        String readString="";
        if(isExternalStorageReadable()){
            if(externalStorageDirectory==null){
                externalStorageDirectory=new File(Environment.getExternalStorageDirectory(),getPackageName());
            }
            readString=readFromFile(new File(externalStorageDirectory.getAbsolutePath()+"/"+fileName));
        }
        return readString;
    }

    /**
     * Utlitarin method that takes File as an argument and returns the textual content of file
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String readFromFile(File file) throws FileNotFoundException, IOException{
        String readString="";
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder=new StringBuilder(readString);
        while ((readString=bufferedReader.readLine())!=null){
            stringBuilder.append(readString);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}