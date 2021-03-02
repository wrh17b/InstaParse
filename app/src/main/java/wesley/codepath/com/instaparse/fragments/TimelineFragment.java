package wesley.codepath.com.instaparse.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import wesley.codepath.com.instaparse.Post;
import wesley.codepath.com.instaparse.R;
import wesley.codepath.com.instaparse.adapters.PostAdapter;


public class TimelineFragment extends Fragment {
    public static final String TAG= TimelineFragment.class.getCanonicalName();
    public static final String POST_CREATED_AT="createdAt";
    RecyclerView rvTimeline;
    PostAdapter postAdapter;
    List<Post> posts;
    Context context;
    SwipeRefreshLayout swipeContainer;

    public TimelineFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        rvTimeline=view.findViewById(R.id.rvTimeline);
        LinearLayoutManager layoutManager= new LinearLayoutManager(context);
        rvTimeline.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        postAdapter=new PostAdapter(posts,context);
        rvTimeline.setAdapter(postAdapter);
        swipeContainer=(SwipeRefreshLayout) view.findViewById(R.id.srlRefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
            }
        });

        queryPosts();

    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(POST_CREATED_AT);
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