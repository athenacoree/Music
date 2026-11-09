package com.athenacoree.music

import io.github.jan.supabase.gotrue.providers.builtin.Email

/**
 * Email/password account actions. Supabase handles password hashing and tokens.
 * The app never stores or logs the user's password itself.
 */
internal object MusicAuthRepository {
    suspend fun signUp(email: String, password: String) {
        MusicSupabase.client.gotrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String) {
        MusicSupabase.client.gotrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        MusicSupabase.client.gotrue.signOut()
    }
}
