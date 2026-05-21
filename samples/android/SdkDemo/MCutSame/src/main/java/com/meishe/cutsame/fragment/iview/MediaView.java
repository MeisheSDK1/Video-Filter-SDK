package com.meishe.cutsame.fragment.iview;



import com.meishe.base.bean.MediaSection;
import com.meishe.base.model.IBaseView;

import java.util.List;

/**
 * author : lhz
 * date   : 2020/8/31
 * desc   :媒体列表View
 * Media List View
 */
public interface MediaView extends IBaseView {

    void onMediaBack(List<MediaSection> mediaData);

    void onItemChange(int position);
}
