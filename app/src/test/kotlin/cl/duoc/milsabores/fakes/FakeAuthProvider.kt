package cl.duoc.milsabores.fakes

import cl.duoc.milsabores.data.repository.auth.IAuthProvider

class FakeAuthProvider(private val uid: String?) : IAuthProvider {
    override val currentUid: String? = uid
}
