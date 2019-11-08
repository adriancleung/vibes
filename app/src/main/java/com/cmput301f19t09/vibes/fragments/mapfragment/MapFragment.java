package com.cmput301f19t09.vibes.fragments.mapfragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cmput301f19t09.vibes.R;
import com.cmput301f19t09.vibes.models.EmotionalState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapData data;
    GoogleMap googlemap;

    boolean firstPointPut = false;
    /**
     * This is used to filter out the moods being showed;
     */
    public enum Filter{
        SHOW_MINE,
        SHOW_EVERYONE
    }

    /**
     * An example for calling this MapFragment can be found below.
     * FragmentManager fragmentManager = getSupportFragmentManager();
     *         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
     *
     *         MapFragment fragment = new MapFragment();
     *         Bundle bundle = new Bundle();
     *         MapData showingUsers = new MapData();
     *         showingUsers.add(UserPoint.getMockUser());
     *
     *         bundle.putSerializable("MapData", showingUsers);
     *         fragment.setArguments(bundle);
     *
     *         fragmentTransaction.add(R.id.map_container, fragment);
     *         fragmentTransaction.commit();
     */
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment getInstance(){
        return new MapFragment();
    }

    /**
     * Checks for the bundle MapData.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            data = (MapData) bundle.getSerializable("MapData");
            Log.d("MAPFRAGMENT: ", "" + data.size());
        }
    }

    /**
     * Displays the UserPoint in the map.
     * @param point
     */
    public void showUserPoint(UserPoint point){
        if(googlemap != null){

            MarkerOptions options = new MarkerOptions();
            options.position(point.getLocation());
            if(point.getReason()!=null){
                options.snippet(point.getReason());
            }
            if(point.getEmotion() != null){
                options.title(point.getEmotion());
            }

            Integer emoticon = (Integer) EmotionalState.getMap().get(point.getEmotion()).first;
            Integer color = (Integer)  EmotionalState.getMap().get(point.getEmotion()).second;
            options.icon(bitmapDescriptorFromVector(getActivity(), emoticon, color));
//            switch(point.getEmotion()) {
//                case "HAPPINESS":
//                    options.icon(bitmapDescriptorFromVector(getActivity(), R.drawable.emotion_image_happiness));
//                    break;
//            }
            googlemap.addMarker(options);

            if(!firstPointPut ){
                firstPointPut = true;
                CameraPosition googlePlex = CameraPosition.builder()
                .target(point.getLocation())
                .zoom(3)
                .bearing(0)
                .tilt(45)
                .build();

                googlemap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

            }
        }
    }

    public Drawable scaleImage (Drawable image) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 32, 32, true);

        image = new BitmapDrawable(getResources(), bitmapResized);

        return image;

    }

    /**
     * Making a callback function for when the map object is ready.
     * As the map is read,
     * The onMapReady function is called to go throught the given UserPoints.
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
        return view;
    }

    public GoogleMap getGooglemap(){
        return this.googlemap;
    }


    /**
     * Showing a user's mood
     * @param username
     * @return
     */
    public UserPoint showMoodOf(String username){
        return UserPoint.getMockUser();
    }

    /**
     * This is a callback function. It is called when the map is ready.
     * It goes throught the data MapData, and and calls showUserPoint(UserPoint) for each item in it.
     * @param mMap
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googlemap = mMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear();
            if(this.data != null){
                    for (UserPoint p:this.data) {
                        this.showUserPoint(p);
                    }
        }
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
        vectorDrawable.setBounds(0, 0, 64, 64);

        Bitmap bitmap2 = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = (Bitmap.createScaledBitmap(bitmap2, 64, 64, true));

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}