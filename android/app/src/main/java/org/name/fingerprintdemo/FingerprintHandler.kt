package org.name.fingerprintdemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Field

class FingerprintHandler(private val appContext: Context): FingerprintManager.AuthenticationCallback() {
    private var cancellationSignal: CancellationSignal? = null

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        Toast.makeText(appContext, "Authentication error\n$errString", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        Toast.makeText(appContext, "Authentication help\n$helpString", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(appContext, "Authentication failed", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        try {
            var fingerprintField : Field = (result as Object).getClass().getDeclaredField("mFingerprint") // there are 3 fields: mCryptoObject, mFingerprint [private], mUserId
            fingerprintField.setAccessible(true)
            var fingerprintCopy : Object? = fingerprintField.get(result) as? Object
            Toast.makeText(appContext, "Authentication succeeded\n$fingerprintField = $fingerprintCopy", Toast.LENGTH_LONG).show()
        } catch(e: IllegalAccessException) {
            Toast.makeText(appContext, "Authentication succeeded can't get it IllegalAccess", Toast.LENGTH_LONG).show()
        } catch(e: TypeCastException) {
            Toast.makeText(appContext, "Authentication succeeded can't get it TypeCast", Toast.LENGTH_LONG).show()
        }
    }
}