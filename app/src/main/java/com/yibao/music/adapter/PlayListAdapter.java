package com.yibao.music.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.MusicApplication;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.ItemPlayListBinding;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicDaoUtil;
import com.yibao.music.util.RxBus;

import java.util.List;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： {TODO}
 */

public class PlayListAdapter extends BaseBindingAdapter<PlayListBean> {
    private SparseBooleanArray mCheckedBoxMap;

    public PlayListAdapter(List<PlayListBean> list, SparseBooleanArray checkedBoxMap) {
        super(list);
        mCheckedBoxMap = checkedBoxMap;
    }


    @Override
    public void bindView(RecyclerView.ViewHolder holder, PlayListBean playListBean) {

        if (holder instanceof PlayViewHolder) {
            PlayViewHolder playViewHolder = (PlayViewHolder) holder;
//            playViewHolder.mBinding.checkboxItem.setVisibility(isSelectStatus() ? View.VISIBLE : View.GONE);
//            playViewHolder.mBinding.ivItemEdit.setVisibility(isSelectStatus() ? View.VISIBLE : View.GONE);
//            playViewHolder.mBinding.ivItemArrow.setVisibility(isSelectStatus() ? View.GONE : View.VISIBLE);
            playViewHolder.mBinding.tvPlayListName.setText(playListBean.getTitle());
            List<MusicBean> musicBeans = MusicApplication.getInstance().getMusicDao().queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle())).build().list();
            String count = musicBeans.size() + " 首歌曲";
            playViewHolder.mBinding.tvPlayListCount.setText(count);
            int adapterPosition = playViewHolder.getAdapterPosition();
            playViewHolder.mBinding.checkboxItem.setChecked(mCheckedBoxMap.get(adapterPosition));
            playViewHolder.mBinding.rlPlayListItem.setOnClickListener(view -> {
                if (isSelectStatus()) {
                    checkBoxClick(playListBean, adapterPosition, playViewHolder.mBinding.checkboxItem.isChecked());
                    PlayListAdapter.this.openDetails(playListBean, adapterPosition, true);
                } else {
                    PlayListAdapter.this.openDetails(playListBean, adapterPosition, false);
                }
            });
            playViewHolder.mBinding.playListItemSlide.setOnClickListener(v -> {
                getDataList().remove(adapterPosition);
                MusicDaoUtil.setMusicListFlag(playListBean);
                RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_TWO));
            });
            playViewHolder.mBinding.checkboxItem.setOnClickListener(v -> checkBoxClick(playListBean, adapterPosition, playViewHolder.mBinding.checkboxItem.isChecked()));

            playViewHolder.mBinding.ivItemEdit.setOnClickListener(v -> editItmeTitle(adapterPosition));
            playViewHolder.mBinding.rlPlayListItem.setOnLongClickListener(v -> {
                deletePlaylist(playListBean, adapterPosition);
                return true;
            });
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemPlayListBinding binding = ItemPlayListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PlayViewHolder(binding);
    }


    static class PlayViewHolder extends RecyclerView.ViewHolder {

        ItemPlayListBinding mBinding;

        PlayViewHolder(ItemPlayListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

        }
    }

    @Override
    protected String getLastItemDes() {
        return " 个播放列表";
    }
}
