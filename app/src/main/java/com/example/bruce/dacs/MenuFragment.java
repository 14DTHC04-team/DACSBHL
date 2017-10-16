package com.example.bruce.dacs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bruce.dacs.BigMap.BigMap;
import com.example.bruce.dacs.LoginActivity;
import com.example.bruce.dacs.R;
import com.example.bruce.dacs.Users.User_Profile;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MenuFragment extends Fragment {

    ViewGroup mView;
    ListView listView;
    public void MenuFragmen() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = (ViewGroup) inflater.inflate(R.layout.activity_menu_fragment,container,false);

        String[] menuItem = {"My profile",
                            "Zoom Map",
                            "Log out",
                            "Help"};
        listView = (ListView)mView.findViewById(R.id.Menu);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(),
                                                                        android.R.layout.simple_list_item_1,
                                                                        menuItem);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) listView.getItemAtPosition(position);
                if(value == "Log out")
                {
                    Profile profile = Profile.getCurrentProfile();
                    if(profile != null) {

                        LoginManager.getInstance().logOut();
                        getActivity().finish();
                        Intent target = new Intent(getActivity(), LoginActivity.class);
                        startActivity(target);
                    }
                    else
                    {
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                        Intent target = new Intent(getActivity(), LoginActivity.class);
                        startActivity(target);
                    }
                }
                if(value == "Zoom Map"){
                    Intent target = new Intent(getActivity(), BigMap.class);
                    startActivity(target);
                }
                if(value == "My profile"){
                    getActivity().finish();
                    Intent target = new Intent(getActivity(), User_Profile.class);
                    startActivity(target);
                }
            }
        });
        return mView;
    }
}
