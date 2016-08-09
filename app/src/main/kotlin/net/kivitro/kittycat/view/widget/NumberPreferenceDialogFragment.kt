package net.kivitro.kittycat.view.widget

import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.NumberPicker
import net.kivitro.kittycat.R
import timber.log.Timber

/**
 * Created by Max on 06.08.2016.
 */
class NumberPreferenceDialogFragment : PreferenceDialogFragmentCompat() {
    private lateinit var numberPicker: NumberPicker
    private lateinit var numberPreference: NumberPickerPreference

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        numberPicker = view.findViewById(R.id.edit) as NumberPicker
        numberPreference = preference as NumberPickerPreference

        numberPicker.minValue = 0
        numberPicker.maxValue = 100
        numberPicker.value = numberPreference.number

        Timber.d("onBindDialogView ${numberPreference.number}")
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        val selectedValue = numberPicker.value
        Timber.d("onDialogClosed $positiveResult $selectedValue")
        if (positiveResult && numberPreference.callChangeListener(selectedValue)) {
            Timber.d("onDialogClosed store")
            numberPreference.number = selectedValue
            numberPreference.summary = numberPreference.summary // trigger summery update
        }
    }

    companion object {
        fun newInstance(key: String): NumberPreferenceDialogFragment {
            Timber.w("newInstance for $key")
            val f = NumberPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            f.arguments = b
            return f
        }
    }

}
