package com.rhdigital.rhclient.common.ancestors;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rhdigital.rhclient.common.dto.UserFieldDto;
import com.rhdigital.rhclient.common.interfaces.OnClickCallback;
import com.rhdigital.rhclient.room.model.Course;
import com.rhdigital.rhclient.room.model.CourseDescription;
import com.rhdigital.rhclient.room.model.Program;
import com.rhdigital.rhclient.room.model.Report;
import com.rhdigital.rhclient.room.model.Workbook;

import java.util.HashMap;
import java.util.List;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Program program, Bitmap bitmap) {}

    public void bind(Program program, Course course, Bitmap bitmap, OnClickCallback videoCallback, OnClickCallback workbookCallback) {}

    public void bind(Course course) {}

    public void bind(CourseDescription courseDescription) {}

    public void bind(Workbook workbook) {}

    public void bind(Workbook workbook, Uri uri, OnClickCallback onClickCallback) {}

    public void bind(Report report, Uri uri, OnClickCallback onClickCallback) {}

    public void bind(String reportGroup, List<Report> reports, HashMap<String, Uri> uriMap, OnClickCallback onClickCallback) {}

    public void bind(UserFieldDto userField, OnClickCallback callback) {}
}
