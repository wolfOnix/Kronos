package com.roko.kronos.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.roko.kronos.R
import com.roko.kronos.constants.MODIFIER_PADDING_BOTTOM_EXTRA
import com.roko.kronos.ui.theme.Lavender
import com.roko.kronos.ui.theme.Mint
import com.roko.kronos.ui.theme.or

@Composable fun HeaderIcon() {
    Icon(
        modifier = MODIFIER_PADDING_BOTTOM_EXTRA.height(40.dp),
        imageVector = ImageVector.vectorResource(id = R.drawable.notification_logo),
        contentDescription = null,
        tint = Lavender or Mint,
    )
}
