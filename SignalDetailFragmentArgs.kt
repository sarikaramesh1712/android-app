package com.example.mobileproject.ui.details

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Int
import kotlin.jvm.JvmStatic

public data class SignalDetailFragmentArgs(
  public val signalId: Int = -1,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putInt("signalId", this.signalId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("signalId", this.signalId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): SignalDetailFragmentArgs {
      bundle.setClassLoader(SignalDetailFragmentArgs::class.java.classLoader)
      val __signalId : Int
      if (bundle.containsKey("signalId")) {
        __signalId = bundle.getInt("signalId")
      } else {
        __signalId = -1
      }
      return SignalDetailFragmentArgs(__signalId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): SignalDetailFragmentArgs {
      val __signalId : Int?
      if (savedStateHandle.contains("signalId")) {
        __signalId = savedStateHandle["signalId"]
        if (__signalId == null) {
          throw IllegalArgumentException("Argument \"signalId\" of type integer does not support null values")
        }
      } else {
        __signalId = -1
      }
      return SignalDetailFragmentArgs(__signalId)
    }
  }
}
