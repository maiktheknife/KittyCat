package net.kivitro.android.preferences

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.support.v7.preference.DialogPreference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.util.AttributeSet
import android.view.View
import com.flask.colorpicker.ColorPickerView
import timber.log.Timber

/**
 * Created by Max on 29.08.2016.
 */
class ColorPickerPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    var color = 0
        get
        set(value) {
            field = value
            persistInt(value)
        }

    init {
        isPersistent = true
        dialogIcon = null
        dialogLayoutResource = R.layout.pref_colorpicker

        // layoutResource = R.layout.pref_colorpicker_widget

        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.preferenceStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Timber.d("onGetDefaultValue")
        return a.getIndex(index)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        Timber.d("onSetInitialValue $restorePersistedValue $defaultValue")
        color = if (restorePersistedValue) getPersistedInt(color) else defaultValue as Int
    }

    class ColorPickerPreferenceDialogFragment: PreferenceDialogFragmentCompat(){
        private lateinit var colorPicker: ColorPickerView
        private lateinit var colorPreference: ColorPickerPreference

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)
            colorPreference = preference as ColorPickerPreference
            colorPicker = view.findViewById(R.id.color_picker_view) as ColorPickerView

            colorPicker.setColor(colorPreference.color, true)

            Timber.d("onBindDialogView ${colorPreference.color}")
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            val selectedValue = colorPicker.selectedColor
            Timber.d("onDialogClosed $positiveResult $selectedValue")
            if (positiveResult && colorPreference.callChangeListener(selectedValue)) {
                colorPreference.color = selectedValue
            }
        }

        companion object {
            fun newInstance(key: String): PreferenceDialogFragmentCompat {
                Timber.d("newInstance for $key")
                val f = ColorPickerPreferenceDialogFragment()
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
            return ColorPickerPreferenceDialogFragment.newInstance(key)
        }
    }

}