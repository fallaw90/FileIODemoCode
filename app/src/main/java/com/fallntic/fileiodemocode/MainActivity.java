package com.fallntic.fileiodemocode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends BaseActivity implements View.OnClickListener{


    private static final String TAG=MainActivity.class.getSimpleName();

    private static final String FILE_NAME="sample.txt";

    private Context mContext;

    private Button buttonWriteToFile,buttonReadFromFile;
    private TextView textViewContentFromFile;
    private EditText editTextUserMessage;
    private UserAction recentUserAction;
    enum UserAction{
        READ,WRITE
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();

        buttonReadFromFile=(Button)findViewById(R.id.buttonWriteToFile);
        buttonWriteToFile=(Button)findViewById(R.id.buttonReadFromFile);
        textViewContentFromFile=(TextView)findViewById(R.id.textViewContentFromFile);
        editTextUserMessage=(EditText)findViewById(R.id.editTextUserMessage);

        buttonReadFromFile.setOnClickListener(this);
        buttonWriteToFile.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonWriteToFile: writeContentToFile(); break;
            case R.id.buttonReadFromFile: populateTheReadText();break;
            default: break;
        }
    }


    private void writeContentToFile(){
        recentUserAction=UserAction.WRITE;
        String string=editTextUserMessage.getText().toString();
        if(isStringEmpty(string)){
            try{
                if(arePermissionsGranted(EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS)){
                    writeToExternalStorageFile(FILE_NAME,string);
                }else{
                    requestRunTimePermissions(this, EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS, EXTERNAL_STORAGE_PERMISSION,"to READ/WRITE to external storage");
                }
            }catch (FileNotFoundException exception){
                Toast.makeText(mContext,getString(R.string.error_string_file_not_found),Toast.LENGTH_SHORT).show();
            } catch (IOException exception ){
                Toast.makeText(mContext,getString(R.string.error_string_io_exception),Toast.LENGTH_SHORT).show();
            }catch (Exception exception){
                Toast.makeText(mContext,getString(R.string.error_generic),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populatTextFromPreviousSession();
    }

    private void populateTheReadText(){
        recentUserAction=UserAction.READ;
        try{
            if(arePermissionsGranted(EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS)){
                textViewContentFromFile.setText(readTextFromExternalStorage(FILE_NAME));
                textViewContentFromFile.setVisibility(View.VISIBLE);
            }else{
                requestRunTimePermissions(this, EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS, EXTERNAL_STORAGE_PERMISSION,"to READ/WRITE to external card");
            }
        }catch (FileNotFoundException exception){
            Toast.makeText(mContext,getString(R.string.error_string_file_not_found),Toast.LENGTH_SHORT).show();
            textViewContentFromFile.setVisibility(View.GONE);
        } catch (IOException exception ){
            Toast.makeText(mContext,getString(R.string.error_string_io_exception),Toast.LENGTH_SHORT).show();
            textViewContentFromFile.setVisibility(View.GONE);
        }catch (Exception exception){
            Toast.makeText(mContext,getString(R.string.error_generic),Toast.LENGTH_SHORT).show();
            textViewContentFromFile.setVisibility(View.GONE);
        }
    }

    private void populatTextFromPreviousSession(){
        recentUserAction=UserAction.READ;
        String readContent=null;
        try{
            if(arePermissionsGranted(EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS)){
                readContent= readTextFromExternalStorage(FILE_NAME);
            }else{
                requestRunTimePermissions(this, EXTERNAL_STORAGE_READ_WRITE_PERMISSIONS, EXTERNAL_STORAGE_PERMISSION,"to READ/WRITE external storage");
            }

            textViewContentFromFile.setVisibility(View.VISIBLE);
        }catch (Exception exception){
            Toast.makeText(mContext,getString(R.string.error_generic),Toast.LENGTH_SHORT).show();
            textViewContentFromFile.setVisibility(View.GONE);
        }
        textViewContentFromFile.setText("From Previous Session: \n "+readContent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(arePermissionsGranted(permissions) && requestCode== EXTERNAL_STORAGE_PERMISSION){
            if(recentUserAction==UserAction.WRITE){
                writeContentToFile();
            }else if(recentUserAction==UserAction.READ){
                populateTheReadText();
            }
        }
    }
}