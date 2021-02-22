package wesley.codepath.com.instaparse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import wesley.codepath.com.instaparse.adapters.PostAdapter;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG=TimelineActivity.class.getCanonicalName();

    RecyclerView rvTimeline;
    PostAdapter postAdapter;
    List<Post> posts;
    Context context;

    //TODO: Add a compose button, add a logout button



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        context=this;

        getSupportActionBar().setTitle("Timeline");

        rvTimeline=findViewById(R.id.rvTimeline);
        LinearLayoutManager layoutManager= new LinearLayoutManager(context);
        rvTimeline.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        postAdapter=new PostAdapter(posts,context);
        rvTimeline.setAdapter(postAdapter);

        queryPosts();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itLogout:
                logout();
                break;
            case R.id.itCompose:
                goCompose();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void goCompose() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        ParseUser.logOut();
        Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> queryposts, ParseException e) {
                if(e!=null){
                    Log.e(TAG, "Error querying posts",e );
                    //something has gone wrong
                    return;
                }
                for(Post post : queryposts){
                    Log.i(TAG, "Post "+ post.getCaption()+", Username: "+post.getUser().getUsername());
                }
                posts.addAll(queryposts);
                postAdapter.notifyDataSetChanged();
            }
        });
    }
}