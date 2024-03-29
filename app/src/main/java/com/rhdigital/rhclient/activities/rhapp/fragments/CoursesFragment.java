package com.rhdigital.rhclient.activities.rhapp.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rhdigital.rhclient.R;
import com.rhdigital.rhclient.RHApplication;
import com.rhdigital.rhclient.activities.rhapp.RHAppActivity;
import com.rhdigital.rhclient.activities.rhapp.adapters.CoursesRecyclerViewAdapter;
import com.rhdigital.rhclient.activities.rhapp.dialogs.DownloadingDialog;
import com.rhdigital.rhclient.activities.rhapp.viewmodel.CoursesViewModel;
import com.rhdigital.rhclient.activities.rhapp.viewmodel.RHAppViewModel;
import com.rhdigital.rhclient.common.dto.VideoControlActionDto;
import com.rhdigital.rhclient.common.interfaces.OnClickCallback;
import com.rhdigital.rhclient.common.services.NavigationService;
import com.rhdigital.rhclient.common.services.VideoPlayerService;
import com.rhdigital.rhclient.room.model.Course;
import com.rhdigital.rhclient.databinding.FragmentCoursesBinding;
import com.rhdigital.rhclient.room.model.Workbook;

import java.util.HashMap;
import java.util.List;

public class CoursesFragment extends RHAppFragment {

    private final String TAG = "COURSES_FRAGMENT";

    private RHAppActivity activity;

    // VIEW
    private FragmentCoursesBinding binding;

    // VIEW MODEL
    private CoursesViewModel coursesViewModel;
    private RHAppViewModel rhAppViewModel;

    // ADAPTERS
    private CoursesRecyclerViewAdapter coursesRecyclerViewAdapter;

    // OBSERVABLES
    private LiveData<HashMap<String, Bitmap>> coursesPosterObservable;
    private LiveData<List<Course>> coursesObservable;

    // OBSERVERS
    private Observer<HashMap<String, Bitmap>> coursesPosterObserver;
    private Observer<List<Course>> coursesObserver;

    private int width = 0;
    private int height = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(getLayoutInflater());
        rhAppViewModel = new ViewModelProvider(this).get(RHAppViewModel.class);
        coursesViewModel = new CoursesViewModel(getActivity().getApplication());
        coursesViewModel.init(getArguments().getString("programId"))
                .observe(getViewLifecycleOwner(), complete -> {
                    if (complete) {
                        initialiseUI();
                        initialiseLiveData();
                    }
                });
        binding.setViewModel(coursesViewModel);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = (RHAppActivity) getActivity();
        ((RHApplication)activity.getApplication()).setCurrentFragment(null);
    }

    @Override
    public void onPause() {
        hideShimmer();
        super.onPause();
    }

    private void showShimmer() {
        binding.coursesRecycler.setVisibility(View.GONE);
        binding.shimmerContainer.setVisibility(View.VISIBLE);
        binding.shimmerContainer.startShimmer();
    }

    private void hideShimmer() {
        binding.shimmerContainer.stopShimmer();
        binding.shimmerContainer.setVisibility(View.GONE);
        binding.coursesRecycler.setVisibility(View.VISIBLE);
    }

    private void initialiseUI() {
        calculateImageDimensions();
        initialiseRecyclerView();
    }

    private void initialiseLiveData() {
        showShimmer();
        coursesPosterObserver = new Observer<HashMap<String, Bitmap>>() {
            @Override
            public void onChanged(HashMap<String, Bitmap> posterMap) {
                if (posterMap != null) {
                    if (coursesObservable.getValue().size() == posterMap.size()) {
                        coursesPosterObservable.removeObserver(this);
                        onUpdateProgramPosters(posterMap);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.server_error_courses, Toast.LENGTH_LONG).show();
                }
                hideShimmer();
            }
        };

        coursesObserver = new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                if (courses != null) {
                    coursesObservable.removeObserver(this);
                    onUpdateCourses(courses);
                } else {
                    Toast.makeText(getContext(), R.string.server_error_courses, Toast.LENGTH_LONG).show();
                }
            }
        };

        coursesObservable = coursesViewModel.getCourses();
        coursesObservable.observe(getViewLifecycleOwner(), coursesObserver);
    }

    private void initialiseRecyclerView() {
        OnClickCallback videoCallback = (args) -> {
            VideoControlActionDto videoControlAction = (VideoControlActionDto)args[0];
            switch (videoControlAction.getActionType()) {
                case VideoControlActionDto.MAXIMISE:
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("videoData", videoControlAction.getVideo());
                        NavigationService.getINSTANCE()
                                .navigate(
                                        getActivity().getLocalClassName(),
                                        R.id.videosFragment, bundle, null
                                );
            }
        };

        OnClickCallback workbookCallback = (args) -> {
            String url = (args[1]).toString();
            DownloadingDialog downloadingDialog = presentDownloadingDialog();
            coursesViewModel.downloadFile(url)
                    .observe(getViewLifecycleOwner(), res -> {
                        if (res != null) {
                            Workbook report = (Workbook)args[0];
                            activity.writeFileToDisk(report.getTitle(), res);
                            downloadingDialog.onSuccess();
                        } else{
                            downloadingDialog.onFailure();
                        }
                    });
        };

        coursesRecyclerViewAdapter = new CoursesRecyclerViewAdapter(coursesViewModel.program.getValue(), videoCallback, workbookCallback);
        binding.coursesRecycler.setHasFixedSize(true);
        binding.coursesRecycler.setItemViewCacheSize(10);
        binding.coursesRecycler.setDrawingCacheEnabled(true);
        binding.coursesRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        binding.coursesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.coursesRecycler.setAdapter(coursesRecyclerViewAdapter);
    }

    private DownloadingDialog presentDownloadingDialog() {
        DownloadingDialog downloadingDialog = new DownloadingDialog();
        downloadingDialog.show(getParentFragmentManager(), "downloading_dialog");
        return downloadingDialog;
    }

    private void calculateImageDimensions() {
        float dip = 220f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = Math.round(px);
    }

    private void onUpdateCourses(List<Course> courses) {
        coursesRecyclerViewAdapter.setCourses(courses);
        if (coursesPosterObservable != null) {
            coursesPosterObservable.removeObservers(this);
        }
        coursesPosterObservable = coursesViewModel.getAllCoursePosters(getContext(), courses, width, height);
        coursesPosterObservable.observe(getActivity(), coursesPosterObserver);
    }

    private void onUpdateProgramPosters(HashMap<String, Bitmap> posterMap) {
        coursesRecyclerViewAdapter.setPosterUriMap(posterMap);
    }

    @Override
    public void onDestroyView() {
        this.coursesRecyclerViewAdapter = null;
        VideoPlayerService.getInstance().destroyAllVideoStreams(false);
        super.onDestroyView();
    }
}
