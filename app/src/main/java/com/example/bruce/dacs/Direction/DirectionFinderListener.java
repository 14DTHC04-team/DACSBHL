package com.example.bruce.dacs.Direction;

import java.util.List;
/**
 * Created by BRUCE on 5/7/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
