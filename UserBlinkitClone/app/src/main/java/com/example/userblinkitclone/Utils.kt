package com.example.userblinkitclone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.Json

object Utils {

    private const val SUPABASE_URL = "https://ckftxhbccogjinuvsshd.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNrZnR4aGJjY29namludXZzc2hkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4Mjk0MjcsImV4cCI6MjA4NDQwNTQyN30.qIiw09qvcBGccmmz-0_dhl3C3gYn508UnonV4g9Zcds"

    private val supabaseDeferred = CompletableDeferred<SupabaseClient>()

    fun initialize() {
        val client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest) {
                serializer = KotlinXSerializer(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Auth)
            install(Storage)
        }
        supabaseDeferred.complete(client)
    }

    suspend fun getSupabase(): SupabaseClient {
        return supabaseDeferred.await()
    }
}