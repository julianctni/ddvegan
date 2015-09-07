package com.pasta.ddvegan.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;

public class HelpFragment extends DialogFragment {



    public HelpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container);
        Button b = (Button) view.findViewById(R.id.finishTutorial);


        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putInt(DataRepo.APP_VERSION_KEY, DataRepo.appVersionCode).apply();
                getDialog().dismiss();
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
    }

}

