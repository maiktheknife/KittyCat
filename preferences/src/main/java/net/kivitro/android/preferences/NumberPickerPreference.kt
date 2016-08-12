package net.kivitro.android.preferences

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import timber.log.Timber

/**
 * Created by Max on 06.08.2016.
 */
class NumberPickerPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    var minValue = 10
    var maxValue = 100
    var value = 0
        get
        set(value) {
            field = value
            persistInt(value)
        }

    init {
        // Do custom stuff here, read attributes etc.
        isPersistent = true
        dialogIcon = null
        dialogLayoutResource = R.layout.pref_dialog_time

        minValue = attrs?.getAttributeIntValue(ns, "minValue", minValue) ?: minValue
        maxValue = attrs?.getAttributeIntValue(ns, "maxValue", maxValue) ?: maxValue

        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.preferenceStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Timber.d("onGetDefaultValue")
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        Timber.d("onSetInitialValue $restorePersistedValue $defaultValue")
        value = if (restorePersistedValue) getPersistedInt(value) else defaultValue as Int
    }

    override fun getSummary(): CharSequence {
        return getPersistedInt(value).toString()
    }

    class NumberPickerPreferenceDialogFragment : PreferenceDialogFragmentCompat() {
        private lateinit var numberPicker: NumberPicker
        private lateinit var numberPreference: NumberPickerPreference

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            numberPicker = view.findViewById(R.id.edit) as NumberPicker
            numberPreference = preference as NumberPickerPreference

            numberPicker.minValue = numberPreference.minValue
            numberPicker.maxValue = numberPreference.maxValue
            numberPicker.value = numberPreference.value

            Timber.d("onBindDialogView ${numberPreference.value}")
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            val selectedValue = numberPicker.value
            Timber.d("onDialogClosed $positiveResult $selectedValue")
            if (positiveResult && numberPreference.callChangeListener(selectedValue)) {
                numberPreference.value = selectedValue
                numberPreference.summary = numberPreference.summary // trigger summery update
            }
        }

        companion object {
            fun newInstance(key: String): PreferenceDialogFragmentCompat {
                Timber.d("newInstance for $key")
                val f = NumberPickerPreferenceDialogFragment()
                val b = Bundle(1)
                b.putString(ARG_KEY, key)
                f.arguments = b
                return f
            }
        }
    } // clazz

    companion object {
        const val ns = "http://kivitro.net/android/preferences"
        fun newDialogInstance(key: String): PreferenceDialogFragmentCompat {
            return NumberPickerPreferenceDialogFragment.newInstance(key)
        }
    }

}