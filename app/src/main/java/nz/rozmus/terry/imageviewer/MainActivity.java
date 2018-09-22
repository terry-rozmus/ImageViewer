package nz.rozmus.terry.imageviewer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.GridView;
import android.provider.MediaStore;
import android.net.Uri;
import android.widget.Toast;
import android.content.Context;

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

    // Return a string array of URIs for the images from the phone
    private List<String> getImages() {
        List<String> outUris = new ArrayList<>();

        String[] star = {"*"};
        if (Build.VERSION.SDK_INT > 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        Log.i("Permissions", "External Media Read Granted 2");
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                star, null, null, "date_added DESC");
        try {
            while (cursor.moveToNext()) {
                String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                outUris.add(uri);
            }
        } finally {
            cursor.close();
        }
        return outUris;
    }

    // The adapter, this supplies data to the ListView,
    // it grabs an image in the background using an AsyncTask
    public class ImageAdapter extends BaseAdapter {
        // Get the phone's image uris
        List<String> uris = new ArrayList<>();

        // Holds the beach imageview and it's position in the list
        class ViewHolder {
            int position;
            ImageView image;
        }

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
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            Log.i(TAG,"getView:"+i+","+convertView);
            ViewHolder vh;
            if (convertView == null) {
                // if it's not recycled, inflate it from xml
                convertView = getLayoutInflater().inflate(R.layout.image, viewGroup, false);
                // create a new ViewHolder for it
                vh=new ViewHolder();
                vh.image = convertView.findViewById(R.id.image);
                // and set the tag to it
                convertView.setTag(vh);
            } else
                vh=(ViewHolder)convertView.getTag(); // otherwise get the viewholder
            // set it's position
            vh.position=i;

            // and erase the image so we don't see old photos
            vh.image.setImageBitmap(null);

            // Get the uris for the images on the phone
            uris = getImages();
            Log.i("Uris List", uris.toString());

            // make an AsyncTask to load the image
            new AsyncTask<ViewHolder,Void,Bitmap>() {
                private ViewHolder vh;
                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    vh=params[0];
                    // get the string for the url
                    String uriStr = uris.get(vh.position%uris.size());
                    Uri targetUri = Uri.parse(uriStr);
                    Bitmap bitmap=null;
                    try {
                        Log.i(TAG,"Loading:" + uriStr);
                        if(vh.position!=i)
                            return null;
                        // decode the image into a bitmap
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    } catch (Exception e) {
                        Log.i(TAG,"Error Loading:" + uriStr);
                        e.printStackTrace();
                    }
                    // return the bitmap (might be null)
                    return bitmap;
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    // only set the imageview if the position hasn't changed.
                    if(vh.position==i)
                        vh.image.setImageBitmap(bitmap);
                }
            }.execute(vh);
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagelist = findViewById(R.id.images);
        adapter = new ImageAdapter();
        imagelist.setAdapter(adapter);
        reload = findViewById(R.id.button);
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
        if (grantResults.length > 0
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        } else {
            Log.i("Permissions", "External Media Read Granted 1");

        }
    }

}