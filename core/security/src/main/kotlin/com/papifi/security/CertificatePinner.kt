package com.papifi.security

interface CertificatePinnerWrapper {
    fun getPinnedDomains(): Map<String, List<String>>
}

class DefaultCertificatePinnerWrapper : CertificatePinnerWrapper {
    override fun getPinnedDomains(): Map<String, List<String>> {
        return mapOf(
            "api.papifi.com" to listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        )
    }
}
