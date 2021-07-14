package co.reachnetwork.applesignin

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import co.reachnetwork.applesignin.internal.ErrorInfo
import co.reachnetwork.applesignin.internal.SignInWithAppleActivity

class SignInWithAppleClient(
    private val signInRequestCode: Int = 7919
) {

    private var callback: Callback? = null

    fun signIn(
        activity: Activity,
        request: SignInWithAppleRequest,
        callback: Callback
    ) {
        if (request.clientId.isBlank()) {
            callback.onError(
                SignInWithAppleException(
                    ErrorInfo.INVALID_REQUEST,
                    "client_id.required"
                )
            )
            return
        }
        if (request.redirectUri.isBlank()) {
            callback.onError(
                SignInWithAppleException(
                    ErrorInfo.INVALID_REQUEST,
                    "redirect_uri.required"
                )
            )
            return
        }

        this.callback = callback
        activity.startActivityForResult(
            SignInWithAppleActivity.createIntent(activity, request),
            signInRequestCode
        )
    }

    fun signIn(
        fragment: Fragment,
        request: SignInWithAppleRequest,
        callback: Callback
    ) {
        if (request.clientId.isBlank()) {
            callback.onError(
                SignInWithAppleException(
                    ErrorInfo.INVALID_REQUEST,
                    "client_id.required"
                )
            )
            return
        }
        if (request.redirectUri.isBlank()) {
            callback.onError(
                SignInWithAppleException(
                    ErrorInfo.INVALID_REQUEST,
                    "redirect_uri.required"
                )
            )
            return
        }

        this.callback = callback
        fragment.startActivityForResult(
            SignInWithAppleActivity.createIntent(fragment.requireContext(), request),
            signInRequestCode
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == signInRequestCode) {
            val callback = this.callback ?: return
            this.callback = null

            when (resultCode) {
                Activity.RESULT_CANCELED -> {
                    callback.onCancel()
                }
                else -> {
                    val error =
                        data?.getParcelableExtra<ErrorInfo>(SignInWithAppleActivity.KEY_EXTRA_ERROR)
                    if (error != null) {
                        callback.onError(SignInWithAppleException(error.code, error.message))
                        return
                    }

                    val result =
                        data?.getParcelableExtra<SignInWithAppleResult>(SignInWithAppleActivity.KEY_EXTRA_RESULT)
                    if (result == null) {
                        callback.onError(
                            SignInWithAppleException(
                                ErrorInfo.NOT_FOUND,
                                "result.not.found"
                            )
                        )
                        return
                    }

                    callback.onSuccess(result)
                }
            }
        }
    }

    interface Callback {

        fun onSuccess(result: SignInWithAppleResult)

        fun onError(throwable: Throwable)

        fun onCancel()
    }
}
