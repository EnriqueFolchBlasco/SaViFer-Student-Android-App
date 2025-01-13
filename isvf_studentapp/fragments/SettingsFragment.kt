package es.efb.isvf_studentapp.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.efb.isvf_studentapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }

}
