package com.rhdigital.rhclient.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.rhdigital.rhclient.database.model.Course;

import java.util.List;

@Dao
public abstract class CourseDAO extends BaseDAO<Course> {

    @Query("DELETE FROM courses")
    abstract public void deleteAll();

    @Query("DELETE FROM courses WHERE id = :id")
    abstract public int deleteById(int id);

    @Query("SELECT * FROM courses")
    abstract public LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE isAuthorised = 1;")
    abstract public LiveData<List<Course>> getAllAuthorisedCourses();

    @Query("UPDATE courses SET isAuthorised = 1 WHERE id = :id")
    abstract public int authorise(String id);
}
