package com.rhdigital.rhclient.database.DAO;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.rhdigital.rhclient.database.model.AuthorisedProgram;
import com.rhdigital.rhclient.database.model.Program;

import java.util.List;

public abstract class AuthorisedProgramDAO extends BaseDAO<AuthorisedProgram> {

    // DELETE
    @Query("DELETE FROM authorised_programs")
    abstract public void deleteAll();

    // GET
    @Query("SELECT * FROM authorised_programs WHERE programId = :id")
    abstract public LiveData<AuthorisedProgram> findById(@NonNull String id);

    @Query("SELECT * FROM authorised_programs")
    abstract public LiveData<List<AuthorisedProgram>> getAll();
}
