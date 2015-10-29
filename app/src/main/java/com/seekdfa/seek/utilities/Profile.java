package com.seekdfa.seek.utilities;

import android.content.Context;
import android.provider.Settings;

import com.parse.ParseUser;

/**
 * Created by jbruzek on 10/28/15.
 */
public class Profile {
    private static String name;
    private static String email;
    private static String firstName;
    private static String lastName;
    private static String profilePic;
    private static String coverPic;
    private static boolean anonymous;
    private static Context context;

    /**
     * Set all of the profile information
     * @param n
     * @param fn
     * @param ln
     * @param e
     * @param pp
     * @param cp
     * @param anon
     */
    public static void setProfileInformation(String n, String fn, String ln, String e, String pp, String cp, boolean anon) {
        name = n;
        firstName = fn;
        lastName = ln;
        email = e;
        profilePic = pp;
        coverPic = cp;
        anonymous = anon;
    }

    /**
     * set the context
     */
    public static void setContext(Context c) {
        context = c;
    }

    /**
     * is this an anonymous profile?
     */
    public static boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Is the profile information set?
     */
    public static boolean isLoaded() {
        return (name != null && firstName != null && lastName != null && profilePic != null && coverPic != null && email != null);
    }

    /**
     * get the current user id
     */
    public static String getUserId() {
        if (anonymous) {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            ParseUser user = ParseUser.getCurrentUser();
            return user.getObjectId();
        }
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getProfilePic() {
        return profilePic;
    }

    public static String getCoverPic() {
        return coverPic;
    }
}
