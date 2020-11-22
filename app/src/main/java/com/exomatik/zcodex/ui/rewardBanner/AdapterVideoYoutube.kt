package com.exomatik.zcodex.ui.rewardBanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.exomatik.zcodex.R
import com.exomatik.zcodex.model.ModelVideoYoutube
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailLoader.OnThumbnailLoadedListener
import com.google.android.youtube.player.YouTubeThumbnailView

class AdapterVideoYoutube(context: Context, var dataVideo: List<ModelVideoYoutube>) :
    RecyclerView.Adapter<AdapterVideoYoutube.VideoInfoHolder>() {
    var ctx: Context = context
    private val videoCode = "XfP31eWXli4"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoInfoHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_video_youtube, parent, false)
        return VideoInfoHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoInfoHolder, position: Int) {
        val onThumbnailLoadedListener: OnThumbnailLoadedListener =
            object : OnThumbnailLoadedListener {
                override fun onThumbnailError(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    errorReason: YouTubeThumbnailLoader.ErrorReason) {

                }

                override fun onThumbnailLoaded(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    s: String
                ) {
                    youTubeThumbnailView.visibility = View.VISIBLE
                    holder.relativeLayoutOverYouTubeThumbnailView.visibility = View.VISIBLE
                }
            }
        holder.textTitle.text = dataVideo[position].title

        holder.imgYoutube.setOnClickListener {
            ctx.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("vnd.youtube://" + dataVideo[position].urlVideo)
                )
            )
        }
        holder.youTubeThumbnailView.initialize(
            videoCode,
            object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    youTubeThumbnailLoader: YouTubeThumbnailLoader
                ) {
                    youTubeThumbnailLoader.setVideo(dataVideo[position].urlVideo)
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener)
                }

                override fun onInitializationFailure(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    //write something for failure
                }
            })
    }

    override fun getItemCount(): Int {
        return dataVideo.size
    }

    inner class VideoInfoHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var relativeLayoutOverYouTubeThumbnailView: RelativeLayout
        var youTubeThumbnailView: YouTubeThumbnailView
        var textTitle: AppCompatTextView
        var imgYoutube: AppCompatImageView
        private var playButton: ImageView = itemView.findViewById<View>(R.id.btnYoutube_player) as ImageView
        override fun onClick(v: View) {
            val intent = YouTubeStandalonePlayer.createVideoIntent(
                ctx as Activity,
                videoCode,
                dataVideo[layoutPosition].urlVideo
            )
            ctx.startActivity(intent)
        }

        init {
            textTitle = itemView.findViewById<View>(R.id.text_title) as AppCompatTextView
            imgYoutube = itemView.findViewById<View>(R.id.img_youtube) as AppCompatImageView

//            textTitle.setText(dataVideo.get(getLayoutPosition()).title());
            playButton.setOnClickListener(this)
            relativeLayoutOverYouTubeThumbnailView =
                itemView.findViewById<View>(R.id.relativeLayout_over_youtube_thumbnail) as RelativeLayout
            youTubeThumbnailView =
                itemView.findViewById<View>(R.id.youtube_thumbnail) as YouTubeThumbnailView
        }
    }

}