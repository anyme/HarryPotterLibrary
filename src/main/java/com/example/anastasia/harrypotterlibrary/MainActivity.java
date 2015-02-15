package com.example.anastasia.harrypotterlibrary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
        private int key = -1;
        private String title = "";
        private int price = 0;
        private String pictureURL = "";
        private Drawable image;
        private String isbn = "";

        BookClass(int aKey, String aTitle, Integer aPrice, String aPictureURL, String aISBN) {
            key = aKey;
            title = aTitle;
            price = aPrice;
            pictureURL = aPictureURL;
            isbn = aISBN;
        }

       public String getTitle() {
           return title;
       }

       public String getPriceString() {
           return String.valueOf(price) + "€";
       }

       public int getPrice() {
            return price;
        }

       public String getPictureURL() {
           return pictureURL;
       }

       public String getISBN() {
           return isbn;
       }

       public void loadImage() {
           LoadRemoteImageTask loadImageTask = new LoadRemoteImageTask(key);
           loadImageTask.execute(pictureURL);
       }

       public void setImage(Drawable anImage) {
           image = anImage;
       }

       public Drawable getImage() {
           return image;
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
            return jsonResponse.size();
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
            BookClass aBook = jsonResponse.get(position);

            if (aBook != null) {
                CheckBox checkBox = (CheckBox) grid.findViewById(R.id.grid_item_checkbox);
                TextView titleView = (TextView) grid.findViewById(R.id.grid_item_title);
                TextView priceView = (TextView) grid.findViewById(R.id.grid_item_price);
                ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);

                checkBox.setTag(position);
                checkBox.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });

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
            jsonResponse.get(key).setImage(result);
        }
    }


    private final String urlJsonArray = "http://henri-potier.xebia.fr/books";
    private GridView grid;

    private ArrayList<BookClass> jsonResponse;

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
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(urlJsonArray,
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
        jsonResponse = new ArrayList<BookClass>();
        for (int i = 0; i < jsonArray.length(); ++i) {
           try {
               JSONObject aBook = (JSONObject) jsonArray.get(i);
               BookClass aBookInstance = new BookClass(i, aBook.getString("title"),
                       aBook.getInt("price"),
                       aBook.getString("cover"),
                       aBook.getString("isbn"));

               aBookInstance.loadImage();

               jsonResponse.add(aBookInstance);

           } catch (JSONException e) {}

        }
    }


    private void inflateListWithData() {

        CustomGrid adapter = new CustomGrid();
        grid = (GridView) findViewById(R.id.gridview);
        grid.setAdapter(adapter);
       /* grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + jsonResponse.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

}
