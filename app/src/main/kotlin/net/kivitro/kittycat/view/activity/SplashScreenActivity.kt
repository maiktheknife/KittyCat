package net.kivitro.kittycat.view.activity;

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Max on 02.04.2017.
 */

class SplashScreenActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}
