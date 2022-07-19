@file:OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)

package ru.fitsuli.developerslifeviewer.screens

import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.fitsuli.developerslifeviewer.R
import ru.fitsuli.developerslifeviewer.utils.DLifeData
import ru.fitsuli.developerslifeviewer.utils.Result
import ru.fitsuli.developerslifeviewer.utils.Utils.Companion.Pages
import ru.fitsuli.developerslifeviewer.utils.downloadJsonStr
import ru.fitsuli.developerslifeviewer.utils.shareLink


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    var verticalSwipeEnabled by remember {
        mutableStateOf(
            prefs.getBoolean("vertical_swipe_on", false)
        )
    }
    val screenPager = rememberPagerState()
    val pages = listOf(
        stringResource(id = R.string.latest), stringResource(id = R.string.random),
        stringResource(id = R.string.top), stringResource(id = R.string.hot)
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                SmallTopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = {
                            verticalSwipeEnabled = !verticalSwipeEnabled
                            prefs.edit { putBoolean("vertical_swipe_on", verticalSwipeEnabled) }
                        }) {
                            AnimatedContent(targetState = verticalSwipeEnabled) {
                                Icon(
                                    painter = painterResource(
                                        id = if (it) R.drawable.ic_round_swipe_24
                                        else R.drawable.ic_round_swipe_vertical_24
                                    ), contentDescription = "Swipe switcher"
                                )
                            }
                        }
                    }
                )

                TabRow(
                    selectedTabIndex = screenPager.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(screenPager, tabPositions)
                        )
                    },
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    pages.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(text = title) },
                            selected = screenPager.currentPage == index,
                            onClick = {
                                scope.launch {
                                    screenPager.animateScrollToPage(index)
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            count = 4, state = screenPager,
            itemSpacing = 8.dp,
        ) { pageNum ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                val pagerState = rememberPagerState()
                val webPageId = remember { mutableStateOf(0) }
                val resultList = remember {
                    mutableStateListOf<Result>()
                }

                AnimatedContent(
                    targetState = verticalSwipeEnabled,
                    modifier = Modifier
                        .weight(5f)
                ) {
                    if (it) {
                        VerticalPager(
                            count = resultList.size, state = pagerState,
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) { page ->
                            Page(page, resultList)
                        }
                    } else {
                        HorizontalPager(
                            count = resultList.size, state = pagerState,
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) { page ->
                            Page(page, resultList)
                        }
                    }
                }


                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    NavigationButtons(
                        modifier = Modifier,
                        pagerState, verticalSwipeEnabled,
                        resultList
                    )
                }

                LaunchedEffect(Unit) {
                    autoAddNewData(context, pageNum, pagerState, webPageId, resultList)
                }
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    verticalSwipeEnabled: Boolean,
    resultList: SnapshotStateList<Result>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Surface(
        modifier = modifier,
        tonalElevation = 1.dp,
        shape = CircleShape
    ) {

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            val isEnabledPrev by remember { derivedStateOf { pagerState.currentPage > 0 && pagerState.targetPage > 0 } }
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = (pagerState.currentPage - 1).coerceAtLeast(
                                0
                            )
                        )
                    }
                },
                enabled = isEnabledPrev
            ) {
                AnimatedContent(targetState = verticalSwipeEnabled) {
                    Icon(
                        imageVector = if (it) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "Go back",
                    )
                }
            }

            IconButton(onClick = {
                context.shareLink("https://developerslife.ru/${resultList[pagerState.currentPage].id}")
            }) {
                Icon(imageVector = Icons.Rounded.Share, contentDescription = "Share")
            }

            val isEnabledNext by remember { derivedStateOf { pagerState.pageCount != 0 && pagerState.currentPage != pagerState.pageCount - 1 } }
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = (pagerState.currentPage + 1).coerceAtMost(
                                pagerState.pageCount - 1
                            )
                        )
                    }
                },
                enabled = isEnabledNext
            ) {
                AnimatedContent(targetState = verticalSwipeEnabled) {
                    Icon(
                        imageVector = if (it) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowRight,
                        contentDescription = "Go forward"
                    )
                }
            }
        }
    }
}

@Composable
private fun Page(
    page: Int,
    resultList: SnapshotStateList<Result>
) {
    val context = LocalContext.current
    val coilLoader = remember {
        ImageLoader.invoke(context).newBuilder()
            .componentRegistry {
                add(
                    if (Build.VERSION.SDK_INT >= 28) {
                        ImageDecoderDecoder(
                            context,
                            enforceMinimumFrameDelay = true
                        )
                    } else {
                        GifDecoder()
                    }
                )
            }
            .build()
    }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Column {

            CoilGifImage(
                imageLoader = coilLoader,
                gifUrl = resultList[page].gifURL.replaceFirst(
                    "http",
                    "https"
                ), // I wonder how, I wonder why
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = resultList[page].description,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        4.dp,
                        alignment = Alignment.Bottom
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = resultList[page].author,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "${resultList[page].votes} ${stringResource(id = R.string.votes)}",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

        }
    }
}

private tailrec suspend fun autoAddNewData(
    context: Context,
    page: Int,
    pagerState: PagerState,
    pageItemToOpen: MutableState<Int>,
    results: SnapshotStateList<Result>
) {
    if (pagerState.pageCount == 0 ||
        (page != Pages.Random && pagerState.targetPage == pagerState.pageCount - 2) ||
        pagerState.targetPage == pagerState.pageCount - 1
    ) {
        context.getAndAddUrls(pageItemToOpen, page, results)
        delay(300)
    }
    delay(300)
    autoAddNewData(context, page, pagerState, pageItemToOpen, results)
}

private suspend fun Context.getAndAddUrls(
    pageToOpen: MutableState<Int>,
    page: Int,
    results: SnapshotStateList<Result>
) {
    val downloadedJson =
        downloadJsonStr(
            if (page == Pages.Random) {
                "https://developerslife.ru/random?json=true"
            } else {
                "https://developerslife.ru/${
                    when (page) {
                        Pages.Hot -> "hot" // 
                        Pages.Latest -> "latest"
                        else -> "top"
                    }
                }/${pageToOpen.value}?json=true&pageSize=10"
            }
        )
            ?: return

    if (page == Pages.Random) {
        val dldata = runCatching {
            Json.decodeFromString<Result>(downloadedJson)
        }.getOrNull()
            ?: return

        results += dldata
    } else {
        val dldata = runCatching {
            Json.decodeFromString<DLifeData>(downloadedJson)
        }.getOrNull()
            ?: return

        results += dldata.result
        pageToOpen.value++
    }
}

@Composable
private fun CoilGifImage(modifier: Modifier = Modifier, imageLoader: ImageLoader, gifUrl: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(
                data = gifUrl,
                imageLoader = imageLoader,
                builder = {
//                    scale(Scale.FILL)
                    crossfade(true)
                    placeholder(R.drawable.ic_round_downloading_48)
                    error(R.drawable.ic_round_error_outline_48)
                },
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}