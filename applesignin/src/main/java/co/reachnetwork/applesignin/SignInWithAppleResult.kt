package co.reachnetwork.applesignin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignInWithAppleResult(
    val code: String? = null,
    val idToken: String? = null,
    val user: User? = null
) : Parcelable {

    @Parcelize
    data class User(
        val name: Name? = null,
        val email: String? = null
    ) : Parcelable {

        @Parcelize
        data class Name(
            val firstName: String?,
            val lastName: String?
        ) : Parcelable
    }
}
