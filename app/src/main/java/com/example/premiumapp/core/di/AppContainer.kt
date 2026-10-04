package com.example.premiumapp.core.di

import android.content.Context

typealias AppContainer = com.example.premiumapp.core.common.AppContainer
typealias AppContainerImpl = com.example.premiumapp.core.common.AppContainerImpl

fun AppContainer(context: Context): AppContainer = com.example.premiumapp.core.common.AppContainer(context)
