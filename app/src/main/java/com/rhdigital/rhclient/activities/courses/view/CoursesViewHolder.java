package com.rhdigital.rhclient.activities.courses.view;

import android.animation.AnimatorSet;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.rhdigital.rhclient.R;
import com.rhdigital.rhclient.RHApplication;
import com.rhdigital.rhclient.activities.courses.CoursesActivity;
import com.rhdigital.rhclient.activities.courses.listeners.FullscreenToggleOnClick;
import com.rhdigital.rhclient.activities.courses.services.VideoPlayerService;
import com.rhdigital.rhclient.common.loader.CustomLoaderFactory;
import com.rhdigital.rhclient.common.services.NavigationService;
import com.rhdigital.rhclient.database.model.Course;

public class CoursesViewHolder extends RecyclerView.ViewHolder {
  private TextView headerView;
  private TextView authorView;
  private ImageView imageView;
  private Button actionButton;
  private FrameLayout frameLayout;
  private PlayerView videoPlayer;
  private RelativeLayout itemContent;
  private SimpleExoPlayer player;
  private CustomLoaderFactory customLoaderFactory = null;
  private int imageWidth = 0;
  private int imageHeight = 0;

  // Video Controls
  private ImageButton backButton;
  private TextView videoTitle;
  private ImageButton fullscreen;
  private ImageButton fullscreenExit;

  private Course course;

  private boolean isAuthorisedMode = false;

  private Context context;
  private Uri videoUri;

  private VideoPlayerService videoPlayerService;

  public CoursesViewHolder(@NonNull View itemView, Context context) {
    super(itemView);
    this.context = context;

    //Dependency Injection
    this.videoPlayerService = ((RHApplication) context.getApplicationContext()).getVideoPlayerService();

    itemContent = itemView.findViewById(R.id.item_content);
    imageView = itemView.findViewById(R.id.courses_card_item_image_view);
    headerView = itemView.findViewById(R.id.courses_text_header_item);
    authorView = itemView.findViewById(R.id.courses_text_author_item);
    frameLayout = (FrameLayout) itemView.findViewById(R.id.loader);
    videoPlayer = (PlayerView) itemView.findViewById(R.id.video_player);
    actionButton = itemView.findViewById(R.id.courses_item_action_button);
    videoTitle = itemView.findViewById(R.id.exo_title);
    backButton = itemView.findViewById(R.id.exo_exit);
    fullscreen = itemView.findViewById(R.id.exo_full);
    fullscreenExit = itemView.findViewById(R.id.exo_full_exit);

    fullscreenExit.setVisibility(View.GONE);

    // OnClickListeners
    backButton.setOnClickListener(new CourseItemViewBackOnClick(context, this));

    // View Tree Management
    imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (imageView.getHeight() > 0 && imageView.getWidth() > 0) {
          imageHeight = imageView.getHeight();
          imageWidth = imageView.getWidth();
          imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
        }
      }
    });

    frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (initLoaderFactory()) {
          addLoader();
          frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
        }
      }
    });

    itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (itemView != null) {
          //Navigation
          NavController navController = Navigation.findNavController((CoursesActivity)context, R.id.nav_host_courses);
          NavigationService.getINSTANCE().addNav(getClass().getName(), navController);

          fullscreen.setOnClickListener(new FullscreenToggleOnClick(context, getClass().getName(),null));
          itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this::onGlobalLayout);
        }
      }
    });
  }

  public void setCourse(Course course) {
    this.course = course;
    this.headerView.setText(course.getName());
    this.authorView.setText(course.getAuthor());
  }

  public void setIsAuthorisedMode(boolean authorisedMode) {
    this.isAuthorisedMode = authorisedMode;
    if (actionButton.hasOnClickListeners()) {
      actionButton.setOnClickListener(null);
    }
    if (isAuthorisedMode) {
      actionButton.setOnClickListener(new CourseItemViewWatchNowOnClick(this));
      actionButton.setText("Watch Now");
      videoTitle.setText(course.getName());
      requestVideoMinimiseReattach();
    } else {
      //TODO: Enable More Info Functionality
      actionButton.setText("More Info");
    }
  }

  public ImageView getImageView() {
    return imageView;
  }

  public TextView getHeaderView() {
    return headerView;
  }

  public TextView getVideoTitle() {
    return videoTitle;
  }

  // Video Player
  public void setVideoUri(Uri uri) {
    this.videoUri = uri;
  }

  public void revealPlayer(boolean playerReveal) {
    if (playerReveal) {
      itemContent.setVisibility(View.GONE);
      videoPlayer.setVisibility(View.VISIBLE);
    } else {
      videoPlayer.setVisibility(View.GONE);
      itemContent.setVisibility(View.VISIBLE);
    }
  }

  public void requestVideoMinimiseReattach() {
    if (videoPlayerService.isFullScreen()) {
      if (videoPlayerService.validateViewHolderID(course.getId())) {
        initVideoPlayer();
      }
    }
  }

  public void initVideoPlayer() {
    revealPlayer(true);
    this.videoPlayerService.initPlayer(context, videoUri, course.getId(), this, videoPlayer);
  }

  // Loader
  private boolean initLoaderFactory() {
    if (frameLayout.getHeight() > 0 && frameLayout.getWidth() > 0) {
      if (customLoaderFactory == null) {
        customLoaderFactory = new CustomLoaderFactory(
          itemView.getContext(),
          frameLayout.getWidth(),
          frameLayout.getHeight(),
          4,
          35,
          25);
        return true;
      }
    }
    return false;
  }

  public void addLoader() {
    if (customLoaderFactory != null) {
      if (!initLoaderFactory()) {
        removeLoader();
      }
      for (View v : customLoaderFactory.getChildren()) {
        frameLayout.addView(v);
      }

      for (AnimatorSet a : customLoaderFactory.createAnimations()) {
        a.start();
      }
    }
  }

  public void removeLoader() {
    for (AnimatorSet a : customLoaderFactory.createAnimations()) {
      a.end();
    }
    for (View v : customLoaderFactory.getChildren()) {
      frameLayout.removeView(v);
    }
  }

  public static class CourseItemViewWatchNowOnClick implements View.OnClickListener {

    private CoursesViewHolder holder;

    public CourseItemViewWatchNowOnClick(CoursesViewHolder holder) {
      this.holder = holder;
    }

    @Override
    public void onClick(View view) {
      holder.initVideoPlayer();
    }
  }

  public static class CourseItemViewBackOnClick implements View.OnClickListener {

    Context context;
    CoursesViewHolder holder;

    public CourseItemViewBackOnClick(Context context, CoursesViewHolder holder) {
      this. context = context;
      this.holder = holder;
    }

    @Override
    public void onClick(View view) {
      VideoPlayerService.getInstance().destroyVideo();
      holder.revealPlayer(false);
    }
  }

}
