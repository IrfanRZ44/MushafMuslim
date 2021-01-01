package id.exomatik.bacashirah.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import coil.api.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import id.exomatik.bacashirah.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun showLog(message: String?){
    Log.e("Error", "$message This log")
}

fun dismissKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocus = activity.currentFocus
    if (currentFocus != null) imm.hideSoftInputFromWindow(
        currentFocus.applicationWindowToken, 0
    )
}

@BindingAdapter("visible")
fun setVisibility(view: View?, isVisible: Boolean) {
    if (isVisible)
        view?.visibility = View.VISIBLE
    else
        view?.visibility = View.GONE
}

@BindingAdapter("toast")
fun showMessage(view: View?, message: String?) {
    if (message != null) {
        Toast.makeText(view?.context, message, Toast.LENGTH_LONG).show()
    }
}

@BindingAdapter("snackbar")
fun showSnackbar(view: View?, message: String?) {
    try {
        if (message != null) {
            val snackbar =
                Snackbar.make(view ?: throw Exception("No View"), "", Snackbar.LENGTH_LONG)

            val v = snackbar.view

            v.background = ContextCompat.getDrawable(view.context, R.drawable.snakbar_black)

            val tv = v.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView

            tv.setTextColor(Color.parseColor("#FFFFFF"))

            snackbar.setText(message)
            snackbar.show()
        }
    } catch (e: Exception) {
        Log.e("Error", e.message + " Message Error")
    }
}

fun showSnackbarIndefinite(view: View?, message: String?) {
    try {
        if (message != null) {
            val snackbar =
                Snackbar.make(view ?: throw Exception("No View"), "", Snackbar.LENGTH_INDEFINITE)

            val v = snackbar.view

            v.background = ContextCompat.getDrawable(view.context, R.drawable.snakbar_black)

            val tv = v.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView

            tv.setTextColor(Color.parseColor("#FFFFFF"))

            snackbar.setText(message)
            snackbar.show()
        }
    } catch (e: Exception) {
        Log.e("Error", e.message + " Message Error")
    }
}

@BindingAdapter("showTextStatus")
fun showTextStatus(appCompatTextView: AppCompatTextView, message: String?) {
    try {
        if (!message.isNullOrEmpty()) {
            appCompatTextView.visibility = View.VISIBLE
            if (message.contains("Berhasil")) {
                appCompatTextView.setTextColor(Color.parseColor("#39883C"))
            } else if (message.contains("Error") || message.contains("Gagal")) {
                appCompatTextView.setTextColor(Color.parseColor("#d32f2f"))
            } else if (message.contains("belum")){
                appCompatTextView.setTextColor(Color.parseColor("#757575"))
            } else{
                appCompatTextView.setTextColor(Color.parseColor("#757575"))
            }
        }
        else{
            appCompatTextView.visibility = View.GONE
        }
        appCompatTextView.text = message
    } catch (e: Exception) {
        Log.e("Error", e.message + " Message Error")
    }
}

@BindingAdapter("disableButton")
fun disableButton(view: View?, isEnable: Boolean) {
    view?.isEnabled = !isEnable
}

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, value: String?) {
    if (view.isClickable) {
        if (value == null) {
            view.error = "Data tidak boleh kosong"
        } else {
            view.error = null
        }
    }
}

@BindingAdapter("errorTextPhone")
fun setErrorMessagePhone(view: TextInputLayout, value: String?) {
    if (value != null) {
        if (!value.contains("+62")) {
            view.error = "Data harus mengandung Kode Negara"
        } else {
            view.error = null
        }
    }

    if (view.isClickable) {
        if (value == null) {
            view.error = "Data tidak boleh kosong"
        } else {
            view.error = null
        }
    }
}

@BindingAdapter("imageSrc")
fun setImage(image: AppCompatImageView, value: String?) {
    image.load(value) {
        crossfade(true)
        placeholder(R.drawable.ic_camera_white)
        transformations(CircleCropTransformation())
        error(R.drawable.ic_camera_white)
        fallback(R.drawable.ic_camera_white)
        memoryCachePolicy(CachePolicy.ENABLED)
    }

    if (!value.isNullOrEmpty()) image.setBackgroundResource(android.R.color.transparent)
    else image.setBackgroundResource(R.drawable.border_circle_gray)
}

@BindingAdapter("imageBoxSrc")
fun setImageBox(image: AppCompatImageView, value: String?) {
    image.load(value) {
        crossfade(true)
        placeholder(R.drawable.ic_camera_white)
        error(R.drawable.ic_camera_white)
        fallback(R.drawable.ic_camera_white)
        memoryCachePolicy(CachePolicy.ENABLED)
    }

    if (!value.isNullOrEmpty()) image.setBackgroundResource(R.drawable.radius_gray)
    else image.setBackgroundResource(R.drawable.border_box_gray)
}

fun onClickFoto(urlFoto: String, navController: NavController){
    val bundle = Bundle()
    val fragmentTujuan = LihatFotoFragment()
    bundle.putString("urlFoto", urlFoto)
    fragmentTujuan.arguments = bundle
    navController.navigate(R.id.lihatFotoFragment, bundle)
}
