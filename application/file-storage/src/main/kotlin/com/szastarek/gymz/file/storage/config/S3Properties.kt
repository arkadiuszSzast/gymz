package com.szastarek.gymz.file.storage.config

import com.szastarek.gymz.shared.config.ConfigKey
import com.szastarek.gymz.shared.config.ConfigMap

data class S3Properties(
    val s3Endpoint: String,
    val bucketPrefix: String,
    val region: String,
) {
    companion object {
        fun create(config: ConfigMap): S3Properties {
            return S3Properties(
                config.getStringProperty(ConfigKey("s3.endpoint")),
                config.getStringProperty(ConfigKey("s3.bucketPrefix")),
                config.getStringProperty(ConfigKey("s3.region")),
            )
        }
    }
}
