package com.indieteam.mytask.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.indieteam.mytask.R
import kotlinx.android.synthetic.main.fragment_qr.*
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType

class QrFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val studentId = arguments?.getString("studentId")
        studentId?.let {
            object : Thread() {
                override fun run() {
                    try {
                        val qrBitmap = QRCode.from(it.toUpperCase()).to(ImageType.JPG).withSize(1000, 1000).bitmap()
                        activity?.runOnUiThread {
                            qr_img.setImageBitmap(qrBitmap)
                        }
                    }catch (e: Exception){
                        Log.d("err", e.toString())
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "Err #02, Không thể tạo mã QR", Toast.LENGTH_SHORT).show()
                        }
                    }
                    this.join()
                }
            }.start()
        }
    }
}
