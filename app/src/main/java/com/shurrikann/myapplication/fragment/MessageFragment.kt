package com.shurrikann.myapplication.fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shurrikann.myapplication.base.BaseFragment
import com.shurrikann.myapplication.databinding.FragmentMessageBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageFragment : BaseFragment<FragmentMessageBinding>(FragmentMessageBinding::inflate) {
    override fun initView() {
        binding.compress.setContent {
            MaterialTheme {
                MessageListScreen(
                    onItemClick = { message ->
                        showToast("点击了:$message")
                    },
                    onShowToast = { text ->
                        showToast(text)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(onItemClick: (String) -> Unit, onShowToast: (String) -> Unit) {
    //1.状态管理
    val messages = remember { mutableStateListOf(*(List(20) { "消息内容$it" }).toTypedArray()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMore by remember { mutableStateOf(true) }//是否还有更多数据的开关
    val listState = rememberLazyListState()
    //2.模拟下拉刷新的逻辑
    val scope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        scope.launch {
            delay(2000)
            messages.clear()
            messages.addAll(List(20) {
                "刷新的消息 $it"
            })
            hasMore = true //刷新后重置为加载更多
            isRefreshing = false
        }
    }
    //3.监听上拉加载
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2 && hasMore && !isRefreshing//增加判断条件
        }
    }
    //触发上拉加载逻辑
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoadingMore) {
            try {
                isLoadingMore = true
                delay(1500)
                //模拟加载更多
                val currentSize = messages.size
                if (currentSize >= 50) {
                    hasMore = false
                } else {
                    messages.addAll(List(10) {
                        "更多消息:${currentSize + it}"
                    })
                }
            } catch (e: Exception) {
                onShowToast("加载失败")
            } finally {
                isLoadingMore = false
            }
        }
    }
    //4UI布局
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(messages) { msg ->
                MessageRow(msg, onItemClick)
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            }
            //5底部加载进度条
            if (messages.size > 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasMore) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "___我是有底线的___",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageRow(text: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(text)
            }
            .padding(16.dp)
    ) {
        Text(text = text, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}