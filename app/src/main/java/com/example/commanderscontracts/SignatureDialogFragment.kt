package com.example.commanderscontracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.signature_pad.*
import java.util.*

class SignatureDialogFragment(private val onSignedListener: OnSignedCaptureListener) :
    DialogFragment(),
    SignatureView.OnSignedListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.signature_pad, container, false)
    }

    override fun getTheme(): Int {
        return R.style.Dialog_App
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonCancel.setOnClickListener { dismiss() }
        buttonClear.setOnClickListener { signatureView.clear() }
        buttonOk.setOnClickListener {

            val filename = UUID.randomUUID().toString()
            onSignedListener.onSignatureCaptured(signatureView.getSignatureBitmap(), filename)
            dismiss()
        }
        signatureView.setOnSignedListener(this)
    }
    override fun onStartSigning() {
    }

    override fun onSigned() {
        buttonOk.isEnabled = true
        buttonClear.isEnabled = true
    }

    override fun onClear() {
        buttonClear.isEnabled = false
        buttonOk.isEnabled = false
    }
}