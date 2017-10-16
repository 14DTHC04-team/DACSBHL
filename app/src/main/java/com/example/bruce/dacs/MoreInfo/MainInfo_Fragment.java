package com.example.bruce.dacs.MoreInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by BRUCE on 8/30/2017.
 */

public class MainInfo_Fragment extends android.support.v4.app.Fragment {

    View mView;


    ListView listView ;
    ArrayList<MoreInfo> moreInfoList;
    InfoAdapter moreInfoAdapter;
    TextView txtBI;

    int location_ID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.maininfo_fragment,container,false);


        listView= (ListView) mView.findViewById(R.id.infoListView);


        txtBI = (TextView) mView.findViewById(R.id.txtBasicInfo);
        txtBI.setText(getActivity().getIntent().getStringExtra("basicInfo"));



        moreInfoList = new ArrayList<MoreInfo>();
        moreInfoAdapter = new InfoAdapter(getActivity(), R.layout.info_adapter, moreInfoList);
        listView.setAdapter(moreInfoAdapter);


        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-------------------------------------------------------------------------------------------------------------------------------------

        Firebase_Description();
    }
    public void Firebase_Description(){

        location_ID = getActivity().getIntent().getIntExtra("location_id",0);   //intent BigMapF
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Server.url_MoreInfomation+location_ID, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response !=null)
                {
                    for(int i=0;i<response.length();i++)
                    {
                        try {
                            JSONObject jsonObject=response.getJSONObject(i);
                            MoreInfo moreInfo = new MoreInfo();
                            moreInfo.infomation_ID=jsonObject.getInt("id");
                            moreInfo.location_id=jsonObject.getInt("madiadiemdulich");
                            moreInfo.Info=jsonObject.getString("thongtin");
                            moreInfo.Image=jsonObject.getString("img");

                            moreInfoList.add(moreInfo);
                            moreInfoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "k?t n?i th?t b?i", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}
