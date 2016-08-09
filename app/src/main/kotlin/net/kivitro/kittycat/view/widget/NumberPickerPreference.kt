package net.kivitro.kittycat.view.widget

import android.content.Context
import android.content.res.TypedArray
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import net.kivitro.kittycat.R
import timber.log.Timber

/**
 * Created by Max on 06.08.2016.
 */
class NumberPickerPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    var number = 0
        get() = field
        set(value) {
            field = value
            persistInt(value)
        }

    init {
        // Do custom stuff here, read attributes etc.
        isPersistent = true
        dialogIcon = null
        dialogLayoutResource = R.layout.pref_dialog_time

        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? ) : this(context, attrs, R.attr.preferenceStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        Timber.d("onGetDefaultValue")
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        Timber.d("onSetInitialValue $restorePersistedValue $defaultValue")
        number = if (restorePersistedValue) getPersistedInt(number) else defaultValue as Int
    }

    override fun getSummary(): CharSequence {
        return getPersistedInt(number).toString()
    }

}