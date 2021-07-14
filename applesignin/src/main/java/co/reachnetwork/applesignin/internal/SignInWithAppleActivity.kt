package co.reachnetwork.applesignin.internal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import co.reachnetwork.applesignin.R
import co.reachnetwork.applesignin.SignInWithAppleRequest
import co.reachnetwork.applesignin.SignInWithAppleResult
import co.reachnetwork.applesignin.databinding.ActivitySignInWithAppleBinding
import co.reachnetwork.applesignin.webview.SignInWithAppleWebViewClient
import co.reachnetwork.applesignin.webview.SignInWithAppleWebViewClientForPost
import co.reachnetwork.applesignin.webview.SignInWithAppleWebViewClientForQuery


internal class SignInWithAppleActivity : AppCompatActivity() {

    companion object {

        const val KEY_EXTRA_RESULT = "key_extra_result"
        const val KEY_EXTRA_ERROR = "key_extra_error"
        private const val KEY_EXTRA_REQUEST = "key_extra_request"
        private const val KEY_STATE_WEB_VIEW = "key_state_web_view"

        fun createIntent(
            context: Context,
            req: SignInWithAppleRequest
        ): Intent =
            Intent(context, SignInWithAppleActivity::class.java).apply {
                putExtra(KEY_EXTRA_REQUEST, req)
            }
    }

    private lateinit var binding: ActivitySignInWithAppleBinding

    private val authCallback = object : SignInWithAppleWebViewClient.Callback {
        override fun onSuccess(result: SignInWithAppleResult) {
            val data = Intent().apply {
                putExtra(KEY_EXTRA_RESULT, result)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        override fun onError(code: Int, message: String) {
            val data = Intent().apply {
                putExtra(KEY_EXTRA_ERROR, ErrorInfo(code, message))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        override fun onCancel() {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_sign_in_with_apple
        )

        val req = intent.getParcelableExtra<SignInWithAppleRequest>(
            KEY_EXTRA_REQUEST
        )

        binding.webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        binding.webView.apply {
            if (req?.isQueryResponseType == true) {
                webViewClient = SignInWithAppleWebViewClientForQuery(req, authCallback)
            } else {
                req?.let {
                    webViewClient = SignInWithAppleWebViewClientForPost(it, authCallback, this)
                }
            }
        }

        if (savedInstanceState != null) {
            savedInstanceState.getBundle(KEY_STATE_WEB_VIEW)?.also {
                binding.webView.restoreState(it)
            }
        } else {
            req?.let {
                binding.webView.loadUrl(SignInWithAppleUri(it).authUri().toString())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(
            KEY_STATE_WEB_VIEW,
            Bundle().apply {
                binding.webView.saveState(this)
            }
        )
    }
}
