package com.cmput301f19t09.vibes.fragments.mapfragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cmput301f19t09.vibes.MainActivity;
import com.cmput301f19t09.vibes.R;
import com.cmput301f19t09.vibes.fragments.mooddetailsfragment.MoodDetailsDialogFragment;
import com.cmput301f19t09.vibes.models.EmotionalState;
import com.cmput301f19t09.vibes.models.MoodEvent;
import com.cmput301f19t09.vibes.models.User;
import com.cmput301f19t09.vibes.models.UserManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MapFragment extends Fragment implements OnMapReadyCallback, Observer,
        ClusterManager.OnClusterClickListener<MoodEvent>,
        ClusterManager.OnClusterInfoWindowClickListener<MoodEvent>,
        ClusterManager.OnClusterItemClickListener<MoodEvent>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MoodEvent> {

    GoogleMap googlemap;

    private ClusterManager<MoodEvent> mClusterManager;

    boolean firstPointPut = false;

    @Override
    public boolean onClusterClick(Cluster<MoodEvent> event) {
        Log.d("MAP", "cluster is clicked.");
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MoodEvent> event) {
        Log.d("MAP", "cluster info is clicked.");

    }

    @Override
    public boolean onClusterItemClick(MoodEvent event) {
        ((MainActivity)getActivity()).openDialogFragment(MoodDetailsDialogFragment.newInstance(event, filter == Filter.SHOW_MINE));
        Toast.makeText(getActivity(), "NOONONO",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(MoodEvent event) {
        Log.d("MAP", "clusterPoint info is clicked.");
    }

    /**
     * This is used to filter out the moods being showed;
     */
    public enum Filter{
        SHOW_MINE,
        SHOW_EVERYONE
    }

    Filter filter;
    private List<String> observedUsers;
    private Map<String, MoodEvent> displayedEvents;

    /**
     * The map fragment shows the locations of the moods.
     * It can show an interactive MoodEvent, helping the mood to be able to get edited or viewed
     * by the user.
     */
    public MapFragment() {
        // Required empty public constructor
        observedUsers = new ArrayList<>();
        displayedEvents = new HashMap<>();
    }

    public static MapFragment newInstance(){
        return new MapFragment();
    }

    /**
     * Checks for the bundle.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Displays the MoodEvent in the map.
     * @param event The MoodEvent that you're trying to show.
     */
    public String showMoodEvent(MoodEvent event){
        String id = "";
        if(googlemap != null){
//            MarkerOptions options = new MarkerOptions();
//            options.position(new LatLng(point.getLat(), point.getLong()));
//            if(point.getReason()!=null){
//                options.snippet(point.getReason());
//            }
//            if(point.getEmotion() != null){
//                options.title(point.getEmotion());
//            }

//            Integer emoticon = (Integer) EmotionalState.getMap().get(point.getEmotion()).first;
//            Integer color = (Integer)  EmotionalState.getMap().get(point.getEmotion()).second;
//            options.icon(bitmapDescriptorFromVector(getActivity(), emoticon, color));

//            id = googlemap.addMarker(options).getId();
            mClusterManager.addItem(event);

//            mClusterManager.getMarkerCollection().addMarker().getId();
//            mClusterManager.getClusterMarkerCollection().getMarkers();

            if(!firstPointPut ){
                firstPointPut = true;
                googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()), 4));
            }
        }
        return id;
    }

    /**
     * Making a callback function for when the map object is ready.
     * As the map is read,
     * The onMapReady function is called to go throught the given MoodEvent.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SupportMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);
        getChildFragmentManager().beginTransaction().add(R.id.filter_root, MapFilter.getInstance(Filter.SHOW_MINE), "mapFilter").commit();
        UserManager.addUserObserver(UserManager.getCurrentUserUID(), this);

        return view;
    }

    public GoogleMap getGooglemap(){
        return this.googlemap;
    }

    /**
     * This is a callback function. It is called when the map is ready.
     * @param mMap
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googlemap = mMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.clear();

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(getActivity(), googlemap);
        ClusterRenderer clusterRenderer = new CustomClusterRenderer(getActivity(), googlemap, mClusterManager);
        mClusterManager.setRenderer(clusterRenderer);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        googlemap.setOnCameraIdleListener(mClusterManager);
        googlemap.setOnMarkerClickListener(mClusterManager);
        googlemap.setOnInfoWindowClickListener(mClusterManager);

        // TODO: Take this back to be implementd
//        googlemap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                ((MainActivity)getActivity()).openDialogFragment(MoodDetailsDialogFragment.newInstance(displayedEvents.get(marker.getId()), filter == Filter.SHOW_MINE));
//                return true;
//            }
//        });
        switchFilter(Filter.SHOW_MINE);
    }

    /**
     * This is used to convert the drawable object into its bitmap descriptor.
     * It is used for showing the image of the icon.
     * @param context
     * @param vectorResId
     * @return
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId, Integer color) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        vectorDrawable.setBounds(0, 0, 64, 64);

        Bitmap bitmap2 = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = (Bitmap.createScaledBitmap(bitmap2, 64, 64, true));

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void switchFilter(Filter filter) {
        if (googlemap == null)
            return;

        this.filter = filter;
        User user = UserManager.getCurrentUser();
        Log.d("TEST/Map", "Showing own events");
        if (filter == Filter.SHOW_MINE) {
            removeObservers();
            showUserEvents();
        } else if (filter == Filter.SHOW_EVERYONE) {
            Log.d("TEST/Map", "Showing other events");
            for (String id : user.getFollowingList())
            {
                Log.d("TEST/Map", "Adding " + id);
                observedUsers.add(id);
                User followed_user = UserManager.getUser(id);
                if (followed_user.isLoaded()) {
                    if (followed_user.getMostRecentMoodEvent() != null) {
                        showEvent(followed_user.getMostRecentMoodEvent());
                    }
                }
                followed_user.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        showFollowedEvents();
                    }
                });
            }
        }
    }

    private void showFollowedEvents()
    {
        googlemap.clear();
        for (String id : observedUsers)
        {
            User user = UserManager.getUser(id);
            if (user.isLoaded() && user.getMostRecentMoodEvent() != null)
            {
                showEvent(user.getMostRecentMoodEvent());
            }
        }
    }

    private void showUserEvents()
    {
        googlemap.clear();
        displayedEvents.clear();
        for (MoodEvent event : UserManager.getCurrentUser().getMoodEvents()) {
            showEvent(event);
        }
    }

    private void showEvent(MoodEvent event)
    {
        String id = showMoodEvent(event);

        if (id != "")
        {
            displayedEvents.put(id, event);
        }
    }

    private void removeObservers()
    {
        for (String id : observedUsers)
        {
            UserManager.getUser(id).deleteObservers();
        }
    }

    @Override
    public void onPause() {
        removeObservers();
        UserManager.getCurrentUser().deleteObserver(this);
        super.onPause();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d("TEST/Map", "Current User Update");
        if (filter == Filter.SHOW_MINE)
        {
            showUserEvents();
        }
        else
        {
            for (String id : ((User)o).getFollowingList())
            {
                if (!observedUsers.contains(id))
                {
                    observedUsers.add(id);
                    User followed_user = UserManager.getUser(id);
                    if (followed_user.isLoaded()) {
                        if (followed_user.getMostRecentMoodEvent() != null) {
                            showEvent(followed_user.getMostRecentMoodEvent());
                        }
                    }
                    followed_user.addObserver(new Observer() {
                        @Override
                        public void update(Observable o, Object arg) {
                            showFollowedEvents();
                        }
                    });
                }
            }
            showFollowedEvents();
        }
    }
}