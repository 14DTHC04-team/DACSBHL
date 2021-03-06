package com.example.bruce.dacs.BigMap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bruce.dacs.Direction.DirectionFinder;
import com.example.bruce.dacs.Direction.DirectionFinderListener;
import com.example.bruce.dacs.Direction.Route;
import com.example.bruce.dacs.GPSTracker;
import com.example.bruce.dacs.MoreInfo.InfoActivity;
import com.example.bruce.dacs.R;
import com.example.bruce.dacs.Server;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.example.bruce.dacs.R.id.map;

public class BigMap extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, MenumapAdapter.RecyclerViewClicklistener {
    private GoogleMap mMap;
    private GoogleMap myMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Marker> locationUser=new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String origin;
    String destination;
    LatLng myLocation;



    MenumapAdapter adapter;


    TextView txtToogle;
    Button btnOK;
    EditText edit;

    SearchView searchView;
    DrawerLayout Drawer;


    int r = 0;
    boolean count = true;

    Double latitude;
    Double longtitude;


    RecyclerView recyclerView;

    private DatabaseReference LatLngUser;

    GeoFire geoFire;
    GeoQuery geoQuery;

    boolean area=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        init();

        //toogle
        txtToogle = (TextView) findViewById(R.id.txtToogle);
        txtToogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawer.openDrawer(Gravity.START);
            }
        });
        Drawer = (DrawerLayout) findViewById(R.id.drawer_map);

        //Editext chuyen searchView
        searchView = (SearchView) findViewById(R.id.searchInten);


        //recyclerview
        listTourist = new ArrayList<>();
        adapter = new MenumapAdapter(listTourist, BigMap.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_BigMap);

        recyclerView.setHasFixedSize(true);
        adapter.setClickListener(BigMap.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(BigMap.this));
        recyclerView.setAdapter(adapter);


        Search();
        LatLngUser = FirebaseDatabase.getInstance().getReference("LatLngUser");
        geoFire = new GeoFire(LatLngUser);
    }

    private void Search() {
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Nhập địa điểm....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.toString())){
                    Intent intent=new Intent(BigMap.this,SearchLocation.class);
                    intent.putExtra("Keyword",newText);
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    public void init() {
        btnOK = (Button) findViewById(R.id.btnSearch);
        edit = (EditText) findViewById(R.id.editText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap=googleMap;
        mMap = googleMap;

        destination = "";

        final GPSTracker gps = new GPSTracker(this);

        myMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {



                origin = location.getLatitude() + ", " + location.getLongitude();
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title("Some where"));

                if (count==true)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,14));
                    count=false;
                }

                    FirebaseDatabase.getInstance().getReference("LatLngUser").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                            for (Marker maker :locationUser)
                                maker.remove();
                            geoFire.getLocation(String.valueOf(dataSnapshot.getKey()), new LocationCallback() {
                                @Override
                                public void onLocationResult(String key, GeoLocation location) {
                                    if (location != null)
                                    {
                                        if(dataSnapshot.getKey().toString()!=FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                                            locationUser.add(myMap.addMarker(new MarkerOptions()
                                                    .title("Firend")
                                                    .position(new LatLng(location.latitude, location.longitude))
                                                    .flat(true)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()));
                    geoFire.getLocation("06Sl7Z8ZmdQjG5UZiBqh6V6YCRp2", new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if (location != null) {
                                myMap.addCircle(new CircleOptions()
                                        .center(new LatLng(location.latitude,location.longitude))
                                        .radius(500) //tinh theo met'
                                        .strokeColor(Color.RED)
                                        .fillColor(0x44ff0000));
                                geoQuery=geoFire.queryAtLocation(new GeoLocation(location.latitude,location.longitude),0.5f);
                                geoQuery.removeAllListeners();
                                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                    @Override
                                    public void onKeyEntered(String key, final GeoLocation location) {

                                            if (FirebaseAuth.getInstance().getCurrentUser().getUid() == key) {
                                                if (area == true) {
                                                    sendNotification("HiwhereAmI",FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+": Bạn Đã Vào Khu Vực Của Team");
                                                    area=false;
                                                }
                                            }
                                    }
                                        @Override
                                        public void onKeyExited(String key) {
                                            if (FirebaseAuth.getInstance().getCurrentUser().getUid() == key) {
                                                if (area == false) {
                                                    sendNotification("HiwhereAmI",FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+": Bạn Đã Đi Khỏi Khu Vực Team");
                                                    area=true;
                                                }
                                            }
                                        }
                                    @Override
                                    public void onKeyMoved(String key, GeoLocation location) {
                                        Log.d("asd","MOve"+key + " " + location.latitude + "  " + location.longitude);
                                    }
                                    @Override
                                    public void onGeoQueryReady() {
                                        Log.d("asd","hoanthanh");
                                    }
                                    @Override
                                    public void onGeoQueryError(DatabaseError error) {

                                    }
                                });
                            } else {
                                System.out.println(String.format("There is no location for key %s in GeoFire", key));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.err.println("There was an error getting the GeoFire location: " + databaseError);
                        }
                    });



            }
        });

        myLocation = new LatLng(gps.getLatitude(), gps.getLongtitude());
        latitude = myLocation.latitude;
        longtitude = myLocation.longitude;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

//--------------------------------------------------------------------------------------------------------------------------------
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                final Dialog info = new Dialog(BigMap.this);
                info.setContentView(R.layout.alertdialog_bigmap);
                info.setTitle("Choose what you want !");
                info.show();
                Button btn= (Button) info.findViewById(R.id.aa);
                Button btn2= (Button) info.findViewById(R.id.ss);


                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        AlertDialog.Builder EnableGPSRequest = new AlertDialog.Builder(BigMap.this);
                        EnableGPSRequest.setTitle("GPS is setting");
                        EnableGPSRequest.setMessage("Please enable GPS of this device to use this feature! ");
                        if (isGPSEnable != true) {
                            EnableGPSRequest.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            });

                            EnableGPSRequest.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            EnableGPSRequest.show();
                        }
                        else {

                            destination = marker.getPosition().latitude + ", " + marker.getPosition().longitude;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 16));
                            sendRequest(origin,destination);
                            info.dismiss();
                        }
                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        info.dismiss();
                        Intent inforr = new Intent(BigMap.this, InfoActivity.class);
                        inforr.setAction(Intent.ACTION_GET_CONTENT);
                        int id = 0; String basicInfo = "";
                        for(Tourist_Location tl : listTourist){
                            if(marker.getPosition().latitude == tl.Latitude && marker.getPosition().longitude == tl.Longtitude){
                                id = tl.location_ID;
                                basicInfo = tl.BasicInfo;
                            }
                        }
                        inforr.putExtra("locationname", marker.getTitle());
                        inforr.putExtra("basicInfo", basicInfo);
                        inforr.putExtra("location_id", id);
                        startActivity(inforr);
                    }
                });
            }
        });

//-------------------------------------set adapter cho infoWindow------------------------------------------------------------
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.infowindow_layout,null);

                TextView txttitle = (TextView) view.findViewById(R.id.txtTitle);
                TextView txtsnippet = (TextView) view.findViewById(R.id.txtSnippet);
                ImageView imgLocation = (ImageView) view.findViewById(R.id.imgInfoWindow);
                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarInfoWindow);

                txttitle.setText(marker.getTitle());
                txtsnippet.setText(marker.getSnippet());
                for(Tourist_Location t : listTourist){
                    if(t.LocationName.equals(marker.getTitle())){
                        ratingBar.setRating(t.star);
                        Picasso.with(BigMap.this).load(t.LocationImg).into(imgLocation);
                    }
                }
                return view;
            }
        });
//------------------------------------------------------------------------------------------------------------------------------
        //lay destination khi click vao marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

//----------------------------------------------------tự động tìm đường khi click lâu trên GGmap------------------------------------------

        OnMapClickListener(mMap,gps);

//--------------------------------------------------------- nut OK tim ban' kinh -----------------------------------------------------

        SubmitRadius(mMap,myLocation,btnOK,edit,adapter,listTourist,latitude,longtitude,gps);

//--------------------------------------------------tim dia diem-------------------------------------------------------------------------------------



//------------------------------------------------------------------------------------------------------------------------------------

        Firebase_Tourist_Location(latitude, longtitude, r);

    }

    private void sendNotification(String title, String content) {
        Notification.Builder builder=new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);
        NotificationManager manager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(this,BigMap.class);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);
        Notification notification =builder.build();
        notification.flags |=Notification.FLAG_AUTO_CANCEL;
        notification.defaults|=Notification.DEFAULT_SOUND;
        manager.notify(new Random().nextInt(),notification);


    }

    private void OnMapClickListener(final GoogleMap mMap, final GPSTracker gps){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            Marker onTouch;
            String destination = "";
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(onTouch != null){
                    onTouch.remove();
                }
                onTouch = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker was chosen").snippet(gps.Address(latLng.latitude,latLng.longitude)));
                destination = latLng.latitude + ", " + latLng.longitude;

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    int countClick = 0;
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        countClick++;
                        if(countClick%2 == 0) {
                            onTouch.remove();
                            destination = gps.getLatitude() + ", " + gps.getLongtitude();
                            sendRequest(origin,destination);
                        }
                        return false;
                    }
                });
                sendRequest(origin,destination);
            }
        });
    }

    private void SubmitRadius(final GoogleMap mMap, final LatLng myLocation, final Button btnOK, final EditText edit, final MenumapAdapter adapter, final ArrayList<Tourist_Location> listTourist,
                                final double latitude, final double longtitude,final GPSTracker gps){

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                AlertDialog.Builder EnableGPSRequest = new AlertDialog.Builder(BigMap.this);
                EnableGPSRequest.setTitle("GPS is setting");
                EnableGPSRequest.setMessage("Please enable GPS of this device to use this feature! ");
                if (isGPSEnable != true) {
                    EnableGPSRequest.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });

                    EnableGPSRequest.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    EnableGPSRequest.show();
                }
                else{
                    //Drawer.closeDrawer(Gravity.START);
                    if (edit.length() == 0) {
                        mMap.clear();
                        listTourist.clear();
                        adapter.notifyDataSetChanged();
                        Firebase_Tourist_Location(latitude, longtitude, 0);

                    } else {
                        mMap.clear();
                        listTourist.clear();
                        adapter.notifyDataSetChanged();

                        double value1 = 0;
                        value1 = Double.parseDouble(edit.getText().toString());

                        mMap.addCircle(new CircleOptions()
                                .center(myLocation)
                                .radius(value1) //tinh theo met'
                                .strokeColor(Color.RED)
                                .fillColor(0x44ff0000));


                        Firebase_Tourist_Location(latitude, longtitude, value1);
                    }
                }
            }
        });
    }

    public void getRatingStar(final ArrayList<Tourist_Location> listTourist){
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.url_RatingStar, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response != null){
                    for (int i = 0;i < response.length(); i++){
                        try{
                            JSONObject jsonObject = response.getJSONObject(i);
                            for(Tourist_Location tl : listTourist)
                                if(jsonObject.getInt("location_ID") == tl.location_ID) {
                                    tl.star = Float.parseFloat(jsonObject.getString("Star"));
                                    break;
                                }
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    //Marker myMarker;
    Tourist_Location tourist_location;
    ArrayList<Tourist_Location> listTourist;
    public void Firebase_Tourist_Location(final double myLat,final double myLng, final double R_Circle) {
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Server.url_TouristLocation, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            tourist_location = new Tourist_Location();
                            JSONObject jsonObject = response.getJSONObject(i);
                            tourist_location.location_ID = jsonObject.getInt("id");
                            tourist_location.LocationName=jsonObject.getString("ten");
                            tourist_location.Address=jsonObject.getString("diachi");
                            tourist_location.Longtitude=jsonObject.getDouble("log");
                            tourist_location.Latitude=jsonObject.getDouble("lat");
                            tourist_location.LocationImg=jsonObject.getString("img");
                            tourist_location.BasicInfo=jsonObject.getString("thongtincoban");
                            tourist_location.province_ID=jsonObject.getInt("matinh");

                            if(R_Circle == 0)
                            {
                                double distance = Radius(myLat, myLng, tourist_location.Latitude, tourist_location.Longtitude);
                                tourist_location.Distance = distance;
                                listTourist.add(tourist_location);
                                getRatingStar(listTourist);
                                final  LatLng LatLgData = new LatLng(tourist_location.Latitude,tourist_location.Longtitude);
                                mMap.addMarker(new MarkerOptions().position(LatLgData).title(tourist_location.LocationName).snippet(tourist_location.Address).icon(null));

//                PicassoMarker marker = new PicassoMarker(myMarker);
//                Picasso.with(BigMap.this).load(tourist_location.LocationImg).resize(150,150).centerCrop().into(marker);

//------------------------------------xap sep ListView sao cho hien dia diem du lich gan nguoi dung nhat dau tien------------------------------------------------------------
                                Collections.sort(listTourist, new Comparator<Tourist_Location>(){
                                    public int compare(Tourist_Location obj1, Tourist_Location obj2) {
                                        // ## Ascending order
                                        //return (obj1.Distance).compareToIgnoreCase(obj2.Distance); // To compare string values
                                        return Double.valueOf(obj1.Distance).compareTo(Double.valueOf(obj2.Distance)); // To compare integer values

                                        // ## Descending order
                                        //return obj2.Distance.compareToIgnoreCase(obj1.Distance); // To compare string values
                                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                    }
                                });
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                double distance = Radius(myLat, myLng, tourist_location.Latitude, tourist_location.Longtitude);

                                if (distance <= R_Circle) {

                                    tourist_location.Distance = distance;
                                    listTourist.add(tourist_location);
                                    getRatingStar(listTourist);
                                    final LatLng LatLgData = new LatLng(tourist_location.Latitude, tourist_location.Longtitude);
                                    mMap.addMarker(new MarkerOptions().position(LatLgData).title(tourist_location.LocationName).snippet(tourist_location.Address).icon(null));

//                PicassoMarker marker = new PicassoMarker(myMarker);
//                Picasso.with(BigMap.this).load(tourist_location.LocationImg).resize(150,150).centerCrop().into(marker);

//------------------------------------xap sep ListView sao cho hien dia diem du lich gan nguoi dung nhat dau tien------------------------------------------------------------

                                    Collections.sort(listTourist, new Comparator<Tourist_Location>() {
                                        public int compare(Tourist_Location obj1, Tourist_Location obj2) {
                                            // ## Ascending order
                                            //return (obj1.Distance).compareToIgnoreCase(obj2.Distance); // To compare string values
                                            return Double.valueOf(obj1.Distance).compareTo(Double.valueOf(obj2.Distance)); // To compare integer values
                                            // ## Descending order
                                            //return obj2.Distance.compareToIgnoreCase(obj1.Distance); // To compare string values
                                            // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                        }
                                    });
//---------------------------------------------------------------------------------------------------------------------------------------------------
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BigMap.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }


    public double Radius(double myLat, double myLng, double lat, double lng)
    {
        double distance;
        double a = lat - myLat;
        double b = lng - myLng;
        double c = Math.sqrt(a*a + b*b);
        distance = c*(111.2)*1000;
        return distance;
    }

    @Override
    public void itemClick(View view, final int position) {

        Tourist_Location cM = this.listTourist.get(position);
        final String locationName = cM.LocationName;
        final int location_id = cM.location_ID;
        final String basicInfo = cM.BasicInfo;
        final Dialog info = new Dialog(BigMap.this);

        //info.requestWindowFeature(Window.FEATURE_NO_TITLE); -- bo title cua dialog
        info.setContentView(R.layout.alertdialog_bigmap);
        info.setTitle("Choose what you want !");
        info.show();
        final Button btn= (Button) info.findViewById(R.id.aa);
        final Button btn2= (Button) info.findViewById(R.id.ss);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                AlertDialog.Builder EnableGPSRequest = new AlertDialog.Builder(BigMap.this);
                EnableGPSRequest.setTitle("GPS is setting");
                EnableGPSRequest.setMessage("Please enable GPS of this device to use this feature! ");
                if (isGPSEnable != true) {
                    EnableGPSRequest.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    EnableGPSRequest.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    EnableGPSRequest.show();
                }
                else {
                    for (Tourist_Location tl : listTourist) {
                        if (tl.LocationName == locationName) {
                            destination = tl.Latitude + ", " + tl.Longtitude;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng (tl.Latitude, tl.Longtitude), 16));
                            sendRequest(origin,destination);
                        }
                    }
                    info.dismiss();
                    Drawer.closeDrawer(Gravity.START);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.dismiss();
                Intent infor = new Intent(BigMap.this, InfoActivity.class);
                infor.putExtra("locationname", locationName);
                infor.putExtra("location_id", location_id);
                infor.putExtra("basicInfo", basicInfo);
                startActivity(infor);
            }
        });
    }
    private boolean FindDirectionInList(String nameSearch,ArrayList<Tourist_Location> tls){
        boolean check = false;
        for(Tourist_Location tl: tls){
            if(tl.LocationName.contains(nameSearch)){
                check = true;
                destination = tl.Latitude + ", " + tl.Longtitude;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tl.Latitude, tl.Longtitude), 16));
                sendRequest(origin,destination);
            }
        }
        return check;
    }


    //ham tim duong
    private void sendRequest(String origin,String destination) {
        if (origin == "") {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination == "") {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (Route route : routes) {
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            Toast.makeText(BigMap.this,"Distance: "+route.distance.text+"\n"+"Duration: "+route.duration.text,Toast.LENGTH_LONG).show();



            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                   // .icon(BitmapDescriptorFactory.fromResource(R.drawable.imagemenu))
//                    .title(route.startAddress)
//                    .position(myLocation)));
//            LatLng LatLgData = new LatLng(Double.parseDouble(tourist_location.Latitude),Double.parseDouble(tourist_location.Longtitude));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.imagemenu))
//                    .title(route.endAddress)
//                    .position(LatLgData)));


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.GRAY).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
            if(!TextUtils.isEmpty(searchView.getQuery().toString())){
                searchView.setQuery("",false);
                searchView.requestFocus();
                searchView.clearFocus();

            }
    }

  }
//class PicassoMarker implements Target {
//    Marker mMarker;
//
//    PicassoMarker(Marker marker) {
//        mMarker = marker;
//    }
//
//    @Override
//    public int hashCode() {
//        return mMarker.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if(o instanceof PicassoMarker) {
//            Marker marker = ((PicassoMarker) o).mMarker;
//            return mMarker.equals(marker);
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//        mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//    }
//
//    @Override
//    public void onBitmapFailed(Drawable errorDrawable) {
//    }
//
//    @Override
//    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//    }
//}
