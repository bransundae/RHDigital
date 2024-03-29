package com.rhdigital.rhclient.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rhdigital.rhclient.room.model.Workbook;
import com.rhdigital.rhclient.room.repository.RHRepository;

import java.util.List;

public class WorkbookViewModel extends AndroidViewModel {
    private RHRepository rhRepository;

    public WorkbookViewModel(@NonNull Application application) {
        super(application);
        rhRepository = new RHRepository(application);
    }

    public LiveData<List<Workbook>> getAllWorkbooks() {
        return rhRepository.getAllWorkbooks();
    }

//    public LiveData<List<CourseWithWorkbooks>> getAllCoursesWithWorkbooks() { return rhRepository.getAllCoursesWithWorkbooks(); }
//
//    public LiveData<List<CourseWithWorkbooks>> getAllAuthorisedCoursesWithWorkbooks() { return rhRepository.getAllAuthorisedCoursesWithWorkbooks(); }
//
//    public LiveData<List<Workbook>> getWorkbooksById(@NonNull int courseId) {
//        return rhRepository.getWorkbooksById(courseId);
//    }

//    public LiveData<HashMap<String, Bitmap>> getAllWorkbookPosters(Context context, List<CourseWithWorkbooks> workbooks, int width, int height) { return new RemoteResourceService().getAllBitmap(context, workbooks, width, height, false); }
//
//    public LiveData<HashMap<String, HashMap<String, Uri>>> getAllWorkbookUri(List<CourseWithWorkbooks> workbooks) { return new RemoteResourceService().getAllWorkbookURI(workbooks); };
//
//    public LiveData<ResponseBody> downloadWorkbook(String downloadURL) { return new RemoteResourceService().downloadWorkbook(downloadURL); }

}
