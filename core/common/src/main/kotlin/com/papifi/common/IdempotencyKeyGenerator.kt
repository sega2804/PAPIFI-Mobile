package com.papifi.common

import java.util.UUID

interface IdempotencyKeyGenerator {
    fun generateKey(): String
}

class UuidIdempotencyKeyGenerator : IdempotencyKeyGenerator {
    override fun generateKey(): String {
        return UUID.randomUUID().toString()
    }
}
