package org.dcxz.designdigger.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dcxz.designdigger.view.flowing_drawer.MenuFragment;

import org.dcxz.designdigger.R;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class Fragment_Menu extends MenuFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setupReveal(inflater.inflate(R.layout.fragment_menu, container, false));
    }
}
