package com.example.shaunrain.gesturelock.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shaunrain.gesturelock.R;
import com.example.shaunrain.gesturelock.model.ImageModel;
import com.example.shaunrain.gesturelock.model.MediaModel;
import com.example.shaunrain.gesturelock.ui.HideMediaActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ShaunRain on 16/2/21.
 */
public class ImageHideAdapter extends RecyclerView.Adapter<ImageHideAdapter.ImageViewHolder> implements HideAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ImageModel> mDatas;
    private boolean isEdit;

    private HashMap<Integer, Boolean> pickDict;

    private RecyclerView recyclerView;

    public ImageHideAdapter(Context mContext, List<ImageModel> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
        pickDict = new HashMap<>();
    }

    public void addItem(MediaModel item) {
        Log.d("fromid", "add!" + item.getName());
        mDatas.add((ImageModel) item);
        notifyDataSetChanged();
    }

    public void addItems(MediaModel... items) {
        for (MediaModel item : items) {
            mDatas.add((ImageModel) item);
        }
        notifyDataSetChanged();
    }

    public void editMode() {
        isEdit = true;
        notifyDataSetChanged();
    }

    public void uneditMode() {
        isEdit = false;
        pickDict.clear();
        notifyDataSetChanged();
    }

    public void recoverItems() {
        if (!pickDict.isEmpty()) {
            for (int position : pickDict.keySet()) {
                boolean success = HideMediaActivity.mediaUtil.unhideMedia(mDatas.get(position));
                mDatas.remove(position);
                Log.d("Unhide", "" + success);
            }
            pickDict.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_hide_image, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        viewHolder.setClickable(isEdit);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.setClickable(isEdit);
        if (pickDict.containsKey(position))
            holder.setPick();
        holder.image_content.setImageDrawable(Drawable.createFromPath(mDatas.get(position).getThumbPath()));
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

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

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


