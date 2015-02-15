package com.example.anastasia.harrypotterlibrary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;



public class MainActivity extends ActionBarActivity {

    class BookClass {
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



    class CustomGrid extends BaseAdapter {
        private Context mContext;

        public CustomGrid() {
            mContext = self;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mJSONResponse.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View grid = inflater.inflate(R.layout.grid_item, null);
            BookClass aBook = mJSONResponse.get(position);

            if (aBook != null) {
                CheckBox checkBox = (CheckBox) grid.findViewById(R.id.grid_item_checkbox);
                TextView titleView = (TextView) grid.findViewById(R.id.grid_item_title);
                TextView priceView = (TextView) grid.findViewById(R.id.grid_item_price);
                ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);

                checkBox.setId(position);
                checkBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        int id = cb.getId();

                        if (mJSONResponse.get(id).isSelected()) {
                            mJSONResponse.get(id).setDeselected();
                            --mCounter;
                        } else {
                            mJSONResponse.get(id).setSelected();
                            ++mCounter;
                        }
                        mCounterView.setText(String.valueOf(mCounter));
                    }
                });

                if (mJSONResponse.get(position).isSelected()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }

                titleView.setText(aBook.getTitle());
                priceView.setText(aBook.getPriceString());

                imageView.setImageDrawable(aBook.getImage());

            } else {
                grid = convertView;
            }
            return grid;
        }
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
            mAdapter.notifyDataSetChanged();
        }
    }


    private final String mURLJsonArray = "http://henri-potier.xebia.fr/books";
    private CustomGrid mAdapter;
    private GridView mGrid;
    private TextView mCounterView;
    private int mCounter = 0;
    private ArrayList<BookClass> mJSONResponse;
    private Context self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeJSONArrayRequest();
    }

    private void makeJSONArrayRequest() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(mURLJsonArray,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSONResponse(response);
                        inflateListWithData();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void parseJSONResponse(JSONArray jsonArray) {
        mJSONResponse = new ArrayList<BookClass>();
        for (int i = 0; i < jsonArray.length(); ++i) {
           try {
               JSONObject aBook = (JSONObject) jsonArray.get(i);
               BookClass aBookInstance = new BookClass(i, aBook.getString("title"),
                       aBook.getInt("price"),
                       aBook.getString("cover"),
                       aBook.getString("isbn"));

               aBookInstance.loadImage();

               mJSONResponse.add(aBookInstance);

           } catch (JSONException e) {}

        }
    }


    private void inflateListWithData() {
        mAdapter = new CustomGrid();
        mGrid = (GridView) findViewById(R.id.gridview);
        mGrid.setAdapter(mAdapter);
       /* mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + mJSONResponse.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.action_show_items).getActionView();
        mCounterView = (TextView) badgeLayout.findViewById(R.id.counter);

        return super.onCreateOptionsMenu(menu);


       /* getMenuInflater().inflate(R.menu.menu_main, menu);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.badge).getActionView();
        mCounterView = (TextView) badgeLayout.findViewById(R.id.counter);
        return true;*/
    }

}
