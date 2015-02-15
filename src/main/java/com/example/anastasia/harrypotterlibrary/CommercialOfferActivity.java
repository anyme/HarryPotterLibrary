package com.example.anastasia.harrypotterlibrary;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CommercialOfferActivity extends ActionBarActivity {

    MyApplication mApplication;
    private ArrayList<MyApplication.BookClass> mJSONResponse;
    private CustomList mAdapter;
    private ListView mList;
    private int mTotal = 0;


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
        totalView.append("  " + mTotal);
    }

    private void inflateListWithData() {
        mAdapter = new CustomList();
        mApplication.setGridAdapter(mAdapter);
        mList = (ListView) findViewById(R.id.listview);
        mList.setAdapter(mAdapter);
    }

    private void fetchSelectedItems() {
        ArrayList<MyApplication.BookClass> jsonResponse = mApplication.getJSONResponse();
        mJSONResponse = new ArrayList<MyApplication.BookClass>();

        for (int i = 0; i < jsonResponse.size(); i++) {
            if (jsonResponse.get(i).isSelected()) {
                mJSONResponse.add(jsonResponse.get((i)));
                mTotal += jsonResponse.get(i).getPrice();
            }
        }

    }



}
