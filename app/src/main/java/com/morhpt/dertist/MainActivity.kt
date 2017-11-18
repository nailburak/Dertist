package com.morhpt.dertist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_main.*
import android.view.LayoutInflater
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.morhpt.dertist.help.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dert.view.*
import kotlinx.android.synthetic.main.recyclerview.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var gloabal     = false
    private var limit       = 1L
    private val firebase    = Firebase()
    private lateinit var mAdapter: FirestoreRecyclerAdapter<Dert, DertHolder>
    private lateinit var query: Query
    private lateinit var help: Help

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = "Derts"

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        build()
    }

    private fun build() {
        help = Help(this)
        getLimit()
        onReflesh()
    }

    private fun getLimit() {
        firebase.dertsRef.get().addOnSuccessListener { querySnapshot ->
            if (!gloabal)
                query = firebase.dertsRef.orderBy("timestamp").limit(querySnapshot.size().toLong())
            else
                query = firebase.dertsRef.orderBy("timestamp").whereEqualTo("country", help.COUNTRY_CODE).limit(querySnapshot.size().toLong())
            setRecyclerView()
        }
    }

    private fun setRecyclerView() {

        val options = FirestoreRecyclerOptions.Builder<Dert>().setQuery(query, Dert::class.java).build()
        mAdapter    = object : FirestoreRecyclerAdapter<Dert, DertHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DertHolder {
                val view = LayoutInflater.from(parent?.context) .inflate(R.layout.item_dert, parent, false)
                main_no_data.visibility = View.GONE
                main_swiperefresh.isRefreshing = false
                return DertHolder(view)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: DertHolder?, position: Int, model: Dert?) {
                val time = TimeMinute(TimeUnit.MICROSECONDS.toMinutes(model?.timestamp!!.time)).calculate()["s"]


                firebase.usersRef.document("burak").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null)
                        return@addSnapshotListener

                    if (!documentSnapshot.exists())
                        return@addSnapshotListener

                    val user = documentSnapshot.toObject(User::class.java)

                    holder?.dert?.text  = model.dert
                    holder?.title?.text = model.title
                    holder?.posted?.text = "${time?.time} ${if (time?.time == 1L) time.type.dropLast(1) else time?.type} ago"
                    holder?.name?.text = user.displayName
                    Picasso.with(applicationContext).load(user.photoUrl).into(holder?.avatar)
                }
            }

            override fun onError(e: FirebaseFirestoreException?) {
                super.onError(e)

                main_no_data.visibility = View.VISIBLE
            }
        }

        val layoutManager = LinearLayoutManager(this)

        mAdapter.startListening()
        recyclerview.layoutManager = layoutManager
        recyclerview.itemAnimator = DefaultItemAnimator()
        recyclerview.adapter = mAdapter
    }

    private fun onReflesh() {
        main_swiperefresh.setOnRefreshListener {
            getLimit()
        }
    }


    class DertHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val main        = itemView.main
        val dert        = itemView.dert
        val title       = itemView.title
        val name        = itemView.user_name
        val avatar      = itemView.user_avatar
        val posted      = itemView.posted
        val moreVert    = itemView.more_vert

        val smoke       = itemView.smoke_icon
        val smokeCount  = itemView.smoke_count
    }

    override fun onStart() {
        super.onStart()

//        if (firebase.currentUser == null)
//            startActivity(Intent(this, SplashActivity::class.java))


    }

    override fun onStop() {
        super.onStop()

        mAdapter.stopListening()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val a = Intent(Intent.ACTION_MAIN)
            a.addCategory(Intent.CATEGORY_HOME)
            a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(a)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val drawable = DrawableCompat.wrap(menu.findItem(R.id.action_global).icon)
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white))
        menu.findItem(R.id.action_global).icon = drawable
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> true
        R.id.action_global -> {
            val drawable = DrawableCompat.wrap(resources.getDrawable(R.drawable.ic_earth))
            if (gloabal)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white))
            else
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent))
            item.icon = drawable
            gloabal = !gloabal
            getLimit()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
