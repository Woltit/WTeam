package com.wteam.plugins

import org.gradle.api.provider.Property

abstract class Extension {
    abstract val enabled: Property<Boolean>
}
