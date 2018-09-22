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
import java.net.URL;
import java.net.URLConnection;

//
// Show images on phone
//

public class MainActivity extends Activity {
    private static final String TAG = "Images";
    Button reload;
    GridView imagelist;
    ImageAdapter adapter;
    // list of camera URLs, anything that returns a jpeg will work here.
    String urls[]={
            "https://www.surf2surf.com/reports/freecams/RG",
            "https://www.surf2surf.com/reports/freecams/MW",
            "https://www.surf2surf.com/reports/freecams/PH",
            "https://www.surf2surf.com/reports/freecams/NP",
            "https://www.surf2surf.com/reports/freecams/TA",
            "https://www.surf2surf.com/reports/freecams/MB",
            "https://www.surf2surf.com/reports/freecams/WG",
            "https://www.surf2surf.com/reports/freecams/MM",
            "https://www.surf2surf.com/reports/freecams/MC",
            "https://www.surf2surf.com/reports/freecams/HB",
            "https://www.surf2surf.com/reports/freecams/WM",
            "https://www.surf2surf.com/reports/freecams/GS",
            "https://www.surf2surf.com/reports/freecams/WA",
            "https://www.surf2surf.com/reports/freecams/DN",

            "https://www.surf2surf.com/reports/freecams/RG",
            "https://www.surf2surf.com/reports/freecams/MW",
            "https://www.surf2surf.com/reports/freecams/PH",
            "https://www.surf2surf.com/reports/freecams/NP",
            "https://www.surf2surf.com/reports/freecams/TA",
            "https://www.surf2surf.com/reports/freecams/MB",
            "https://www.surf2surf.com/reports/freecams/WG",
            "https://www.surf2surf.com/reports/freecams/MM",
            "https://www.surf2surf.com/reports/freecams/MC",
            "https://www.surf2surf.com/reports/freecams/HB",
            "https://www.surf2surf.com/reports/freecams/WM",
            "https://www.surf2surf.com/reports/freecams/GS",
            "https://www.surf2surf.com/reports/freecams/WA",
            "https://www.surf2surf.com/reports/freecams/DN",

            "https://www.surf2surf.com/reports/freecams/RG",
            "https://www.surf2surf.com/reports/freecams/MW",
            "https://www.surf2surf.com/reports/freecams/PH",
            "https://www.surf2surf.com/reports/freecams/NP",
            "https://www.surf2surf.com/reports/freecams/TA",
            "https://www.surf2surf.com/reports/freecams/MB",
            "https://www.surf2surf.com/reports/freecams/WG",
            "https://www.surf2surf.com/reports/freecams/MM",
            "https://www.surf2surf.com/reports/freecams/MC",
            "https://www.surf2surf.com/reports/freecams/HB",
            "https://www.surf2surf.com/reports/freecams/WM",
            "https://www.surf2surf.com/reports/freecams/GS",
            "https://www.surf2surf.com/reports/freecams/WA",
            "https://www.surf2surf.com/reports/freecams/DN",

    };
    // The adapter, this supplies data to the ListView,
    // it grabs an image in the background using an AsyncTask
    public class ImageAdapter extends BaseAdapter {
        // Holds the beach imageview and it's position in the list
        class ViewHolder {
            int position;
            ImageView image;
        }
        // How many items in the ListView, should be urls.length but make it 1000 to show
        // long lists
        @Override
        public int getCount() {
            return urls.length;
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
            Log.i(TAG,"getView:"+i+","+convertView);
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
                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    vh=params[0];
                    // get the string for the url
                    String address=urls[vh.position%urls.length];
                    Bitmap bmp=null;
                    try {
                        Log.i(TAG,"Loading:"+address);
                        URL url = new URL(address);
                        // open network connection
                        URLConnection connection=url.openConnection();
                        // vh position might have changed
                        if(vh.position!=i)
                            return null;
                        // decode the jpeg into a bitmap
                        bmp = BitmapFactory.decodeStream(connection.getInputStream());
                    } catch (Exception e) {
                        Log.i(TAG,"Error Loading:"+address);
                        e.printStackTrace();
                    }
                    // return the bitmap (might be null)
                    return bmp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagelist =findViewById(R.id.beaches);
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
}

