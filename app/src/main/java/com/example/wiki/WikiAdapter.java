package com.example.wiki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WikiAdapter extends RecyclerView.Adapter<WikiAdapter.CustomViewHolder> {

    private final static String TAG = WikiAdapter.class.getSimpleName() + "_fatal";

    public interface ClickInterface {
        void handleClick(Page queryDetail);
    }

    private List<Page> queryList;
    private Context context;
    private ClickInterface mClickInterface;

    public WikiAdapter(Context context, List<Page> queryList, ClickInterface clickInterface) {
        this.context = context;
        this.queryList = queryList;
        mClickInterface = clickInterface;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgImage;
        final View mView;
        RelativeLayout itemContainer;
        TextView desc;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            txtName = mView.findViewById(R.id.title);
            imgImage = mView.findViewById(R.id.image);
            itemContainer = mView.findViewById(R.id.item_container);
            desc = mView.findViewById(R.id.desc);
        }
    }

    @Override @NonNull
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View returnView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new CustomViewHolder(returnView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        final Page page = queryList.get(position);
        holder.txtName.setText(page.getTitle());

        if(page.getTerms() != null && page.getTerms().getDescription().size() > 0) {
            holder.desc.setText(page.getTerms().getDescription().get(0));
            holder.desc.setVisibility(View.VISIBLE);
        } else {
            holder.desc.setVisibility(View.GONE);
        }

        if (page.getThumbnail() != null && page.getThumbnail().getSource() != null && !page.getThumbnail().getSource().isEmpty()) {

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context));

            builder.build().load(page.getThumbnail().getSource())
                    .placeholder((R.drawable.ic_launcher_background))
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgImage);
        } else {
            holder.imgImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_background));
        }

        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickInterface.handleClick(page);
            }
        });

    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }
}
