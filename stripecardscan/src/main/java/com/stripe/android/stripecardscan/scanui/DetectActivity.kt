package com.stripe.android.stripecardscan.scanui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.stripe.android.camera.framework.image.scale
import com.stripe.android.camera.framework.image.size
import com.stripe.android.camera.framework.util.toRect
import com.stripe.android.stripecardscan.R
import com.stripe.android.stripecardscan.framework.ResourceFetcher
import com.stripe.android.stripecardscan.payment.ml.CardDetect
import com.stripe.android.stripecardscan.payment.ml.CardDetectModelManager
import com.stripe.android.stripecardscan.payment.ml.SSDOcr
import com.stripe.android.stripecardscan.payment.ml.SSDOcrModelManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)

        CoroutineScope(Dispatchers.Main).launch {
            resourceModelExecution_works()

        }
    }

    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
    suspend fun readDate() {
        val bitmap =resources.getDrawable(R.drawable.card_pan, null)
            .toBitmap()
            .scale(Size(224, 224))

        val fetcher = CardDetectModelManager.getModelFetcher(this)
//        assertNotNull(fetcher)
//        assertTrue(fetcher is ResourceFetcher)
        fetcher.clearCache()

        val model = CardDetect.Factory(
            this,
            fetcher.fetchData(forImmediateUse = true, isOptional = false)
        ).newInstance()
        //assertNotNull(model)

        val prediction = model?.analyze(
            CardDetect.cameraPreviewToInput(
                bitmap,
                bitmap.size().toRect(),
                bitmap.size().toRect()
            ),
            Unit
        )

        Toast.makeText(this, prediction?.side.toString(), Toast.LENGTH_SHORT).show()
        Log.e("@@@", "EEEEEEEEEEEEEEEEEEEEee :${CardDetect.Prediction.Side.PAN.name} ${prediction?.side} ")
    }

    @SuppressLint("RestrictedApi")
    suspend fun resourceModelExecution_works()  {
        val bitmap = resources.getDrawable(R.drawable.ocr_card_numbers1, null)
            .toBitmap()
            .scale(Size(600, 375))
        val fetcher = SSDOcrModelManager.getModelFetcher(this)
        //assertNotNull(fetcher)
        //assertTrue(fetcher is ResourceFetcher)
        fetcher.clearCache()

        val model = SSDOcr.Factory(
            this,
            fetcher.fetchData(forImmediateUse = true, isOptional = false)
        ).newInstance()
        //assertNotNull(model)

        val prediction = model?.analyze(
            SSDOcr.cameraPreviewToInput(
                bitmap,
                bitmap.size().toRect(),
                bitmap.size().toRect()
            ),
            Unit
        )
        //assertNotNull(prediction)

        //assertEquals("4242424242424242", prediction?.pan)

        Toast.makeText(this, prediction?.pan.toString(), Toast.LENGTH_SHORT).show()
        Log.e("@@@", "EEEEEEEEEEEEEEEEEEEEee :${prediction?.pan}")

    }

}