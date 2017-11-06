package com.example.bruce.dacs.BigMap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bruce.dacs.R;
import com.example.bruce.dacs.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchLocation extends AppCompatActivity implements SearchLocationAdapter.RecyclerViewClicklistener{
    RecyclerView recyclerViewSearch;
    ArrayList<Tourist_Location> listSearch;
    SearchLocationAdapter adapter;
    Tourist_Location tourist_location;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        recyclerViewSearch= (RecyclerView) findViewById(R.id.recyclerView_seacrh_location);
        searchView= (SearchView) findViewById(R.id.SearchView);
        listSearch=new ArrayList<>();
        adapter=new SearchLocationAdapter(listSearch,SearchLocation.this);
        recyclerViewSearch.setHasFixedSize(true);
        adapter.setClickListener(SearchLocation.this);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(SearchLocation.this));
        recyclerViewSearch.setAdapter(adapter);


        searchView.setQuery(getIntent().getStringExtra("Keyword"),true);

        searchView.setIconifiedByDefault(false);
        searchView.setClickable(false);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GetDataLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText.toString())){
                   finish();
                }
                return false;
            }
        });

    };

    @Override
    public void itemClick(View view, int position) {

    }
    void GetDataLocation(final String txtName)
    {
        listSearch.clear();
        final RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Server.url_SearchName+txtName, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null)
                {
                    for (int i=0;i<response.length();i++)
                    {
                        try {
                            tourist_location = new Tourist_Location();
                            JSONObject jsonObject = response.getJSONObject(i);
                            tourist_location.location_ID = jsonObject.getInt("id");
                            tourist_location.LocationName=jsonObject.getString("ten");
                            tourist_location.Address=jsonObject.getString("diachi");
                            tourist_location.LocationImg=jsonObject.getString("img");
                            tourist_location.province_ID=jsonObject.getInt("matinh");
                            listSearch.add(tourist_location);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
