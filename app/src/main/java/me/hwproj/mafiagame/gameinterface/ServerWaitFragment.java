package me.hwproj.mafiagame.gameinterface;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import me.hwproj.mafiagame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerWaitFragment extends Fragment {


    public ServerWaitFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_wait, container, false);
    }

}
