package io.github.yahia_hassan.popularmoviesstage2;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int prefCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < prefCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            String key = preference.getKey();
            String value = sharedPreferences.getString(key, getString(R.string.pref_sort_popular));
            setPreferenceSummary(preference, value);

        }
    }

    private void setPreferenceSummary (Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String value = sharedPreferences.getString(key, getString(R.string.pref_sort_popular));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();

        Preference preference = findPreference(key);
        setPreferenceSummary(preference, value);
        CacheJSON.mMovieJSON = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
