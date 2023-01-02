package com.example.pruebarecviewbasedatos.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import com.example.pruebarecviewbasedatos.R;

public class FragmentPreferences extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
