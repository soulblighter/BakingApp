package br.com.soulblighter.bakingapp.ui;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class RecipePlayer {

    private SimpleExoPlayer mExoPlayer;
    private Context mContext;

    public RecipePlayer(Context context) {
        mContext = context;
    }

    public void attath2view(SimpleExoPlayerView view) {
        view.setPlayer(mExoPlayer);
    }

    /**
     * Initialize ExoPlayer.
     */
    public void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        }
    }

    /**
     * Prepare ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    public void prepare(Uri mediaUri) {
        if (mExoPlayer != null) {
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mContext, mContext.getApplicationInfo().name);
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(mContext,
                userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
        }
    }

    public void play() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * Release ExoPlayer.
     */
    public void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }
}
