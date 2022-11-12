package com.roko.kronos.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.roko.kronos.ui.theme.likeOnPrimary
import com.roko.kronos.ui.theme.likePrimary

@Composable fun TextButton(
    @StringRes stringRes: Int,
    onClick: () -> Unit = { }
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = likeOnPrimary(),
            containerColor = likePrimary(),
        )
    ) {
        Text(text = stringResource(id = stringRes))
    }
}
