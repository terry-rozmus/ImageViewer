package nz.rozmus.terry.imageviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.provider.MediaStore;
import android.database.Cursor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

//
// Show images on the phone
//
public class MainActivity extends AppCompatActivity {
    GridView imagelist;
    ImageAdapter adapter;
    int screenHeight;
    int screenWidth;
    boolean firstTime;
    boolean toLarge;

    private void getPhoneImages() {
        String[] star = {"*"};
        adapter = new ImageAdapter();
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                star, null, null, "date_added DESC");
        try {
            while (cursor.moveToNext()) {
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
                adapter.addImageInfo(uri, orientation);
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoneImages();
        }

        // Determine and store the screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        // Make sure the action bar is hidden
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        firstTime = true;
        toLarge = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Do a first load or reload when returning from an activity
        // outside the Image Viewer app
        if (!toLarge || firstTime) {
            if (Build.VERSION.SDK_INT > 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPhoneImages();
            }

            imagelist = findViewById(R.id.images);
            firstTime = false;

            imagelist.setAdapter(adapter);

            // Initialise memory cache
            adapter.initialiseCache();

            // Send square image dimensions to adapter
            int imageSize = Math.min(screenWidth, screenHeight) / 3;
            adapter.setImageSize(imageSize, imageSize);

            // Pass current context to ImageAdapter
            adapter.setContext(getLayoutInflater().getContext());

            GridView gridview = (GridView) findViewById(R.id.images);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    // Pass uri to the single photo viewer activity and start it
                    Intent myIntent = new Intent(MainActivity.this, SinglePhotoActivity.class);
                    myIntent.putExtra("uri", adapter.getUri(position));
                    myIntent.putExtra("orientation", String.valueOf(adapter.getOrientation(position)));
                    MainActivity.this.startActivity(myIntent);
                    toLarge = true;
                }
            });
        }
        toLarge = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish();
        else
            getPhoneImages();
    }
}

