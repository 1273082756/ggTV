package com.example.luke.testtv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    /**
     * 定义点击事件接口
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnItemLongClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    /**
     * 找到电影行对应的xml
     */
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View container = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.movie_view, viewGroup, false);
        return new MovieViewHolder(container);
    }

    /**
     * 填充每一行内容
     */
    public void onBindViewHolder(MovieViewHolder movieViewHolder, final int i) {
        movieViewHolder.bind(MovieLab.get().getTv(i),i);
        movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(i);
                }
            }
        });
        movieViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longListener != null) {
                    longListener.onClick(i);
                }
                return true;
            }
        });
    }

    public int getItemCount() {
        return MovieLab.get().getSize();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView moveName;
        private ImageView imageView;
        private int[] img={
                R.drawable.c1,
                R.drawable.c2,
                R.drawable.c3,
                R.drawable.c4,
                R.drawable.c5,
                R.drawable.c6,
        };
        public MovieViewHolder(View container) {
            super(container);
            moveName = container.findViewById(R.id.move_Name);
            imageView = container.findViewById(R.id.move_img);
        }

        public void bind(String moveName,int i) {
            this.moveName.setText(moveName);
            this.imageView.setImageResource(img[i]);
        }
    }
}
