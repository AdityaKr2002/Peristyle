package app.simple.peri.ui.dialogs.wallhaven

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.simple.peri.R
import app.simple.peri.activities.main.LocalDisplaySize
import app.simple.peri.models.WallhavenFilter
import app.simple.peri.preferences.WallHavenPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallhavenSearchDialog(
        filter: WallhavenFilter? = null,
        onDismiss: () -> Unit,
        onSearch: (WallhavenFilter) -> Unit
) {
    val resolutionWidth = LocalDisplaySize.current.width
    val resolutionHeight = LocalDisplaySize.current.height

    var query by remember { mutableStateOf(filter?.query ?: WallHavenPreferences.getQuery()) }
    var categories by remember { mutableStateOf(filter?.categories ?: WallHavenPreferences.getCategory()) }
    var purity by remember { mutableStateOf(filter?.purity ?: "100") }
    var atleast by remember { mutableStateOf(filter?.atleast ?: WallHavenPreferences.getAtleast() ?: "$resolutionWidth x $resolutionHeight") }
    var resolution by remember { mutableStateOf(filter?.resolution ?: WallHavenPreferences.getResolution() ?: "") }
    var ratios by remember { mutableStateOf(filter?.ratios ?: WallHavenPreferences.getRatio()) }
    var sorting by remember { mutableStateOf(filter?.sorting ?: WallHavenPreferences.getSort()) }
    var order by remember { mutableStateOf(filter?.order ?: WallHavenPreferences.getOrder()) }

    val sortingOptions = listOf("date_added", "relevance", "random", "views", "favorites", "toplist")
    val catLabels = listOf(stringResource(R.string.general), stringResource(R.string.anime), stringResource(R.string.people))
    val orderLabels = listOf(stringResource(R.string.descending), stringResource(R.string.ascending))
    val ratioOptions = listOf("portrait", "landscape", "any")

    var ratioExpanded by remember { mutableStateOf(false) }
    var sortingExpanded by remember { mutableStateOf(false) }
    var showWallhavenDocs by remember { mutableStateOf(false) }

    if (showWallhavenDocs) {
        WallhavenDocsDialog(onDismissRequest = { showWallhavenDocs = false })
    }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = stringResource(R.string.search),
                            modifier = Modifier
                                .weight(1F)
                    )
                    IconButton(
                            onClick = { showWallhavenDocs = true }
                    ) {
                        Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = ""
                        )
                    }
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                                value = query,
                                onValueChange = {
                                    query = it
                                    WallHavenPreferences.setQuery(it)
                                },
                                label = { Text(stringResource(R.string.query)) },
                                modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.category))
                        SingleChoiceSegmentedButtonRow {
                            catLabels.forEachIndexed { i, label ->
                                SegmentedButton(
                                        selected = categories[i] == '1',
                                        onClick = {
                                            categories = categories.toCharArray().also {
                                                it[i] = if (categories[i] == '1') '0' else '1'
                                            }.concatToString()

                                            WallHavenPreferences.setCategory(categories)
                                        },
                                        label = { Text(label) },
                                        shape = SegmentedButtonDefaults.itemShape(
                                                index = i,
                                                count = catLabels.size)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(text = stringResource(R.string.order))
                        SingleChoiceSegmentedButtonRow {
                            orderLabels.forEachIndexed { i, label ->
                                SegmentedButton(
                                        selected = order == if (i == 0) "desc" else "asc",
                                        onClick = {
                                            order = if (i == 0) "desc" else "asc"
                                            WallHavenPreferences.setOrder(order)
                                        },
                                        label = { Text(label) },
                                        shape = SegmentedButtonDefaults.itemShape(
                                                index = i,
                                                count = orderLabels.size)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                                expanded = sortingExpanded,
                                onExpandedChange = { sortingExpanded = !sortingExpanded }
                        ) {
                            OutlinedTextField(
                                    value = sorting,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.sort)) },
                                    trailingIcon = {
                                        Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor(
                                                type = MenuAnchorType.PrimaryNotEditable,
                                                enabled = true)
                                        .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                    expanded = sortingExpanded,
                                    onDismissRequest = { sortingExpanded = false }
                            ) {
                                sortingOptions.forEach { option ->
                                    DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                sorting = option
                                                WallHavenPreferences.setSort(option)
                                                sortingExpanded = false
                                            }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                                value = resolution,
                                onValueChange = {
                                    resolution = it
                                    WallHavenPreferences.setResolution(it)
                                },
                                label = { Text(stringResource(R.string.resolution)) },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(
                                            onClick = {
                                                resolution = "${resolutionWidth}x${resolutionHeight}"
                                                WallHavenPreferences.setResolution(resolution)
                                            }) {
                                        Icon(
                                                imageVector = Icons.Default.PhoneAndroid,
                                                contentDescription = stringResource(R.string.set_phone_resolution)
                                        )
                                    }
                                }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                                value = atleast,
                                onValueChange = {
                                    atleast = it
                                    WallHavenPreferences.setAtleast(atleast)
                                },
                                label = { Text(stringResource(R.string.at_least)) },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        atleast = "${resolutionWidth}x${resolutionHeight}"
                                        WallHavenPreferences.setAtleast(atleast)
                                    }) {
                                        Icon(
                                                imageVector = Icons.Default.PhoneAndroid,
                                                contentDescription = stringResource(R.string.set_phone_resolution)
                                        )
                                    }
                                }
                        )

                        Spacer(Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                                expanded = ratioExpanded,
                                onExpandedChange = { ratioExpanded = !ratioExpanded }
                        ) {
                            OutlinedTextField(
                                    value = ratios,
                                    onValueChange = {
                                        ratios = it
                                        WallHavenPreferences.setRatio(it)
                                    },
                                    readOnly = false,
                                    label = { Text(stringResource(R.string.ratios)) },
                                    trailingIcon = {
                                        Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor(
                                                type = MenuAnchorType.PrimaryEditable,
                                                enabled = true
                                        )
                                        .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                    expanded = ratioExpanded,
                                    onDismissRequest = { ratioExpanded = false }
                            ) {
                                ratioOptions.forEach { option ->
                                    DropdownMenuItem(
                                            text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                            onClick = {
                                                ratios = option
                                                WallHavenPreferences.setRatio(option)
                                                ratioExpanded = false
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSearch(
                            WallhavenFilter(
                                    query = query,
                                    categories = categories,
                                    purity = purity,
                                    atleast = atleast,
                                    resolution = resolution,
                                    ratios = ratios,
                                    sorting = sorting,
                                    order = order
                            )
                    )
                    onDismiss()
                }) {
                    Text(stringResource(R.string.search))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
    )
}