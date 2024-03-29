package com.rhdigital.rhclient.room.DAO;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.rhdigital.rhclient.room.model.Video;

import java.util.List;

@Dao
public abstract class VideoDAO extends BaseDAO<Video> {
  // DELETE
  @Query("DELETE FROM videos")
  abstract public void deleteAll();

  @Query("DELETE FROM videos WHERE id = :id")
  abstract public int deleteById(int id);

  // GET
  @Query("SELECT * FROM videos WHERE course_id = :id LIMIT 1")
  abstract public LiveData<Video> findByCourseId(@NonNull String id);

  @Query("SELECT * FROM videos WHERE id = :id")
  abstract public LiveData<Video> findById(@NonNull String id);

  @Query("SELECT * FROM videos")
  abstract public LiveData<List<Video>> getAll();

  @Query("SELECT * FROM videos WHERE is_authorised = 1;")
  abstract public LiveData<List<Video>> getAllAuthorised();

  @Query("SELECT * FROM videos WHERE is_authorised = 0;")
  abstract public LiveData<List<Video>> getAllUnauthorised();

  // AUTH
  @Query("UPDATE videos SET is_authorised = 1 WHERE program_id = :programId")
  abstract public int authorise(@NonNull String programId);

  @Query("UPDATE videos SET is_authorised = 0 WHERE program_id = :programId")
  abstract public void deauthorise(String programId);

  @Query("UPDATE videos SET is_authorised = 0 WHERE is_authorised != 0")
  abstract public void deauthoriseAll();
}
