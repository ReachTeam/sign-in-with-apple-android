package co.reachnetwork.applesignin.webview

import co.reachnetwork.applesignin.SignInWithAppleRequest
import co.reachnetwork.applesignin.SignInWithAppleResult

interface SignInWithAppleWebViewClient {

    val request: SignInWithAppleRequest
    val callback: Callback

    interface Callback {

        fun onSuccess(result: SignInWithAppleResult)

        fun onError(code: Int, message: String)

        fun onCancel()
    }
}