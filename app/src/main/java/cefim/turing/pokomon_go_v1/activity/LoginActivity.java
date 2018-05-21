package cefim.turing.pokomon_go_v1.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements APICallback {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mSignUpTextView;

    private Handler mHandler;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        mHandler = new Handler();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        // TODO: Changer le texte du bouton
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: Appeler la bonne méthode
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // TODO: Ajouter un champ texte cliquable, qui permet d'aller vers la page de création de compte.
        mSignUpTextView = (TextView) findViewById(R.id.sign_up);
        mSignUpTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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

            Object[] paramsValues = {email, password};
            String params = String.format(UtilsAPI.URL_LOGIN_PARAMS, paramsValues);

            try {
                UtilsAPI.getInstance().post(this, UtilsAPI.URL_LOGIN, params, "",0);
            } catch (IOException e) {
                showProgress(false);
                Toast.makeText(this, "Erreur...", Toast.LENGTH_SHORT).show();
            }
        }
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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
    public void successCallback(final Response response, int code) throws IOException {

        final String stringJson = response.body().string();

        Log.d("lol", "---------> Success - " + stringJson);

        mHandler.post(new Runnable() {
            @Override
            public void run() {


                JSONObject obj = null;
                try {
                    obj = new JSONObject(stringJson);
                    Log.d("lol", "---------> Token - " + obj.getString("token"));

                    // TODO: sauvegarder le mot de passe
                    UtilsPreferences.getPreferences(mContext).setKey("password", mPasswordView.getText().toString());
                    UtilsPreferences.getPreferences(mContext).setKey("login", mEmailView.getText().toString());
                    UtilsPreferences.getPreferences(mContext).setKey("token", "Bearer "+obj.getString("token"));


                    // TODO: lancer MainActivity
                    startActivity(new Intent(mContext, MainActivity.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failCallback(final Response response, int code) {


        if (response != null) {

            Log.d("lol", "---------> Fail - " + response.message());


            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showProgress(false);
                    // TODO: récupérer le code de la réponse
                    int code = response.code();
                    Log.d("lol", "---------> Fail - " + code);

                    switch (code){
                        case 404:
                            Toast toast404 = Toast.makeText(mContext, "ERREUR 404", Toast.LENGTH_SHORT);
                            toast404.show();
                            break;
                        case 403:
                            Toast toast403 = Toast.makeText(mContext, "ERREUR 403", Toast.LENGTH_SHORT);
                            toast403.show();
                            break;
                    }

                    // TODO: faites une erreur en fonction du code récupéré.

                    //mPasswordView.requestFocus();
                }
            });
        }
    }
}

