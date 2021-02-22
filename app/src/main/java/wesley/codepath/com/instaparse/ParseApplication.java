package wesley.codepath.com.instaparse;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        //Registering subclass
        ParseObject.registerSubclass(Post.class);

        //Init Parse SDK as soon as app is initialized
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("9PB4InnwnW9eAxEQkL37QTiUq7igGLRswGqHTt1A")
                .clientKey("s08OCy4peln9Ie9rBhcmFVu1UE3wIhtoyn5Ddqmo")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
