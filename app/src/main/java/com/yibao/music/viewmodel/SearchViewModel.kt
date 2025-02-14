package com.yibao.music.viewmodel

import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.MusicBean
import com.yibao.music.model.SearchHistoryBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.model.greendao.SearchHistoryBeanDao
import com.yibao.music.util.LogUtil

class SearchViewModel : BaseViewModel() {

    val searchViewModel = SingleLiveEvent<List<MusicBean>>()


    fun searchMusic(key: String, position: Int) {

        val musicDao = MusicApplication.getInstance().musicDao

        LogUtil.d("====", "=========== searchPosition    $position  searchKey  $key");
        val dataList = ArrayList<MusicBean>()
        // 全部
        when (position) {

            11 -> {
                // 根据歌名精确搜索
                val songList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(key)).build()
                        .list()
                if (songList.isNotEmpty()) {
                    dataList.addAll(songList)
                } else {
                    // 模糊匹配搜索, % 加在前面为包含key，加在后面查询的结果是以 key 开头的数据。
                    val searchSongList: List<MusicBean> = musicDao.queryBuilder().where(
                        MusicBeanDao.Properties.Title.like("%$key%")
                    ).list()
                    if (searchSongList.isNotEmpty()) {
                        dataList.addAll(searchSongList)
                    }
                }
            }
            12 -> {
                // 按专辑搜索
                val albumList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(key)).build()
                        .list()
                if (albumList.isNotEmpty()) {
                    dataList.addAll(albumList)
                }
            }
            13 -> {
                // 按歌手搜索
                val artistList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build()
                        .list()
                if (artistList.isNotEmpty()) {
                    dataList.addAll(artistList)
                }
            }
           14 -> {
                // 按歌手搜索
                val artistList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build()
                        .list()
                if (artistList.isNotEmpty()) {
                    dataList.addAll(artistList)
                }
            }
        }

        searchViewModel.postValue(dataList)
    }

    val historyViewModel = SingleLiveEvent<List<SearchHistoryBean>>()

    fun getHistory() {
        val hisDao = MusicApplication.getInstance().searchDao
        val historyList =
            hisDao.queryBuilder().orderDesc(SearchHistoryBeanDao.Properties.SearchTime).build()
                .list()
        val hisList = historyList.distinctBy { it.searchContent }

        historyViewModel.postValue(hisList)
    }

}