package com.rhdigital.rhclient.common.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rhdigital.rhclient.database.model.Course;

import java.util.HashMap;
import java.util.List;

public class RemoteResourceService {

  private HashMap<String, StorageReference> urlMap = new HashMap<>();
  private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
  private MutableLiveData<HashMap<String, Bitmap>> liveImageMap;
  private MutableLiveData<HashMap<String, Uri>> liveVideoUriMap;
  private MutableLiveData<HashMap<String, Uri>> liveDocumentMap;
  private MutableLiveData<Bitmap> liveProfilePhotoUri;
  private HashMap<String, Bitmap> liveImageMapSurrogate = new HashMap<>();
  private HashMap<String, Uri> liveVideoUriMapSurrogate = new HashMap<>();
  private HashMap<String, Uri> liveDocumentMapSurrogate = new HashMap<>();

  public RemoteResourceService() {
    StorageReference root = firebaseStorage.getReference();
    urlMap.put("l", root.child("courses/posters/drawable-ldpi"));
    urlMap.put("m", root.child("courses/posters/drawable-mdpi"));
    urlMap.put("h", root.child("courses/posters/drawable-hdpi"));
    urlMap.put("x", root.child("courses/posters/drawable-xhdpi"));
    urlMap.put("xx", root.child("courses/posters/drawable-xxhdpi"));
    urlMap.put("xxx", root.child("courses/posters/drawable-xxxhdpi"));
    urlMap.put("video", root.child("courses/videos"));
    urlMap.put("doc", root.child("documents"));
    urlMap.put("profile_photo", root.child("profile_photos"));
  }

  public StorageReference getImageResourceURL(Context context, String endpoint) {
    float density = context.getResources().getDisplayMetrics().density;
    StorageReference ref = null;
      if (density <= 0.75) {
        ref = urlMap.get("l");
      } else if (density <= 1.0) {
        ref = urlMap.get("m");
      } else if (density <= 1.5) {
        ref = urlMap.get("h");
      } else if (density <= 2.0) {
        ref = urlMap.get("x");
      } else if (density <= 3.0) {
        ref = urlMap.get("xx");
      } else if (density <= 4.0) {
        ref = urlMap.get("xxx");
      } else {
        ref = urlMap.get("h");
      }
      return ref.child(endpoint);
  }

  public StorageReference getProfileImageResourceURL(String id) {
    return urlMap.get("profile_photo").child(id);
  }

  public StorageReference getVideoResourceURL(String endpoint) {
    //TODO add screen density to check to determine best resolution to download
    return urlMap.get("video").child(endpoint);
  }

  public StorageReference getDocumentResourceURL(String endpoint) {
    return urlMap.get("doc").child(endpoint);
  }

  public LiveData<HashMap<String, Bitmap>> getAllBitmap(Context context, List<Course> courses, int width, int height) {
    if (liveImageMap == null) {
      liveImageMap = new MutableLiveData<>();
    }
    loadImageUri(context, courses, width, height);
    return liveImageMap;
  }

  public LiveData<Bitmap> getProfilePhoto(Context context, String id, int width, int height) {
    if (liveProfilePhotoUri == null) {
      liveProfilePhotoUri = new MutableLiveData<>();
    }
    loadProfileImageUri(context, id, width, height);
    return liveProfilePhotoUri;
  }

  public LiveData<HashMap<String, Uri>> getAllVideoURI(List<Course> courses, int width, int height) {
    if (liveVideoUriMap == null) {
      liveVideoUriMap = new MutableLiveData<>();
    }
    loadVideoUri(courses, width, height);
    return liveVideoUriMap;
  }

  public LiveData<HashMap<String, Uri>> getAllDocumentURI(String... docIds) {
   if (liveDocumentMap == null) {
     liveDocumentMap = new MutableLiveData<>();
   }
    loadDocumentUri(docIds);
   return liveDocumentMap;
  }

  public void loadProfileImageUri(Context context, String id, int width, int height) {
    getProfileImageResourceURL(id).getDownloadUrl()
      .addOnSuccessListener(uri -> {
        //Preload Image into Disk Cache
        Glide.with(context)
          .load(uri)
          .asBitmap()
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .skipMemoryCache(false)
          .into(new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
              //Populate LiveData
              liveProfilePhotoUri.setValue(resource);
            }
      });
    });
  }

  public void loadDocumentUri(String... documentIds) {
    for (String id: documentIds) {
      getDocumentResourceURL(id).getDownloadUrl()
        .addOnSuccessListener(uri -> {
          //TODO: IMPLEMENT A CLEANER TRUNCATING STRATEGY
          Log.d("REMOTE", "new doc Id : " + id);
          String identifier = id.split("\\.")[0];
          liveDocumentMapSurrogate.put(identifier, uri);
          liveDocumentMap.setValue(liveDocumentMapSurrogate);
        });
    }
  }

  public void loadVideoUri(List<Course> courses, int width, int height) {
    for (Course c : courses) {
      getVideoResourceURL(c.getVideoURL()).getDownloadUrl().addOnSuccessListener(uri -> {
        liveVideoUriMapSurrogate.put(c.getId(), uri);
        liveVideoUriMap.setValue(liveVideoUriMapSurrogate);
        Log.d("REMOTE", "URI : " + uri);
      });
    }
  }

  private void loadImageUri(Context context, List<Course> courses, int width, int height) {
    for (Course c : courses) {
      getImageResourceURL(
          context,
          c.getThumbnailURL())
        .getDownloadUrl()
        .addOnSuccessListener(uri -> {
          //Preload Images into Disk Cache
          Glide.with(context)
            .load(uri)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .into(new SimpleTarget<Bitmap>(width, height) {
              @Override
              public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                //Populate UriMapLiveData
                liveImageMapSurrogate.put(c.getId(), resource);
                liveImageMap.setValue(liveImageMapSurrogate);
              }
            });
        });
    }
  }
}
