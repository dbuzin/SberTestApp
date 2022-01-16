package ru.dbuzin.dev.sbertestapp.ui.feature.converter

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import ru.dbuzin.dev.sbertestapp.R
import ru.dbuzin.dev.sbertestapp.ui.base.EFFECT_LISTENER

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun ConverterScreen() {
    val viewModel: ConverterViewModel = hiltViewModel()
    ConverterContent(
        viewModel.state.value,
        viewModel.effect,
        viewModel::setEvent
    )
}

@Composable
fun ConverterContent(
    state: Converter.State,
    effectFlow: Flow<Converter.Effect>,
    onEvent: (Converter.Event) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    var sum by rememberSaveable { mutableStateOf("1") }
    val valueFrom = remember { mutableStateOf(state.from) }
    val valueTo = remember { mutableStateOf(state.to) }
    val rates = state.currentRates?.data
    val suggestions = listOf("RUB", "USD", "EUR", "GBP", "CHF", "CNY")
    var isOffline by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val textStyle = TextStyle(color = Color.White, fontSize = 20.sp)

    LaunchedEffect(EFFECT_LISTENER) {
        effectFlow.collect { effect ->
            when (effect) {
                is Converter.Effect.Offline -> {
                    isRefreshing = false
                    isOffline = true
                }
                is Converter.Effect.ConnectionRestored -> {
                    isRefreshing = false
                    if (isError || isOffline)
                        Toast.makeText(
                            context,
                            context.getString(R.string.connection_restored),
                            Toast.LENGTH_LONG
                        ).show()
                    isError = false
                    isOffline = false
                }
                is Converter.Effect.Error -> {
                    isRefreshing = false
                    isError = true
                }
                is Converter.Effect.Refresh -> {
                    isRefreshing = true
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        if (isError) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    onEvent(Converter.Event.SwipeDown)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.first_launch_error),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp),
                        color = Color.Red,
                        fontSize = 20.sp
                    )
                }
            }
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    onEvent(Converter.Event.SwipeDown)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = sum,
                            onValueChange = { textSum ->
                                sum = textSum
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            label = {
                                Text(text = stringResource(id = R.string.convert_sum))
                            },
                            textStyle = textStyle,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        var textFieldSize by remember { mutableStateOf(Size.Zero) }
                        var textFieldPos by remember { mutableStateOf(.0F) }

                        val icon = if (expanded)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.KeyboardArrowDown

                        OutlinedTextField(
                            value = valueFrom.value,
                            onValueChange = { valueFrom.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size.toSize()
                                    textFieldPos = coordinates.positionInParent().x
                                },
                            enabled = false,
                            trailingIcon = {
                                Icon(icon, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            offset = with(LocalDensity.current) {
                                DpOffset(
                                    x = textFieldPos.toDp(),
                                    y = 0.toDp()
                                )
                            }
                        ) {
                            suggestions.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    if (label != valueTo.value) {
                                        valueFrom.value = label
                                        onEvent(Converter.Event.ValueFromChanged(label))
                                        expanded = false
                                    }
                                }) {
                                    Text(text = label)
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        var textFieldSize by remember { mutableStateOf(Size.Zero) }
                        var textFieldPos by remember { mutableStateOf(.0F) }

                        val icon = if (expanded)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.KeyboardArrowDown

                        OutlinedTextField(
                            value = valueTo.value,
                            onValueChange = { valueTo.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size.toSize()
                                    textFieldPos = coordinates.positionInParent().x
                                },
                            enabled = false,
                            trailingIcon = {
                                Icon(icon, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            offset = with(LocalDensity.current) {
                                DpOffset(
                                    x = textFieldPos.toDp(),
                                    y = 0.toDp()
                                )
                            }
                        ) {
                            suggestions.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    if (label != valueFrom.value) {
                                        valueTo.value = label
                                        onEvent(Converter.Event.ValueToChanged(label))
                                        expanded = false
                                    }
                                }) {
                                    Text(text = label)
                                }
                            }
                        }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rate = rates?.get(valueFrom.value.plus(valueTo.value))
                        if (rate != null && sum.isNotEmpty()) {
                            val result = sum.replace(',', '.').toDouble().times(rate)
                            val format: String = result.toString().format("%.4f", result)
                            Text(
                                text = stringResource(
                                    id = R.string.result_sum,
                                    sum,
                                    valueFrom.value,
                                    format,
                                    valueTo.value
                                ),
                                fontSize = 20.sp
                            )
                        } else
                            Text(
                                text = if (sum.isEmpty()) stringResource(id = R.string.empty_sum) else stringResource(
                                    id = R.string.convert_error
                                ),
                                fontSize = 20.sp
                            )
                    }

                    if (isOffline)
                        Snackbar(
                            modifier = Modifier
                                .padding(20.dp)
                        ) {
                            Text(text = stringResource(id = R.string.offline))
                        }
                }
            }
        }
    }
}