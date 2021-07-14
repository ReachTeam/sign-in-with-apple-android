package co.reachnetwork.applesignin.webview

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import co.reachnetwork.applesignin.SignInWithAppleRequest
import co.reachnetwork.applesignin.SignInWithAppleResult
import co.reachnetwork.applesignin.internal.ErrorInfo


class SignInWithAppleWebViewClientForQuery(
    override val request: SignInWithAppleRequest,
    override val callback: SignInWithAppleWebViewClient.Callback
) : WebViewClient(), SignInWithAppleWebViewClient {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return handleUrlLoading(view, Uri.parse(url))
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return handleUrlLoading(view, request.url)
    }

    private fun handleUrlLoading(webView: WebView, url: Uri): Boolean {
        return url.toString().let {
            when {
                it.contains("appleid.apple.com") -> false
                it.contains(request.redirectUri) -> {
                    val code = url.getQueryParameter("code")
                    val state = url.getQueryParameter("state")
                    val err = url.getQueryParameter("error")

                    when {
                        err == "user_cancelled_authorize" -> {
                            callback.onCancel()
                        }
                        code == null -> {
                            callback.onError(ErrorInfo.NOT_FOUND, "code.not.found")
                        }
                        state != request.state -> {
                            callback.onError(ErrorInfo.INTERNAL, "state.not.matched")
                        }
                        else -> {
                            callback.onSuccess(SignInWithAppleResult(code = code))
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }
}
