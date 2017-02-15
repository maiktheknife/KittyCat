package net.kivitro.kittycat.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import net.kivitro.kittycat.R

/**
 * Created by Max on 04.08.2016.
 */
class SettingsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.ac_settings)

		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		supportActionBar?.setHomeButtonEnabled(true)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

}
