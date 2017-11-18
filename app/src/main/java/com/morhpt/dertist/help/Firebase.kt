package com.morhpt.dertist.help

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


open class Firebase {
    val DB_USERS    = "users"
    val DB_DERTS    = "derts"
    val DB_ANSWERS  = "answers"
    val DB_SMOKES   = "smokes"

    val DB_SUB_USER         = "user"
    val DB_SUB_TIMESTAMP    = "timestamp"

    val ref         = FirebaseFirestore.getInstance()
    val usersRef    = ref.collection(DB_USERS)
    val dertsRef    = ref.collection(DB_DERTS)
    val answersRef  = ref.collection(DB_ANSWERS)

    val auth        = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    init {
        ref.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }
}
