package com.exomatik.zcodex.ui.main

import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.exomatik.zcodex.R
import com.exomatik.zcodex.base.BaseActivity
import com.exomatik.zcodex.model.ModelUser
import com.exomatik.zcodex.utils.Constant
import com.exomatik.zcodex.utils.Constant.attention
import com.exomatik.zcodex.utils.Constant.iya
import com.exomatik.zcodex.utils.Constant.tidak
import com.exomatik.zcodex.utils.FirebaseUtils
import com.exomatik.zcodex.utils.dismissKeyboard
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import kotlinx.android.synthetic.main.activity_main.*
import androidx.navigation.ui.navigateUp
import coil.api.load
import kotlinx.android.synthetic.main.nav_header.view.*

class MainActivity : BaseActivity() {
    override fun getLayoutResource(): Int = R.layout.activity_main
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var view: View
    private var timerCekKoneksi: CountDownTimer? = null

    override fun myCodeHere() {
        setTheme(R.style.CustomStyle)
        drawerLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setSupportActionBar(toolbar)
        view = findViewById(android.R.id.content)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_beranda,
                R.id.nav_edit_profile,
                R.id.nav_logout
            ), drawerLayout
        )
        navController = findNavController(R.id.navMuballighFragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setSavedData()
    }

    private fun setSavedData() {
        val dataUser = savedData.getDataUser()
        val headerView = navView?.getHeaderView(0)

        if (dataUser != null){

            headerView?.fotoMb?.load(savedData.getDataUser()?.urlFoto) {
                crossfade(true)
                placeholder(R.drawable.ic_profile_white)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_profile_white)
                fallback(R.drawable.ic_profile_white)
                memoryCachePolicy(CachePolicy.ENABLED)
            }

            headerView?.namaMb?.text = savedData.getDataUser()?.username
            headerView?.gelarMb?.text = savedData.getDataUser()?.noHp
        }
        else{
            headerView?.fotoMb?.load(R.drawable.ic_profile_white) {
                crossfade(true)
                placeholder(R.drawable.ic_profile_white)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_profile_white)
                fallback(R.drawable.ic_profile_white)
                memoryCachePolicy(CachePolicy.ENABLED)
            }

            headerView?.namaMb?.text = savedData.getDataUser()?.username
            headerView?.gelarMb?.text = savedData.getDataUser()?.noHp
        }
    }

    override fun onStop() {
        super.onStop()
        timerCekKoneksi?.cancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        dismissKeyboard(this)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun alertLogout() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(attention)
        alert.setMessage(Constant.alertLogout)
        alert.setPositiveButton(
            iya
        ) { _, _ ->
            savedData.getDataUser()?.username?.let { removeToken(it) }
        }
        alert.setNegativeButton(
            tidak
        ) { dialog, _ -> dialog.dismiss() }

        alert.show()
    }

    private fun removeToken(username: String) {
        showProgress()

        val onCompleteListener = OnCompleteListener<Void> { result ->
            progress.visibility = View.GONE
            if (result.isSuccessful) {
                    Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_LONG).show()

                    FirebaseUtils.signOut()
                    savedData.setDataObject(ModelUser(), Constant.referenceUser)
                    navController.navigate(R.id.splashFragment)
            } else {
                    Toast.makeText(this, "Gagal menghapus token", Toast.LENGTH_LONG).show()
                }
            }

        val onFailureListener = OnFailureListener { result ->
            progress.visibility = View.GONE
            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
        }

        FirebaseUtils.deleteValueWith2Child(
            Constant.referenceUser, username,
            Constant.token, onCompleteListener, onFailureListener)
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onBackPressed() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START)!!) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        }
        if (drawerLayout?.isDrawerOpen(GravityCompat.END)!!) {
            drawerLayout?.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        navView.menu.findItem(R.id.nav_logout)?.setOnMenuItemClickListener {
            drawerLayout?.closeDrawer(GravityCompat.START)
            alertLogout()
            true
        }
        return super.onOptionsItemSelected(item)
    }
}