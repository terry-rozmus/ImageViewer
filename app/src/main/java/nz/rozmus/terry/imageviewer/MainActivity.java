package nz.rozmus.terry.imageviewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.GridView;
import android.provider.MediaStore;
import android.database.Cursor;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.net.Uri;

import java.util.List;
import java.util.ArrayList;

//
// Show images on phone
//

public class MainActivity extends Activity {
    private static final String TAG = "Images";
    Button reload;
    GridView imagelist;
    ImageAdapter adapter;
    List<String> uris = new ArrayList<>();

    // The adapter, this supplies data to the ListView,
    // it grabs an image in the background using an AsyncTask
    public class ImageAdapter extends BaseAdapter {
        // Holds phone image and it's position in the list
        class ViewHolder {
            int position;
            ImageView image;
        }

        // How many items in the image uris ListView
        @Override
        public int getCount() {
            return uris.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            Log.i(TAG,"getView:" + i + "," + convertView);
            ViewHolder vh;
            if (convertView == null) {
                // if it's not recycled, inflate it from xml
                convertView = getLayoutInflater().inflate(R.layout.image, viewGroup, false);
                // create a new ViewHolder for it
                vh=new ViewHolder();
                vh.image=convertView.findViewById(R.id.image);
                // and set the tag to it
                convertView.setTag(vh);
            } else
                vh=(ViewHolder)convertView.getTag(); // otherwise get the viewholder
            // set it's position
            vh.position=i;
            // and erase the image so we don't see old photos
            vh.image.setImageBitmap(null);
            // make an AsyncTask to load the image
            new AsyncTask<ViewHolder,Void,Bitmap>() {
                private ViewHolder vh;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    vh=params[0];
                    bitmapOptions.inJustDecodeBounds = true;

                    // get the string for the uri file location
                    String uri = uris.get(vh.position%uris.size());

                    Bitmap bitmap = null;
                    try {
                        // Get the size of the image
                        BitmapFactory.decodeFile(uri, bitmapOptions);

                        // Find scaling factor
                        int dimension = Math.min(bitmapOptions.outHeight, bitmapOptions.outWidth);
                        float scale = (float) dimension / 100;

                        // Find sampling size
                        int sampleSize = 1;
                        while (sampleSize < scale) {
                            sampleSize *= 2; // Must be a power of 2
                        }
                        bitmapOptions.inSampleSize = sampleSize;

                        // Decode the phone images into bitmaps
                        bitmapOptions.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeFile(uri, bitmapOptions);
                    } catch (Exception e) {
                        Log.i(TAG,"Error Loading:" + uri);
                        e.printStackTrace();
                    }
                    // return the bitmap (might be null)
                    return bitmap;
                }
                @Override
                protected void onPostExecute(Bitmap bmp) {
                    // only set the imageview if the position hasn't changed.
                    if(vh.position==i)
                        vh.image.setImageBitmap(bmp);
                }
            }.execute(vh);
            return convertView;
        }
    }

    void getPhoneImageUris() {
        String[] star = {"*"};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                star, null, null, "date_added DESC");
        try {
            while (cursor.moveToNext()) {
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                uris.add(uri);
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
        imagelist =findViewById(R.id.images);
        adapter=new ImageAdapter();
        imagelist.setAdapter(adapter);
        reload=findViewById(R.id.button);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();
            }
        });
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

