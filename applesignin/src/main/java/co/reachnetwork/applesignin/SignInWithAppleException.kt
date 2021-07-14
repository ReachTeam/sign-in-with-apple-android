package co.reachnetwork.applesignin

import co.reachnetwork.applesignin.internal.ErrorInfo


class SignInWithAppleException(val code: Int, message: String) : Exception(message) {

    companion object {
        const val NOT_FOUND = ErrorInfo.NOT_FOUND
        const val INVALID_STATE = ErrorInfo.INVALID_REQUEST
        const val INTERNAL = ErrorInfo.INTERNAL
    }
}