package com.meishe.cutsame.fragment.presenter;


import com.meishe.cutsame.fragment.iview.MediaView;
import com.meishe.base.bean.MediaData;
import com.meishe.base.bean.MediaSection;
import com.meishe.base.bean.MediaTag;
import com.meishe.base.model.Presenter;
import com.meishe.base.utils.MediaUtils;
import com.meishe.base.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * author : lhz
 * date   : 2020/8/31
 * desc   :媒体列表逻辑处理类Presenter
 * Media list logic processing class Presenter
 */
public class MediaPresenter extends Presenter<MediaView> {
    private List<MediaData> mSelectedList;

    /**
     * 获取媒体列表
     * <p></p>
     * Get media list
     *
     * @param type TYPE_VIDEO = 0; 视频 TYPE_PHOTO = 1;图片;YPE_ALL = 2;图片和视频
     */
    public void getMediaList(final int type) {
        MediaUtils.getMediaList(type, new MediaUtils.MediaCallback() {
            @Override
            public void onResult(List<MediaData> dataList) {
                List<MediaSection> list = new ArrayList<>();
                String lastDate = null;
                String date;
                int index = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    MediaData mediaData = dataList.get(i);
                    if (mediaData == null) {
                        continue;
                    }
                    date = TimeUtils.millis2String(mediaData.getDate(), "yyyy-MM-dd");
                    if (lastDate == null || !lastDate.equals(date)) {
                        lastDate = TimeUtils.millis2String(mediaData.getDate(), "yyyy-MM-dd");
                        MediaSection head = new MediaSection(null);
                        head.isHeader = true;
                        head.header = date;
                        index++;
                        list.add(head);
                    }
                    list.add(new MediaSection(mediaData));
                    MediaTag mediaTag = new MediaTag();
                    mediaTag.setIndex(index).setType(type);
                    mediaData.setTag(mediaTag);
                    index++;
                }
                if (getView() != null) {
                    getView().onMediaBack(list);
                }
            }
        });
    }

    /**
     * 处理媒体被选中
     * <p></p>
     * The processing media is selected
     *
     * @param item     the item条目
     * @param position the position 位置
     */
    public void dealSelected(MediaData item, int position) {
        dealSelected(item, position, true);
    }

    /**
     * 处理媒体被选中
     * <p></p>
     * The processing media is selected
     *
     * @param item            the item  选中的媒体数据信息
     * @param position        the position 位置
     * @param useSelectedList the use selected list 使用选择列表
     */
    public void dealSelected(MediaData item, int position, boolean useSelectedList) {
        item.setState(!item.isState());
        if (useSelectedList && mSelectedList == null) {
            mSelectedList = new ArrayList<>();
        }
        if (item.isState()) {
            if (useSelectedList) {
                mSelectedList.add(item);
                item.setPosition(mSelectedList.size() + 1);
            }
            getView().onItemChange(position);
        } else {
            if (useSelectedList) {
                removeItem(item);
            } else {
                getView().onItemChange(position);
            }
        }
    }

    /**
     * 移除选中的item
     * Remove the selected item
     */
    private void removeItem(MediaData item) {
        int deleteIndex = 0;
        for (int i = 0; i < mSelectedList.size(); i++) {
            MediaData mediaData = mSelectedList.get(i);
            if (item.getId() == mediaData.getId()) {
                mSelectedList.remove(i);
                --i;
                deleteIndex = i;
            }
            if (i >= deleteIndex) {
                mediaData.setPosition(i + 1);
                Object tag = mediaData.getTag();
                if (tag != null) {
                    getView().onItemChange(((MediaTag) tag).getIndex());
                }
            }
        }
    }

    /**
     * 获取选中的媒体集合
     * <p></p>
     * Gets the selected media collection
     *
     * @return the selected list 选择集合
     */
    public List<MediaData> getSelectedList() {
        return mSelectedList;
    }
}
