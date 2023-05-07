package com.roko.kronos.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.roko.kronos.ui.theme.likeBackground
import com.roko.kronos.ui.theme.likeOnBackground

@Composable fun TextButton(
    @StringRes stringRes: Int,
    onClick: () -> Unit = { }
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = likeBackground(),
            containerColor = likeOnBackground(),
        )
    ) {
        Text(text = stringResource(id = stringRes))
    }
}
