package edu.training.droidbountyhunterkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import edu.training.droidbountyhunterkotlin.ui.main.SectionsPagerAdapter

class HomeActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val tabs = findViewById<TabLayout>(R.id.tabs)



        val fab = findViewById<FloatingActionButton>(R.id.fab)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        viewPager.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)

        fab.setOnClickListener { view ->
            resultLauncher.launch(Intent(this, AgregarActivity::class.java))

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // TODO handle the result
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_agregar -> {
                resultLauncher.launch(Intent(this, AgregarActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
