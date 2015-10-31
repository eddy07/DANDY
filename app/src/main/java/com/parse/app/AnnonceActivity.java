package com.parse.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.app.adapter.AnnonceAdapter;
import com.parse.app.model.Annonce;
import com.parse.app.model.Tontine;
import com.parse.app.utilities.NetworkUtil;

import java.util.ArrayList;
import java.util.List;


public class AnnonceActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private AnnonceAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<Annonce> annonces = new ArrayList<Annonce>();
    public static final int TYPE_NOT_CONNECTED = 0;
    private String tontineId;
    private Context context;
    private boolean reachedTop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);
        getSupportActionBar().setTitle("Annonces");
        context = this;
        tontineId = getIntent().getExtras().getString("TONTINE_ID");
        listView = (ListView) findViewById(R.id.listviewAnnonce);
        adapter = new AnnonceAdapter(this, annonces);
        listView.setAdapter(adapter);
        listView.setDividerHeight(0);
        adapter.notifyDataSetChanged();
        ScrollView emptyView = (ScrollView) findViewById(R.id.emptyList);
        listView.setEmptyView(emptyView);

        TextView textNoLiveView = (TextView) findViewById(R.id.textNoLive);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color,R.color.app_color);
        swipeLayout.setEnabled(true);

        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);

        } else {
            onRefresh();
        }
        listView.setDividerHeight(0);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listView.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  listView.getChildAt(totalItemCount-1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else {
                    reachedTop = false;
                    return;
                }
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Annonce annonce = annonces.get(position);
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //onRefresh();
        //swipeLayout.setRefreshing(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_annonce, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        if (NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);

        } else {
            ParseQuery<Tontine> tontineParseQuery = ParseQuery.getQuery(Tontine.class);
            tontineParseQuery.getInBackground(tontineId, new GetCallback<Tontine>() {
                @Override
                public void done(Tontine tontine, ParseException e) {
                    if(e==null){
                        ParseQuery<Annonce> annonceParseQuery = ParseQuery.getQuery(Annonce.class);
                        annonceParseQuery.whereEqualTo("tontine",tontine);
                        annonceParseQuery.findInBackground(new FindCallback<Annonce>() {
                            @Override
                            public void done(List<Annonce> annonceList, ParseException e) {
                                if(e==null){
                                    annonces = annonceList;
                                    listView.setAdapter(new AnnonceAdapter(context, annonces));
                                    if(annonces.size() == 0){
                                        AnnonceAdapter sa = new AnnonceAdapter(context, annonces);
                                        sa.clear();
                                        listView.setAdapter(sa);
                                        sa.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: "+listView.getAdapter().getCount());
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    Log.d("Annonces", "not found with error : " + e.getMessage());
                                    swipeLayout.setRefreshing(false);
                                }
                            }
                        });
                    }else{
                        Log.d("tontine", "Not found with error : " + e.getMessage());
                    }
                }
            });

        }
    }
}
