package nz.rozmus.terry.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

// The adapter, this supplies data to the ListView,
// it grabs an image in the background using an AsyncTask
public class ImageAdapter extends BaseAdapter {
    private static final String TAG = "Images";
    private List<String> uris = new ArrayList<>();
    private Context context;

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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        Log.i(TAG,"getView:" + position + "," + convertView);
        ViewHolder vh;
        if (convertView == null) {
            // If it's not recycled, inflate it from xml
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.image, viewGroup, false);

            // Create a new ViewHolder for it
            vh = new ViewHolder();
            vh.image = convertView.findViewById(R.id.image);

            // and set the tag to it
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag(); // otherwise get the viewholder
        }

        // set it's position
        vh.position = position;

        // and erase the image so we don't see old photos
        vh.image.setImageBitmap(null);

        // make an AsyncTask to load the image
        new AsyncTask<ViewHolder, Void, Bitmap>() {
            private ViewHolder vh;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

            @Override
            protected Bitmap doInBackground(ViewHolder... params) {
                vh=params[0];
                bitmapOptions.inJustDecodeBounds = true;

                // get the string for the uri file location
                String uri = uris.get(vh.position % uris.size());
                
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
                if(vh.position==position)
                    vh.image.setImageBitmap(bmp);
            }
        }.execute(vh);
        return convertView;
    }

    public void addUri(String uri) {
        uris.add(uri);
    }

    public void setContext(Context c) {
        context = c;
    }
}
