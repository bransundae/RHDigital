package com.rhdigital.rhclient.common.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebasePushNotificationService extends FirebaseMessagingService {

  public FirebasePushNotificationService() {}

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    if (remoteMessage.getNotification() != null) {
      String title = remoteMessage.getNotification().getTitle();
      String body = remoteMessage.getNotification().getBody();
      PushNotificationHelperService.getINSTANCE().sendNotificationToLifeCycleOwner(title, body);
    }
  }

  @Override
  public void onNewToken(@NonNull String s) {
    super.onNewToken(s);
    PushNotificationHelperService.getINSTANCE().saveTokenRemote();
  }
}
