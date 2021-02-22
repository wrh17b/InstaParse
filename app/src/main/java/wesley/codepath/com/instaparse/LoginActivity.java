package wesley.codepath.com.instaparse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG=LoginActivity.class.getCanonicalName();
    EditText etUsername;
    EditText etPassword;
    Button btLogin;
    Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Checking to see if user already logged in
        if(ParseUser.getCurrentUser()!=null){
            //goMainActivity();
            goTimelineActivity();
            finish();
        }

        etUsername=findViewById(R.id.etUsername);
        etPassword=findViewById(R.id.etPassword);
        btLogin=findViewById(R.id.btLogin);
        btSignUp=findViewById(R.id.btSignUp);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                LoginUser(username,password);
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                boolean earlyreturn=false;
                if(password.isEmpty()){
                    etPassword.setError("Password cannot be empty");
                    earlyreturn=true;
                }
                if(username.isEmpty()){
                    etUsername.setError("Username cannot be empty");
                    earlyreturn=true;
                }
                if(earlyreturn)
                    return;
                SignUp(username,password);
            }
        });

    }

    private void SignUp(String username, String password) {

        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Issue with login", e);
                    //if empty username
                    if(e.getCode()==ParseException.USERNAME_MISSING){
                        etUsername.setError("Username cannot be empty");
                    }
                    //if empty password
                    if(e.getCode()==ParseException.PASSWORD_MISSING){
                        etPassword.setError("Password cannot be empty");
                    }
                    //if username taken
                    if(e.getCode()==ParseException.USERNAME_TAKEN){
                        etUsername.setError("That username is already taken");
                    }
                    return;
                }
                Toast.makeText(LoginActivity.this, "Sign up successful, please login!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoginUser(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //If there was no problem exception e will be null
                //If e not null there was a problem
                
                if(e!=null){
                    Log.e(TAG, "Issue with login", e);
                    //if empty username
                    if(e.getCode()==ParseException.USERNAME_MISSING){
                        etUsername.setError("Username cannot be empty");
                    }
                    //if empty password
                    if(e.getCode()==ParseException.PASSWORD_MISSING){
                        etPassword.setError("Password cannot be empty");
                    }
                    //if invalid username/password
                    if(e.getCode()==ParseException.OBJECT_NOT_FOUND){
                        etUsername.setError("Invalid username/password");
                    }
                    return;
                }
                //Navigate to main activity:
                //goMainActivity();
                goTimelineActivity();
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                //Finishing the activity to remove from back stack
                finish();

            }
        });

    }

    private void goMainActivity() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    private void goTimelineActivity() {
        Intent i = new Intent(this,TimelineActivity.class);
        startActivity(i);
    }
}