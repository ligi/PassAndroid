package org.ligi.passandroid.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.fullscreen_image.*
import org.ligi.kaxt.lockOrientation
import org.ligi.passandroid.R
import timber.log.Timber

class FullscreenBarcodeActivity : PassViewActivityBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_image)

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }
    }

    override fun onResume() {
        super.onResume()

        if (currentPass.barCode == null) {
            Timber.w("FullscreenBarcodeActivity in bad state")
            finish() // this should never happen, but better safe than sorry
            return
        }
        setBestFittingOrientationForBarCode()

        fullscreen_barcode.setImageDrawable(currentPass.barCode!!.getBitmap(resources))

        if (currentPass.barCode!!.alternativeText != null) {
            alternativeBarcodeText.visibility = View.VISIBLE
            alternativeBarcodeText.text = currentPass.barCode!!.alternativeText
        } else {
            alternativeBarcodeText.visibility = View.GONE
        }

    }

    /**
     * QR and AZTEC are best fit in Portrait
     * PDF417 is best viewed in Landscape
     *
     *
     * main work is to avoid changing if we are already optimal
     * ( reverse orientation / sensor is the problem here ..)
     */
    private fun setBestFittingOrientationForBarCode() {
        if (currentPass.barCode!!.format!!.isQuadratic()) {
            when (requestedOrientation) {

                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT -> return  // do nothing

                else -> lockOrientation(Configuration.ORIENTATION_PORTRAIT)
            }

        } else {
            when (requestedOrientation) {

                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> return  // do nothing

                else -> lockOrientation(Configuration.ORIENTATION_LANDSCAPE)
            }

        }
    }
}
