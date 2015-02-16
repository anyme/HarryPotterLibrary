package com.example.anastasia.harrypotterlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CommercialOfferActivity extends ActionBarActivity {

    MyApplication mApplication;
    private final String mURLJsonObject = "http://henri-potier.xebia.fr/books/c8fabf68-8374-48fe-a7ea-a00ccd07afff,a460afed-e5e7-4e39-a39d-c885c05db861/commercialOffers";
    private ArrayList<MyApplication.BookClass> mJSONResponse;
    private CustomList mAdapter;
    private ListView mList;
    private int mTotal = 0;
    private String initialText;


    class CustomList extends BaseAdapter {
        private Context mContext;

        public CustomList() {
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
            View list = inflater.inflate(R.layout.list_item, null);
            MyApplication.BookClass aBook = mJSONResponse.get(position);

            if (aBook.isSelected()) {
                TextView titleView = (TextView) list.findViewById(R.id.list_item_title);
                TextView priceView = (TextView) list.findViewById(R.id.list_item_price);
                ImageView imageView = (ImageView) list.findViewById(R.id.list_item_image);

                titleView.setText(aBook.getTitle());
                priceView.setText(aBook.getPriceString());

                imageView.setImageDrawable(aBook.getImage());
            }

            return list;
        }
    }

    private Context self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commercial_offer);
        mApplication = (MyApplication)getApplicationContext();
        fetchSelectedItems();
        inflateListWithData();
        TextView totalView = (TextView) findViewById(R.id.total_textview);
        initialText = (String) totalView.getText();
        totalView.append("  " + mTotal);
        loadOfferSettings();
    }

    private void inflateListWithData() {
        mAdapter = new CustomList();
        mApplication.setGridAdapter(mAdapter);
        mList = (ListView) findViewById(R.id.listview);
        mList.setAdapter(mAdapter);
    }

    private void fetchSelectedItems() {
        ArrayList<MyApplication.BookClass> jsonResponse = mApplication.getJSONResponse();
        mJSONResponse = new ArrayList<>();

        for (int i = 0; i < jsonResponse.size(); i++) {
            if (jsonResponse.get(i).isSelected()) {
                mJSONResponse.add(jsonResponse.get((i)));
                mTotal += jsonResponse.get(i).getPrice();
            }
        }

    }

    private void loadOfferSettings() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, mURLJsonObject, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        applyCommercialOffer(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

    private void applyCommercialOffer(JSONObject aResponse) {
        HashMap<String, Integer> offer = new HashMap<>();
        Integer sliceValue = 1;

        try {
            JSONArray array = aResponse.getJSONArray("offers");
            for (int i = 0; i < 3; ++i) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                offer.put(jsonObject.getString("type"), jsonObject.getInt("value"));
                if (jsonObject.getString("type").equals("slice"))
                    sliceValue = jsonObject.getInt("sliceValue");
            }
        } catch (Exception e) {}

        Integer percentageOffer = mTotal - offer.get("percentage") * mTotal / 100;
        Integer minusOffer = mTotal - offer.get("minus");
        Integer sliceOffer = mTotal - (mTotal/sliceValue + mTotal%sliceValue == 0 ? 1 : 0) * offer.get("slice");

        Integer min = percentageOffer < minusOffer ? percentageOffer : minusOffer;
        min = min < sliceOffer ? min : sliceOffer;

        TextView totalView = (TextView) findViewById(R.id.total_textview);
        totalView.setText(initialText + "  " + min + "€  instead of  " + mTotal +"€");

    }


}
