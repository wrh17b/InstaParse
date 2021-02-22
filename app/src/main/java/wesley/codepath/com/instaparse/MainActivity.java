package wesley.codepath.com.instaparse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG=MainActivity.class.getCanonicalName();
    public static final String photoFileName="photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=42069;

    TextInputEditText etCaption;
    Button btTakepic;
    Button btSubmit;
    ImageView ivPic;
    ProgressBar pbLoading;

    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCaption=findViewById(R.id.etCaption);
        btTakepic=findViewById(R.id.btTakePic);
        btSubmit=findViewById(R.id.btSubmit);
        ivPic=findViewById(R.id.ivPic);
        pbLoading=findViewById(R.id.pbLoading);


        //Setting actionbar title
        getSupportActionBar().setTitle("Compose");
        
        //Setting the on click listener to open the camera
        btTakepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        //Setting the on click listener to submit the post
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaption.getText().toString();
                if(caption.isEmpty()){
                    Toast.makeText(MainActivity.this, "Caption cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(photoFile==null || ivPic.getDrawable()==null){
                    Toast.makeText(MainActivity.this, "Post must contain image", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(caption,currentUser,photoFile);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itHome:
                goTimelineActivity();
                break;
            case R.id.itLogout:
                logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logout() {
        Intent intent = new Intent(this,LoginActivity.class);
        ParseUser.logOut();
        Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private void goTimelineActivity() {
        Intent intent = new Intent(this,TimelineActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void savePost(String caption, ParseUser currentUser, File photoFile) {
        pbLoading.setVisibility(View.VISIBLE);
        Post post = new Post();
        post.setCaption(caption);
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                pbLoading.setVisibility(View.GONE);
                if(e!=null){
                    Log.e(TAG, "Error saving post",e );
                    Toast.makeText(MainActivity.this, "Error saving post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post saved successfully");
                Toast.makeText(MainActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                etCaption.setText("");
                ivPic.setImageResource(0);

            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Error querying posts",e );
                    //something has gone wrong
                    return;
                }
                for(Post post : posts){
                    Log.i(TAG, "Post "+ post.getCaption()+", Username: "+post.getUser().getUsername());
                }
            }
        });
    }
}