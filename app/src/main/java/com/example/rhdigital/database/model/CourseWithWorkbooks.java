package com.example.rhdigital.database.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CourseWithWorkbooks {

  @Embedded
  private Course course;

  @Relation(
    parentColumn = "id",
    entityColumn = "course_id"
  )
  private List<Workbook> workbooks;

  public Course getCourse() {
    return course;
  }

  public List<Workbook> getWorkbooks() {
    return workbooks;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public void setWorkbooks(List<Workbook> workbooks) {
    this.workbooks = workbooks;
  }
}
