package net.kivitro.kittycat.view.activity

import android.content.Context
import android.content.Intent
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

		setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
		supportActionBar?.apply {
			setDisplayShowHomeEnabled(true)
			setHomeButtonEnabled(true)
			setDisplayHomeAsUpEnabled(true)
		}
	}

	companion object {
		fun getStarterIntent(context: Context) = Intent(context, SettingsActivity::class.java)
	}

}
