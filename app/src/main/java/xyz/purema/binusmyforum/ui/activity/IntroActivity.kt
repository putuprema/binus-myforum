package xyz.purema.binusmyforum.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.domain.utils.ActivityUtils

@ExperimentalCoroutinesApi
class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            ActivityUtils.goToActivity(this, LoginActivity::class.java)
        }
    }
}