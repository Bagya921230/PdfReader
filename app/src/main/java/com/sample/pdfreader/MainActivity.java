package com.sample.pdfreader;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements  View.OnClickListener,AdapterView.OnItemSelectedListener{
    public static ImageView NextButton,PreviousButton,BookmarkButton,PlusButton,MinusButton;
    public static PDFView pdfView;
    public static int curPage;
    public static ArrayList<Integer> bookMarks = new ArrayList<Integer>();
    private String[] bookmarkList;
    private List<String> pageList = new ArrayList<String>();
    private Button ShowButton;
    private Spinner spinner;
    private Boolean isNight;
    private String TAG;
    private RelativeLayout view;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private Button GoToButton;
    private Bitmap mBitmap;
    private ArrayList<Bitmap> pages_id;
    private PdfRenderer renderer;
    private ArrayList<Bitmap> tem_pages_id;
    //    private PageCurlView pageCurlView;
    private int pageCount;
    private boolean hasJumped;
    private int lastJumpVal;

    private final int REQUEST_PERMISSION_PHONE_STATE=1;
    private LinearLayout curlContainer;
    private int page;
    private String fileName;
//    private MyCurlView pageCurlView;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                fileName= null;
            } else {
                fileName= extras.getString("FILE_NAME");
            }
        } else {
            fileName= (String) savedInstanceState.getSerializable("FILE_NAME");
        }



        loadPrefs();

        NextButton = (ImageView) findViewById(R.id.nextBtn);

        PreviousButton = (ImageView) findViewById(R.id.prevBtn);

        BookmarkButton = (ImageView) findViewById(R.id.bookmarkBtn);

        PlusButton = (ImageView) findViewById(R.id.plusBtn);

        MinusButton = (ImageView) findViewById(R.id.minusBtn);

        ShowButton = (Button) findViewById(R.id.showBtn);

        GoToButton = (Button) findViewById(R.id.gotoBtn);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        NextButton.setOnClickListener(this);

        PreviousButton.setOnClickListener(this);

        BookmarkButton.setOnClickListener(this);

        PlusButton.setOnClickListener(this);

        MinusButton.setOnClickListener(this);

        ShowButton.setOnClickListener(this);

        GoToButton.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        view = (RelativeLayout) findViewById(R.id.relativeLayout_view);

        curlContainer = (LinearLayout) findViewById(R.id.curlContainer);



        /*********************************************************************************
        pdfView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {


            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                curPage = pdfView.getCurrentPage();
                YoYo.with(Techniques.FlipInY).duration(3500).delay(300).playOn(pdfView);
                loadPage(curPage-1);

            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                curPage = pdfView.getCurrentPage();
                YoYo.with(Techniques.FlipInY).duration(3500).delay(100).playOn(pdfView);
                loadPage(curPage+1);
            }


        });
        /************************************************************************************8*/


        showPhoneStatePermission();

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.plusBtn:
                curPage = pdfView.getCurrentPage();
                pdfView.zoomTo(1.25f);
                loadPage(curPage);

                break;

            case R.id.minusBtn:
                curPage = pdfView.getCurrentPage();
                pdfView.zoomTo(1.0f);
                loadPage(curPage);
                break;

            case R.id.nextBtn:

                curPage = pdfView.getCurrentPage();
                loadPage(curPage+1);
                ShowButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                break;

            case R.id.prevBtn:

                curPage = pdfView.getCurrentPage();
                loadPage(curPage-1);
                ShowButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                break;

            case R.id.gotoBtn:
                showInputDialog();
                ShowButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);
                break;

            case R.id.bookmarkBtn:

                ShowButton.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.GONE);

                curPage = pdfView.getCurrentPage();

                if(isBookMark(curPage)){
                    bookMarks.remove(new Integer(curPage));
                    BookmarkButton.setImageResource(R.drawable.bookmark);
                    Toast t1 = Toast.makeText(MainActivity.this,"Removed bookmark"+String.valueOf(curPage+1),Toast.LENGTH_SHORT);
                    t1.show();
                    storePreferences((ArrayList<Integer>) bookMarks);

                    pageList.remove("Page No:"+ String.valueOf(curPage+1));
                    if(pageList.isEmpty()){
                        ShowButton.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                    }
                }else{
                    bookMarks.add(curPage);
                    BookmarkButton.setImageResource(R.drawable.bookmarked);
                    Toast t2 = Toast.makeText(MainActivity.this,"Bookmarked page no:"+String.valueOf(curPage+1),Toast.LENGTH_SHORT);
                    t2.show();
                    storePreferences((ArrayList<Integer>) bookMarks);
                    pageList.add("Page No:"+ String.valueOf(curPage+1));
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pageList);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);
                }




                break;
            case R.id.showBtn:

                if(pageList.isEmpty()){

                    Toast t2 = Toast.makeText(MainActivity.this,"No saved bookmarks",Toast.LENGTH_SHORT);
                    t2.show();

                }else{
                    spinner.setVisibility(View.VISIBLE);
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pageList);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);
                    ShowButton.setVisibility(View.GONE);
                }


        }
    }

    public static int getCurrentPage(){
        return pdfView.getCurrentPage();
    }

    public void loadPage(String fileName) throws IOException {


        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+fileName);
        pdfView.fromFile(file)
                .enableSwipe(false)
//                .onDraw(new OnDrawListener() {
//                    @Override
//                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//                        Paint paint = new Paint();
//                        paint.setColor(Color.parseColor("#CD5C5C"));
//                        canvas.drawRect(50, 80, 200, 200, paint);
//                    }
//                })
                .load();

        showBookmark();

    }

    public static void loadPage(int number){


        pdfView.jumpTo(number);
//        YoYo.with(Techniques.FadeIn).duration(1500).delay(0).playOn(pdfView);



        showBookmark();
    }

    public boolean isBookMark(int page){


        if(bookMarks.isEmpty()==false && bookMarks.contains(page)){
            return true;
        }else{
            return false;
        }
    }

    public static void showBookmark(){


//        if(hasJumped){
        curPage = pdfView.getCurrentPage();
//            page = pageCurlView.mIndex+lastJumpVal-1;
//        }else{
//            page = pageCurlView.mIndex;
//        }

        int lastPage = pdfView.getPageCount();

        if(curPage == 0){
            PreviousButton.setImageResource(R.drawable.en_left_arrow);
        }else if(curPage == lastPage-1){
            NextButton.setImageResource(R.drawable.en_right_arrow);
        }else{
            PreviousButton.setImageResource(R.drawable.left_arrow);
            NextButton.setImageResource(R.drawable.right_arrow);
        }

        if(bookMarks.isEmpty()==false && bookMarks.contains(curPage)){

            BookmarkButton.setImageResource(R.drawable.bookmarked);
//            BookmarkButton.setClickable(false);
        }else{
            BookmarkButton.setImageResource(R.drawable.bookmark);
//            BookmarkButton.setClickable(true);
        }

    }


    private void storePreferences(ArrayList<Integer> bookmarks) {

        ArrayList<String> newList = new ArrayList<String>(bookmarks.size());
        for (Integer myInt : bookmarks) {
            newList.add(String.valueOf(myInt));
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        Set<String> set = new HashSet<String>();
        set.addAll(newList);
        editor.putStringSet(fileName, set);
        editor.commit();

        editor.apply();
    }

    private void loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Set<String> set = sp.getStringSet(fileName, null);

        if(set!=null){
            bookmarkList =set.toArray(new String[set.size()]);
            for(String s : bookmarkList) pageList.add("Page No:"+ String.valueOf(Integer.valueOf(s)+1));
//        List<String> list = new ArrayList<String>(set);
            for(String s : bookmarkList) bookMarks.add(Integer.valueOf(s));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item

        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();

        String s=item;
        String[] parts = s.split(":"); // escape .
        String part1 = parts[0];
        String part2 = parts[1];

        int number = Integer.valueOf(part2);
        loadPage(number-1);
//        jumpTo(number);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean checkNight(){

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour < 6 || hour > 18){
            return true;
        } else {
            return false;
        }

    }


    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.requestFocus();
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            int input = Integer.valueOf(editText.getText().toString());


                            if(input>pdfView.getPageCount()){
                                Toast t1 = Toast.makeText(MainActivity.this,"This PDF contains only "+String.valueOf(pdfView.getPageCount())+" pages",Toast.LENGTH_SHORT);
                                t1.show();
                            }else{
                                loadPage(input-1);
//                                jumpTo(input);
                            }


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Field Required!");
            return false;
        }

        return true;
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
                    Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
                    try {
                        loadPage(fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                    pageCurlView.setCurlView(pages_id);
//                    pageCurlView.setCurlSpeed(120);


                } else {

                    Toast.makeText(MainActivity.this, "Permission denied to access camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
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
                showExplanation("Permission needed to access external storage", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

//                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_PHONE_STATE);

            }
        } else {
            Toast.makeText(MainActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            try {
                loadPage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


                    }
                });
        builder.create().show();
    }

}
