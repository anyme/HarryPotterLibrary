package com.example.anastasia.harrypotterlibrary;

import android.content.Context;
import android.content.Intent;
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

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends ActionBarActivity {

    private final String mURLJsonArray = "http://henri-potier.xebia.fr/books";
    private CustomGrid mAdapter;
    private GridView mGrid;
    private TextView mCounterView;
    private int mCounter = 0;
    private ArrayList<MyApplication.BookClass> mJSONResponse;
    private Context self = this;
    private Menu mMenu;
    MyApplication mApplication;

    class CustomGrid extends BaseAdapter {
        private Context mContext;

        public CustomGrid() {
            mContext = self;
        }
        @Override
        public int getCount() {
            return mJSONResponse.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View grid = inflater.inflate(R.layout.grid_item, null);
            MyApplication.BookClass aBook = mJSONResponse.get(position);

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
                        if (mCounter > 0) {
                            enableMenuClick();
                        } else {
                            disableMenuClick();
                        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApplication = (MyApplication) getApplicationContext();
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
                    public void onErrorResponse(VolleyError error) {
                        TextView listTitle = (TextView) findViewById(R.id.list_title);
                        if (error instanceof NetworkError) {
                            listTitle.setText(R.string.network_problem_message);
                        }


                    }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void parseJSONResponse(JSONArray jsonArray) {
        mJSONResponse = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
           try {
               JSONObject aBook = (JSONObject) jsonArray.get(i);
               MyApplication.BookClass aBookInstance = mApplication.createBookClassInstance(i, aBook.getString("title"),
                       aBook.getInt("price"),
                       aBook.getString("cover"));

               aBookInstance.loadImage();

               mJSONResponse.add(aBookInstance);

           } catch (JSONException e) {}

        }
        mApplication.setJSONResponse(mJSONResponse);
    }


    private void inflateListWithData() {
        mAdapter = new CustomGrid();
        mApplication.setGridAdapter(mAdapter);
        mGrid = (GridView) findViewById(R.id.gridview);
        mGrid.setAdapter(mAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.action_show_items).getActionView();
        mCounterView = (TextView) badgeLayout.findViewById(R.id.counter);

        badgeLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, CommercialOfferActivity.class);
                startActivity(intent);
            }
        });

        disableMenuClick();

        return super.onCreateOptionsMenu(menu);

    }

    private void enableMenuClick() {
        RelativeLayout badgeLayout = (RelativeLayout) mMenu.findItem(R.id.action_show_items).getActionView();
        badgeLayout.setClickable(true);
    }

    private void disableMenuClick() {
        RelativeLayout badgeLayout = (RelativeLayout) mMenu.findItem(R.id.action_show_items).getActionView();
        badgeLayout.setClickable(false);
    }

}
