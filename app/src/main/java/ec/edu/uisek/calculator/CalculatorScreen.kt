package ec.edu.uisek.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uisek.calculator.ui.theme.Purple40
import ec.edu.uisek.calculator.ui.theme.Purple80
import ec.edu.uisek.calculator.ui.theme.UiSekBlue

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val state = viewModel.state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Pantalla mejorada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = state.display,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 64.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    lineHeight = 70.sp
                )
            }

            // Cuadrícula de botones
            CalculatorGrid(onEvent = viewModel::onEvent)
        }
    }
}

@Composable
fun CalculatorGrid(onEvent: (CalculatorEvent) -> Unit) {
    val buttons = listOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "×",  // Usar "×" en lugar de "*"
        "1", "2", "3", "−",
        "0", ".", "=", "+"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(buttons.size) { index ->
            val label = buttons[index]
            CalculatorButton(label = label) {
                when (label) {
                    in "0".."9" -> onEvent(CalculatorEvent.Number(label))
                    "." -> onEvent(CalculatorEvent.Decimal)
                    "=" -> onEvent(CalculatorEvent.Calculate)
                    else -> onEvent(CalculatorEvent.Operator(label))
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            CalculatorButton(label = "AC", isSpecial = true) {
                onEvent(CalculatorEvent.AllClear)
            }
        }
        item {}
        item {
            CalculatorButton(label = "C", isSpecial = true) {
                onEvent(CalculatorEvent.Clear)
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    isSpecial: Boolean = false
) {
    val buttonColor = when {
        isSpecial -> Color(0xFFA5D6A7) // Verde claro para AC y C
        label in listOf("÷", "×", "−", "+", "=") -> Purple40 // Operadores
        label == "." -> Purple80 // Punto decimal
        else -> UiSekBlue // Números
    }

    val textColor = if (isSpecial) Color.Black else Color.White

    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(if(label == "AC") 2f else 1f)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorScreen()
}