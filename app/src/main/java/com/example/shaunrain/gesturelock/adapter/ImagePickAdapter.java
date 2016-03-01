package com.example.shaunrain.gesturelock.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.model.ImageModel;
import com.example.shaunrain.gesturelock.ui.HidePickActivity;
import com.example.shaunrain.gesturelock.utils.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ShaunRain on 16/2/22.
 */
public class ImagePickAdapter extends RecyclerView.Adapter<ImagePickAdapter.ImageViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Integer> mDatas;

    public HashMap<Integer, Boolean> pickDict;

    private RecyclerView recyclerView;

    private Toolbar toolbar;


    public ImagePickAdapter(Context mContext, List<Integer> mDatas, android.support.v7.widget.Toolbar toolbar) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.toolbar = toolbar;
        mInflater = LayoutInflater.from(mContext);
        pickDict = new HashMap<>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_hide_image, parent, false);
        ImageViewHolder holder = new ImageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if (pickDict.containsKey(position) && pickDict.get(position) == true)
            holder.setPick();
        holder.image_content.setImageBitmap(ImageUtil.getThumb(mContext.getContentResolver(), mDatas.get(position)));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                //将回收重用的ViewHolder 初始化
                ((ImageViewHolder) holder).setUnpick();
            }
        });
    }

    public ArrayList<ImageModel> getPickList() {
        ArrayList<ImageModel> pickList = new ArrayList<>();
        for (Integer position : pickDict.keySet()) {
            pickList.add(((ImageUtil) HidePickActivity.mediaUtil).getImageModelFromId(mDatas.get(position)));
        }
        return pickList;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


//    @Override
//    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
//        if (mDatas != null) {
//            for (int i = 0; i < mDatas.size(); i++) {
//                Bitmap bitmap;
//                if ((bitmap = mDatas.get(i)) != null)
//                    bitmap.recycle();
//            }
//            System.gc();
//            mDatas = null;
//        }
//        super.onDetachedFromRecyclerView(recyclerView);
//    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        CardView image_card;
        ImageView image_content;
        ImageView image_filter;
        boolean pick;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image_card = (CardView) itemView.findViewById(R.id.image_card);
            image_filter = (ImageView) itemView.findViewById(R.id.image_filter);
            image_content = (ImageView) itemView.findViewById(R.id.image_view);
            image_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (!pick) {
                        setPick();
                        pickDict.put(position, true);
                    } else {
                        setUnpick();
                        pickDict.remove(position);
                    }
                    toolbar.setTitle("已选择: " + pickDict.keySet().size());
                }
            });
        }

        public void setPick() {
            pick = true;
            image_filter.setAlpha(0.5f);
        }

        public void setUnpick() {
            pick = false;
            image_filter.setAlpha(0.0f);
        }

        public void setClickable(boolean clickable) {
            image_card.setClickable(clickable);
        }

    }
}

