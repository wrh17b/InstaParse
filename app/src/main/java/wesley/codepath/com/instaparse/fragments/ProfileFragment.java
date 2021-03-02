package wesley.codepath.com.instaparse.fragments;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import wesley.codepath.com.instaparse.Post;

public class ProfileFragment extends TimelineFragment{
    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(POST_CREATED_AT);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
                postAdapter.clear();
                postAdapter.addAll(queryposts);
                postAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);

            }
        });
    }
}
