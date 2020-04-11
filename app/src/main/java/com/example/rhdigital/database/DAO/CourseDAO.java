package com.example.rhdigital.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.rhdigital.database.model.Course;

import java.util.List;

@Dao
public abstract class CourseDAO extends BaseDAO<Course> {

    @Query("DELETE FROM courses")
    abstract public void deleteAll();

    @Query("DELETE FROM courses WHERE id = :id")
    abstract public int deleteById(int id);

    @Query("SELECT * FROM courses")
    abstract public LiveData<List<Course>> getAllCourses();
}
