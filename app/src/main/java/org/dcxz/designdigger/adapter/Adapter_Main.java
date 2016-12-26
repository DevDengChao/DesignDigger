package org.dcxz.designdigger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dcxz.designdigger.App;
import org.dcxz.designdigger.R;
import org.dcxz.designdigger.entity.Entity_Shot;
import org.dcxz.designdigger.framework.Framework_Adapter;
import org.dcxz.designdigger.view.AutoHeightGifImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifDrawable;

/**
 * <br/>
 * Created by DC on 2016/12/17.<br/>
 */

public class Adapter_Main extends Framework_Adapter<Entity_Shot> {
    /**
     * 请求标签
     */
    public static final String TAG = "Adapter_Main";
    private final Handler handler;
    private final File cacheDir;

    /**
     * @param context 用于初始化{@link #inflater}的上下文
     * @param data    将要被适配的数据集合
     */
    public Adapter_Main(Context context, ArrayList<Entity_Shot> data) {
        super(context, data);
        handler = new Handler();
        cacheDir = context.getCacheDir();
    }

    @Override
    protected View getViewImp(int position, View convertView, ViewGroup parent) {
        Entity_Shot temp = data.get(position);
        ViewHolder holder;
        if (convertView == null) {// TODO: 2016/12/18 优化:Item布局
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            //检查该对象是否已经显示,若已经显示则直接返回,否则进行初始化.
            //用于优化GirdView在更新数据集合过程中出现的闪烁现象.
            if (temp.getImages().getNormal().equals(holder.content.getTag())) {
                return convertView;
            }
        }
        initView(holder, temp);
        return convertView;
    }

    /**
     * 将ViewHolder与Entity_Shot对象组装起来
     *
     * @param holder 持有控件的ViewHolder
     * @param temp   持有数据的Entity_Shot
     */
    @SuppressLint("SetTextI18n")
    private void initView(final ViewHolder holder, final Entity_Shot temp) {
        holder.avatar.setImageResource(R.drawable.progress_rotate);//使用图像占位,避免重用过程中出现图像突变现象
        App.imageRequest(temp.getUser().getAvatar_url(), holder.avatar, TAG);

        holder.content.setImageResource(R.mipmap.item_content);
        holder.content.setTag(temp.getImages().getNormal());
        if (temp.isAnimated()) {// TODO: 2016/12/26 GIF
            new Thread() {
                @Override
                public void run() {
                    try {
                        String path = temp.getImages().getNormal();
                        HttpsURLConnection connection = (HttpsURLConnection) new URL(path).openConnection();
                        connection.setConnectTimeout(10000);
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            final File file = new File(cacheDir, path.substring(path.lastIndexOf("/") + 1));
                            if (!file.exists()) {
                                if (file.createNewFile()) {
                                    InputStream inputStream = connection.getInputStream();
                                    FileOutputStream outputStream = new FileOutputStream(file);
                                    byte cache[] = new byte[1024];
                                    int length;
                                    while ((length = inputStream.read(cache)) != -1) {
                                        outputStream.write(cache, 0, length);
                                    }
                                    outputStream.flush();
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {//not in GIF format
                                        final GifDrawable gifDrawable = new GifDrawable(file);
                                        holder.content.setImageDrawable(gifDrawable);
                                        holder.gif.setVisibility(View.VISIBLE);
                                        holder.gif.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (gifDrawable.isPlaying()) {
                                                    Log.i(TAG, "onClick: gifDrawable is playing, now stop");
                                                    gifDrawable.pause();
                                                } else {
                                                    Log.i(TAG, "onClick: gifDrawable is stopped, now playing");
                                                    gifDrawable.start();
                                                }
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            /*App.stringRequest(temp.getImages().getNormal(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    holder.gif.setVisibility(View.VISIBLE);
                    byte[] cache = response.getBytes();
                    try {//No frames found, at least one frame required
                        final GifDrawable gifDrawable = new GifDrawable(cache);
                        holder.content.setImageDrawable(gifDrawable);
                        holder.gif.setVisibility(View.VISIBLE);
                        holder.gif.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (gifDrawable.isPlaying()) {
                                    Log.i(TAG, "onClick: gifDrawable is playing, now stop");
                                    gifDrawable.pause();
                                } else {
                                    Log.i(TAG, "onClick: gifDrawable is stopped, now playing");
                                    gifDrawable.start();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, null, TAG);*/
            /*App.getQueue().add(new ImageRequest(
                    temp.getImages().getNormal(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            holder.gif.setVisibility(View.VISIBLE);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
                            response.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            try {//not in GIF format
                                final GifDrawable gifDrawable = new GifDrawable(outputStream.toByteArray());
                                holder.content.setImageDrawable(gifDrawable);
                                holder.gif.setVisibility(View.VISIBLE);
                                holder.gif.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (gifDrawable.isPlaying()) {
                                            Log.i(TAG, "onClick: gifDrawable is playing, now stop");
                                            gifDrawable.pause();
                                        } else {
                                            Log.i(TAG, "onClick: gifDrawable is stopped, now playing");
                                            gifDrawable.start();
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, null
            ));*/
        } else {
            App.imageRequest(temp.getImages().getNormal(), holder.content, TAG);
            holder.gif.setVisibility(View.INVISIBLE);
            holder.gif.setOnClickListener(null);
        }
        if (temp.getRebounds_count() == 0) {
            holder.rebound.setVisibility(View.INVISIBLE);
        } else {
            holder.rebound.setVisibility(View.VISIBLE);
            holder.attachment.setText(temp.getRebounds_count() + "");
        }
        if (temp.getAttachments_count() == 0) {
            holder.attachment.setVisibility(View.INVISIBLE);
        } else {
            holder.attachment.setVisibility(View.VISIBLE);
            holder.attachment.setText(temp.getAttachments_count() + "");
        }
        holder.view.setText(temp.getViews_count() + "");// 1 -> "1"
        holder.comment.setText(temp.getComments_count() + "");
        holder.like.setText(temp.getLikes_count() + "");
        holder.userName.setText(temp.getUser().getUsername());
        holder.title.setText(temp.getTitle());
        holder.time.setText(temp.getCreated_at());
    }

    /**
     * 静态内部类,避免引用
     */
    private static class ViewHolder {
        CircleImageView avatar;
        AutoHeightGifImageView content;
        ImageView gif;

        TextView rebound;
        TextView attachment;

        TextView view;
        TextView comment;
        TextView like;

        TextView userName;
        TextView title;
        TextView time;

        ViewHolder(View convertView) {
            avatar = (CircleImageView) convertView.findViewById(R.id.item_avatar);
            content = (AutoHeightGifImageView) convertView.findViewById(R.id.item_content);
            gif = (ImageView) convertView.findViewById(R.id.item_gif);
            rebound = (TextView) convertView.findViewById(R.id.item_rebound);
            attachment = (TextView) convertView.findViewById(R.id.item_attachment);
            view = (TextView) convertView.findViewById(R.id.item_view);
            comment = (TextView) convertView.findViewById(R.id.item_comment);
            like = (TextView) convertView.findViewById(R.id.item_like);

            userName = (TextView) convertView.findViewById(R.id.item_userName);
            title = (TextView) convertView.findViewById(R.id.item_title);
            time = (TextView) convertView.findViewById(R.id.item_time);
        }
    }
}
