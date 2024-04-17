package com.szastarek.gymz.file.storage.config

import aws.sdk.kotlin.runtime.auth.credentials.DefaultChainCredentialsProvider
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class S3Properties(
    val s3Endpoint: String,
    val bucketPrefix: String,
    val region: String,
    val useLocalstackCredentialsProvider: Boolean = false,
) {
    companion object {
        fun create(config: ConfigMap): S3Properties {
            return S3Properties(
                config.getStringProperty(ConfigKey("s3.endpoint")),
                config.getStringProperty(ConfigKey("s3.bucketPrefix")),
                config.getStringProperty(ConfigKey("s3.region")),
                config.getBooleanProperty(ConfigKey("s3.useLocalstackCredentialsProvider")),
            )
        }
    }

    val credentialsProvider: CredentialsProvider
        get() = if (useLocalstackCredentialsProvider) {
            StaticCredentialsProvider(Credentials("accessKeyId", "secret"))
        } else {
            DefaultChainCredentialsProvider()
        }
}
