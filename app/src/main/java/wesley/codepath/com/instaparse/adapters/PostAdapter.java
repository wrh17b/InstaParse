package wesley.codepath.com.instaparse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.GetFileCallback;
import com.parse.ParseException;

import java.io.File;
import java.util.List;

import wesley.codepath.com.instaparse.Post;
import wesley.codepath.com.instaparse.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public static final String TAG=PostAdapter.class.getCanonicalName();
    List<Post> posts;
    Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvAuthor;
        TextView tvPostCaption;
        ImageView ivPostPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor=itemView.findViewById(R.id.tvAuthor);
            tvPostCaption=itemView.findViewById(R.id.tvPostCaption);
            ivPostPic=itemView.findViewById(R.id.ivPostPic);
        }

        public void bind(Post post) {
            tvAuthor.setText(post.getUser().getUsername());
            tvPostCaption.setText(post.getCaption());

            post.getImage().getFileInBackground(new GetFileCallback() {
                @Override
                public void done(File file, ParseException e) {
                    if(e!=null){
                        Log.e(TAG, "Error getting file", e);
                        return;
                    }
                    Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ivPostPic.setImageBitmap(image);
                }
            });
        }
    }
}
