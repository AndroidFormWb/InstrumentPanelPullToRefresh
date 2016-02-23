package com.cc.instrumentpanel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cc.instrumentpanel.refresh.PullToRefreshBase;
import com.cc.instrumentpanel.refresh.PullToRefreshBase.OnRefreshListener;
import com.cc.instrumentpanel.refresh.PullToRefreshListView;


public class MainActivity extends Activity {

	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mPullListView = (PullToRefreshListView) findViewById(R.id.refreshable_view);
		mPullListView.setPullLoadEnabled(false);
	    mPullListView.setScrollLoadEnabled(false);
	    
	    mListView = (ListView) mPullListView.getRefreshableView();
	    
	    ArrayList<String> datas = new ArrayList<String>();
    	for (int i = 0; i < 50 ; i++) {
    		datas.add("name" + i);
    	}
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, datas);
	    mListView.setAdapter(adapter);
	    
	    mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //mIsStart = true;
                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //mIsStart = false;
                new GetDataTask().execute();
            }
        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            boolean hasMoreData = true;
            
//            mAdapter.notifyDataSetChanged();
            mPullListView.onPullDownRefreshComplete();
            mPullListView.onPullUpRefreshComplete();
            mPullListView.setHasMoreData(hasMoreData);
            setLastUpdateTime();

            super.onPostExecute(result);
        }
    }
    
    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        mPullListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        
        return mDateFormat.format(new Date(time));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.type_normal) {
        	mPullListView.setHeaderLayoutType(PullToRefreshListView.NORMAL_HEADER_LAYOUT);
            return true;
        }else if (id == R.id.type_reall) {
        	mPullListView.setHeaderLayoutType(PullToRefreshListView.REAL_HEADER_LAYOUT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
