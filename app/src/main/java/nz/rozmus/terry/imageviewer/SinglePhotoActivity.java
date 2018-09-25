package nz.rozmus.terry.imageviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;


public class SinglePhotoActivity extends AppCompatActivity {
    ListView img;
    ImageAdapter adapter = new ImageAdapter();
    String uri;
    int orientation;

    private void placePhoneImage() {
        //Toast.makeText(SinglePhotoActivity.this, "Test: " + uri, Toast.LENGTH_SHORT).show();
        adapter.addImageInfo(uri, orientation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the App bar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_single_photo);

        // Get the uri and orientation passed in from the main activity
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        orientation = Integer.valueOf(intent.getStringExtra("orientation"));

        // Place image if allowed
        if (Build.VERSION.SDK_INT > 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            placePhoneImage();
        }

        // Determine and store the screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        img = findViewById(R.id.imageView);
        img.setAdapter(adapter);

        // Initialise memory cache
        adapter.initialiseCache();

        // Send screen dimensions to adapter
        int imageHeight;
        int imageWidth;
        if (screenHeight > screenWidth) {
            imageHeight = screenWidth;
            imageWidth = screenHeight;
        } else {
            imageHeight = screenHeight;
            imageWidth = screenWidth;
        }
        adapter.setImageSize(imageHeight, imageWidth);

        // Pass current context to ImageAdapter
        adapter.setContext(getLayoutInflater().getContext());

        // Show a toast with the uri
        //Toast.makeText(SinglePhotoActivity.this, "" + uri, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish();
        else
            placePhoneImage();
    }
}
