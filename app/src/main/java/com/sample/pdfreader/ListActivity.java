package com.sample.pdfreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.pdfreader.Adapters.PdfAdapter;

import java.io.File;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView pdfView;
    private ArrayList<Pdf> pdfArrayList;
    private PdfAdapter pdfAdapter;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        pdfView = (ListView) findViewById(R.id.pdf_list);

        pdfArrayList = new ArrayList<Pdf>();

//        walkdir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        showPhoneStatePermission();




    }

    public void pdfPicked(View view){

        Log.d(TAG, "pdfPicked: "+"running");
        String file_name = view.getTag().toString();

        Intent i = new Intent(ListActivity.this, MainActivity.class);
        i.putExtra("FILE_NAME", file_name);
        startActivity(i);

    }



    public void walkdir(File dir) {
        String pdfPattern = ".pdf";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pdfPattern)){
                        //Do what ever u want
                        pdfArrayList.add(new Pdf(listFile[i].getName()));

                    }
                }
            }
        }

        Log.d(TAG, "walkdir: "+pdfArrayList);
    }

    private void showPhoneStatePermission() {
        int permissionCheck = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.CAMERA},
//                    1);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission needed to access external storage", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
            } else {
                ActivityCompat.requestPermissions(ListActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_PHONE_STATE);

            }
        } else {
            Toast.makeText(ListActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            walkdir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            pdfAdapter = new PdfAdapter(this,pdfArrayList);

            pdfView.setAdapter(pdfAdapter);
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


                    }
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Log.d(TAG, "onRequestPermissionsResult: "+grantResults);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start capturing
                    Toast.makeText(ListActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
                    walkdir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                    pdfAdapter = new PdfAdapter(this,pdfArrayList);

                    pdfView.setAdapter(pdfAdapter);


                } else {

                    Toast.makeText(ListActivity.this, "Permission denied to access external storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }
}
