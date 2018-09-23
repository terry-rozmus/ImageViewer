package nz.rozmus.terry.imageviewer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.provider.MediaStore;
import android.database.Cursor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

//
// Show images on the phone
//
public class MainActivity extends Activity {
    //Button reload;
    GridView imagelist;
    ImageAdapter adapter = new ImageAdapter();

    private void getPhoneImageUris() {
        String[] star = {"*"};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                star, null, null, "date_added DESC");
        try {
            while (cursor.moveToNext()) {
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                adapter.addUri(uri);
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
            getPhoneImageUris();
        }
        setContentView(R.layout.activity_main);
        imagelist = findViewById(R.id.images);
        imagelist.setAdapter(adapter);

        // Pass current context to ImageAdapter
        adapter.setContext(getLayoutInflater().getContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            finish();
        else
            getPhoneImageUris();
    }
}

