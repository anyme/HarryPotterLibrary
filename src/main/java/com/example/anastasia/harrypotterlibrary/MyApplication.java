package com.example.anastasia.harrypotterlibrary;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anastasia on 15/02/15.
 */
public class MyApplication extends Application {
    private ArrayList<BookClass> mJSONResponse;
    private BaseAdapter mGridAdapter;
    /**
     * Created by anastasia on 15/02/15.
     */
    public class BookClass {
        private int mKey = -1;
        private String mTitle = "";
        private int mPrice = 0;
        private String mPictureURL = "";
        private Drawable mImage;
        private String mISBN = "";
        private boolean mSelected = false;

        BookClass(int aKey, String aTitle, Integer aPrice, String aPictureURL, String aISBN) {
            mKey = aKey;
            mTitle = aTitle;
            mPrice = aPrice;
            mPictureURL = aPictureURL;
            mISBN = aISBN;
        }

        private class LoadRemoteImageTask extends AsyncTask<String, Void, Drawable> {

            int key;

            LoadRemoteImageTask(int aKey) {
                key = aKey;
            }

            @Override
            protected Drawable doInBackground(String... aURL) {
                try {
                    InputStream is = (InputStream) new URL(aURL[0]).getContent();
                    Drawable d = Drawable.createFromStream(is, "src name");
                    return d;
                } catch (Exception e) {}
                return null;
            }

            protected void onPostExecute(Drawable result) {
                mJSONResponse.get(key).setImage(result);
                mGridAdapter.notifyDataSetChanged();
            }
        }


        public String getTitle() {
            return mTitle;
        }

        public String getPriceString() {
            return String.valueOf(mPrice) + "€";
        }

        public int getPrice() {
            return mPrice;
        }

        public String getPictureURL() {
            return mPictureURL;
        }

        public String getISBN() {
            return mISBN;
        }

        public void loadImage() {
            LoadRemoteImageTask loadImageTask = new LoadRemoteImageTask(mKey);
            loadImageTask.execute(mPictureURL);
        }

        public void setImage(Drawable anImage) {
            mImage = anImage;
        }

        public Drawable getImage() {
            return mImage;
        }

        public void setSelected() {
            mSelected = true;
        }

        public void setDeselected() {
            mSelected = false;
        }

        public boolean isSelected() {
            return mSelected;
        }
    }

    public ArrayList<BookClass> getJSONResponse() {
        return mJSONResponse;
    }

    public void setJSONResponse(ArrayList<BookClass> aJSONResponse) {
        mJSONResponse = aJSONResponse;
    }

    public BookClass createBookClassInstance(int aKey, String aTitle, Integer aPrice, String aPictureURL, String aISBN) {
        return new BookClass(aKey, aTitle, aPrice, aPictureURL, aISBN);
    }

    public void setGridAdapter(BaseAdapter aGridAdapter){
        mGridAdapter = aGridAdapter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
