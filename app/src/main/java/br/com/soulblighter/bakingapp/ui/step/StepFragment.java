package br.com.soulblighter.bakingapp.ui.step;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.squareup.picasso.Picasso;

import br.com.soulblighter.bakingapp.R;
import br.com.soulblighter.bakingapp.data.Recipe;
import br.com.soulblighter.bakingapp.data.Step;
import br.com.soulblighter.bakingapp.ui.RecipePlayer;
import br.com.soulblighter.bakingapp.ui.recipe.RecipeActivity;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link StepActivity}
 * on handsets.
 */
public class StepFragment extends Fragment {

    private SimpleExoPlayerView mPlayerView;
    private Recipe mItem;
    private RecipePlayer mRecipePlayer;
    private long mPlayerPosition = C.TIME_UNSET;

    private final String SELECTED_POSITION = "player_position";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepFragment() {
    }

    public static StepFragment newInstance(Recipe recipe, int position) {
        StepFragment fragment = new StepFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(StepActivity.ARG_ITEM, recipe);
        arguments.putInt(StepActivity.ARG_POS, position);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(StepActivity.ARG_ITEM)) {
            mItem = getArguments().getParcelable(StepActivity.ARG_ITEM);
        } else {
            getActivity().finish();
        }

        if (savedInstanceState != null) {
            long pos = savedInstanceState.getLong(SELECTED_POSITION, C.TIME_UNSET);
            if(pos != C.TIME_UNSET) {
                mPlayerPosition = pos;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        int pos = getArguments().getInt(StepActivity.ARG_POS);
        Recipe item = getArguments().getParcelable(StepActivity.ARG_ITEM);

        rootView = inflater.inflate(R.layout.step_fragment, container, false);
        Step step = mItem.steps.get(pos - 1);

        final ImageView imageView = rootView.findViewById(R.id.imageView);
        if (step.thumbnailURL != null && !step.thumbnailURL.isEmpty()) {
            Picasso.with(getActivity()).load(step.thumbnailURL).fit().into(imageView, new com.squareup.picasso
                .Callback() {
                @Override
                public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    imageView.setVisibility(View.GONE);
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }

        mPlayerView = rootView.findViewById(R.id.videoView);

        String url = item.steps.get(pos - 1).videoURL;
        if (url != null && !url.isEmpty()) {
            mPlayerView.setVisibility(View.VISIBLE);
            mRecipePlayer = new RecipePlayer(getActivity());
            mRecipePlayer.initializePlayer();
            mRecipePlayer.attath2view(mPlayerView);
            mPlayerView.setControllerShowTimeoutMs(1000);
            if (mPlayerPosition != C.TIME_UNSET) {
                mRecipePlayer.seekTo(mPlayerPosition);
            }
            mRecipePlayer.prepare(Uri.parse(url));
            if (getUserVisibleHint()) {
                mRecipePlayer.play();
                mPlayerView.hideController();
            }
        } else {
            mPlayerView.setVisibility(View.GONE);
        }

        ((TextView) rootView.findViewById(R.id.description)).setText(step.description);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPlayerPosition != C.TIME_UNSET) {
            outState.putLong(SELECTED_POSITION, mPlayerPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (mRecipePlayer != null) {
            mPlayerPosition = mRecipePlayer.getCurrentPosition();
            mRecipePlayer.stop();
            mRecipePlayer.releasePlayer();
            mRecipePlayer = null;
        }
        super.onPause();
    }

    // https://stackoverflow.com/a/28560142/1615055
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) // If we are becoming invisible, then...
            {
                if (mRecipePlayer != null) {
                    mRecipePlayer.pause();
                }
            }

            if (isVisibleToUser) {
                if (mRecipePlayer != null) {
                    mRecipePlayer.play();
                    mPlayerView.hideController();
                }
            }
        }
    }

}
