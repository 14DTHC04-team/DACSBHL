package com.example.bruce.dacs.BigMap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bruce.dacs.R;

public class LocationList_Fragment extends Fragment implements MenumapAdapter.RecyclerViewClicklistener {


    View mView;
    public LocationList_Fragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = (View) inflater.inflate(R.layout.activity_menu__map,container,false);

        return mView;
    }

    @Override
    public void itemClick(View view, int position) {

    }
}
