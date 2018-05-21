package cefim.turing.pokomon_go_v1.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import okhttp3.Response;

/**
 * Created by crespeau on 11/04/2018.
 */

public class SignUpActivity extends AppCompatActivity implements APICallback {

    private EditText mEditTextUserName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private View mProgressView;
    private View mLoginFormView;

    private Handler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mContext = this;
        mHandler = new Handler();

        mEditTextUserName = (EditText) findViewById(R.id.username);
        mEditTextEmail = (EditText) findViewById(R.id.email);
        mEditTextPassword = (EditText) findViewById(R.id.password);

        Button mButtonSignIn = (Button) findViewById(R.id.email_sign_in_button);
        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();;
            }
        });


        mLoginFormView = findViewById(R.id.sign_up_form);
        mProgressView = findViewById(R.id.sign_up_progress);

    }

    private boolean isEmailValid(String email) {
        //TODO: le champ email doit contenir un "@".

        if (email.contains("@"))
            return true;
        else
            return false;

    }

    private boolean isPasswordValid(String password) {
        //TODO: Le champ password doit être supérieur ou égal à 4 carcatères.
        if (password.length()>= 4)
            return true;
        else
            return false;
    }

    private boolean isUsernameValid(String username) {
        //TODO: Le champ password doit être supérieur ou égal à 6 carcatères.
        if (username.length()>= 6)
            return true;
        else
            return false;
    }

    private void attemptSignUp() {

        // Reset errors.
        mEditTextEmail.setError(null);
        mEditTextPassword.setError(null);
        mEditTextUserName.setError(null);

        // Store values at the time of the login attempt.
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String username = mEditTextUserName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mEditTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEditTextPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEditTextEmail.setError(getString(R.string.error_field_required));
            focusView = mEditTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEditTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEditTextEmail;
            cancel = true;
        }


        if (TextUtils.isEmpty(username)) {
            mEditTextUserName.setError(getString(R.string.error_field_required));
            focusView = mEditTextUserName;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mEditTextUserName.setError(getString(R.string.error_invalid_username));
            focusView = mEditTextUserName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            Object[] paramsValues = {email, password, username};
            String params = String.format(UtilsAPI.URL_SIGN_UP_PARAMS, paramsValues);

            try {
                UtilsAPI.getInstance().post(this, UtilsAPI.URL_SIGN_UP, params, "",0);
            } catch (IOException e) {
                showProgress(false);
                Toast.makeText(this, "Erreur...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void successCallback(Response response, int code) throws IOException {
        Log.d("lol", "SUCCESS");

        final String stringJson = response.body().string();

        Log.d("lol", "---------> Success - " + stringJson);


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showProgress(false);
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });

    }

    @Override
    public void failCallback(Response response, int code) {
        Log.d("lol", "Fail");
        mHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              showProgress(false);
                          }
                      });

    }
}
