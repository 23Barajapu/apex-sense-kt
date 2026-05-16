package com.apexsense.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://pqfcdhuowhjcaixazjvr.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBxZmNkaHVvd2hqY2FpeGF6anZyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg0MjQxOTgsImV4cCI6MjA5NDAwMDE5OH0.DLz4p6SdwKdAX_aLjpc1ciaESrICLzC9G2XNBKy4hHY"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
    }
}
