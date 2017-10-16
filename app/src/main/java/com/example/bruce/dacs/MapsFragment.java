package com.example.bruce.dacs;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by BRUCE on 4/26/2017.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    View mView;
    int count = 0;
    Marker currentMarker;
    public MapsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.maps_fragment, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragment = getActivity().getFragmentManager();
        final MapFragment mf = (MapFragment) fragment.findFragmentById(R.id.map);

        mf.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                count++;
                LatLng MyLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title("Some where"));
                // Thêm Marker cho Map:
                MarkerOptions option = new MarkerOptions();
                option.title("You're here !!!");
                option.position(MyLocation);
                //chi zoom map khi lan dau moi zo activity nay`
                if(count <= 1) {

                    currentMarker = mMap.addMarker(option);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 14));
                    currentMarker.showInfoWindow();
                }
                else{
                    currentMarker.remove();
                    currentMarker = mMap.addMarker(option);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

}
