package com.example.adminblinkitclone

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://ckftxhbccogjinuvsshd.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNrZnR4aGJjY29namludXZzc2hkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg4Mjk0MjcsImV4cCI6MjA4NDQwNTQyN30.qIiw09qvcBGccmmz-0_dhl3C3gYn508UnonV4g9Zcds"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
