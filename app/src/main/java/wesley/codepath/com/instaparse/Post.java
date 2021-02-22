package wesley.codepath.com.instaparse;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CAPTION="caption";
    public static final String KEY_IMAGE="image";
    public static final String KEY_USER="user";

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption){
        put(KEY_CAPTION,caption);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE,parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

}
