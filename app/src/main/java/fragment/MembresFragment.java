package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.app.R;
import com.parse.app.adapter.MembreAdapter;
import com.parse.app.model.Tontine;
import com.parse.app.model.Presence;
import com.parse.app.model.Session;
import com.parse.app.utilities.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class MembresFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
	private List<Presence> mItems;
    private ListView listview;
    private MembreAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private boolean reachedTop = true;
    private String sessionId;
    private Tontine tontine;
    private Session tontineSession;
    private ParseUser user;

    public MembresFragment(String sessionId){

        this.sessionId = sessionId;
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
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        if(NetworkUtil.getConnectivityStatus(getActivity().getApplicationContext())==0) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
        }else {
            //Toast.makeText(getActivity().getApplicationContext(),"tontine id = " + tontineId + "session id = " + sessionId, Toast.LENGTH_LONG).show();
            tontine = new Tontine();
            user = ParseUser.getCurrentUser();
            mItems = new ArrayList<Presence>();

            ParseQuery<Session> sessionParseQuery = ParseQuery.getQuery(Session.class);
            sessionParseQuery.getInBackground(sessionId, new GetCallback<Session>() {
                @Override
                public void done(Session session, ParseException e) {
                    if (e == null) {
                        ParseQuery<Presence> presenceParseQuery = ParseQuery.getQuery(Presence.class);
                        presenceParseQuery.whereEqualTo("statu", "present");
                        presenceParseQuery.whereEqualTo("session", session);
                        presenceParseQuery.whereEqualTo("date", session.getDateCreation());
                        presenceParseQuery.findInBackground(new FindCallback<Presence>() {
                            @Override
                            public void done(List<Presence> presences, ParseException e) {
                                if(e == null) {
                                    /*for (Presence presence : presences) {
                                        mItems.add(presence.getMembre());
                                    }*/
                                    mItems = presences;
                                    listview.setAdapter(new MembreAdapter(getActivity().getApplicationContext(), presences));
                                    if (mItems.size()==0) {
                                        MembreAdapter ma = new MembreAdapter(getActivity().getApplicationContext(), presences);
                                        ma.clear();
                                        listview.setAdapter(ma);
                                        ma.notifyDataSetChanged();
                                        System.out.println("Nombre d'element dans l'adapter: " + listview.getAdapter().getCount());
                                    }
                                    swipeLayout.setRefreshing(false);
                                }else{
                                    swipeLayout.setRefreshing(false);
                                   Log.d("presences","error " + e);
                                }
                            }
                        });

                    } else {
                        swipeLayout.setRefreshing(false);
                        Log.d("Session", "Not found error : " + e);
                    }
                }
            });
            }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_membres,container, false);
        listview = (ListView) rootView.findViewById(R.id.listviewMembre);
        mItems = new ArrayList<Presence>();
        adapter = new MembreAdapter(getActivity().getApplicationContext(), mItems);
        listview.setAdapter(adapter);
        listview.setDividerHeight(0);
        ScrollView emptyView = (ScrollView) rootView.findViewById(R.id.emptyList);
        listview.setEmptyView(emptyView);
        TextView textNoLiveView = (TextView) rootView.findViewById(R.id.textNoLive);
        listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_broadcast);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(R.color.app_color, R.color.app_color, R.color.app_color);
        swipeLayout.setEnabled(true);
        onRefresh();
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = listview.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:
                        reachedTop = true;
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem){
                    View v =  listview.getChildAt(totalItemCount-1);
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

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        //Toast.makeText(getActivity().getApplicationContext(), "FLING !", Toast.LENGTH_SHORT).show();
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                                swipeLayout.setEnabled(false);
                            } else if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
                                //Toast.makeText(getActivity().getApplicationContext(), "Up to Down !", Toast.LENGTH_LONG).show();
                                swipeLayout.setEnabled(true);
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        listview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gesture.onTouchEvent(event);
            }

        });
        

        return rootView;
    }

}
