package com.rumahtaqwa.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RumahTaqwaShapes = Shapes(
    // Card, dialog
    medium = RoundedCornerShape(12.dp),
    // Button, chip, TextField
    small = RoundedCornerShape(8.dp),
    // Bottom sheet, modal
    large = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
)