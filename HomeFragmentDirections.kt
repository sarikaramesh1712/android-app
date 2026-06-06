package com.example.mobileproject.ui.home

import android.os.Bundle
import androidx.`annotation`.CheckResult
import androidx.navigation.NavDirections
import com.example.mobileproject.R
import kotlin.Int

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeFragmentToSignalDetailFragment(
    public val signalId: Int = -1,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_homeFragment_to_signalDetailFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putInt("signalId", this.signalId)
        return result
      }
  }

  public companion object {
    @CheckResult
    public fun actionHomeFragmentToSignalDetailFragment(signalId: Int = -1): NavDirections = ActionHomeFragmentToSignalDetailFragment(signalId)
  }
}
