package com.rhdigital.rhclient.activities.auth.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rhdigital.rhclient.R;
import com.rhdigital.rhclient.activities.auth.listeners.SignInOnClickListener;
import com.rhdigital.rhclient.activities.auth.listeners.SignUpRedirectOnClickListener;
import com.rhdigital.rhclient.util.GenericTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SignInFragment extends Fragment {

    //Threading
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Handler handler = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(@NonNull Message msg) {
        if (msg.what == 1) {
          submit.setEnabled(true);
          submit.setVisibility(View.VISIBLE);
        }
      }
    };

    //Components
    AutoCompleteTextView emailInput;
    AutoCompleteTextView passwordInput;
    Button submit;
    TextView resetPasswordRedirect;
    TextView signUpRedirect;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);

        //Initialise Components
        emailInput = (AutoCompleteTextView) view.findViewById(R.id.sign_in_email_input);
        passwordInput = (AutoCompleteTextView) view.findViewById(R.id.sign_in_password_input);
        submit = (Button) view.findViewById(R.id.sign_in_submit_btn);
        resetPasswordRedirect = (Button) view.findViewById(R.id.sign_in_reset_password_redirect_text);
        signUpRedirect = (Button) view.findViewById(R.id.sign_in_sign_up_redirect_text);

        //Set Listeners
        signUpRedirect.setOnClickListener(new SignUpRedirectOnClickListener(this));
        submit.setOnClickListener(new SignInOnClickListener(this));
        return view;
    }

    public void setSubmitDisableTimeout() {
      this.submit.setEnabled(false);
      this.submit.setVisibility(View.INVISIBLE);
      this.scheduledExecutorService.schedule(new GenericTimer(this), 3, TimeUnit.SECONDS);
    }

  public String getEmailText() {
    return this.emailInput.getText().toString();
  }

  public String getPasswordText() {
    return this.passwordInput.getText().toString();
  }

  public Handler getHandler() {
    return handler;
  }
}
