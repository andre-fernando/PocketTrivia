package com.andre_fernando.pockettrivia;



import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.andre_fernando.pockettrivia.helpers.OpenTriviaHelper;
import com.andre_fernando.pockettrivia.helpers.SharedPreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings({"ALL",})
@RunWith(AndroidJUnit4.class)
public class PocketTriviaTests {
    private Context context;

    @Before
    public void getContext(){
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.andre_fernando.pockettrivia", context.getPackageName());
    }

    /**
     * Validates the input and output of the username into the shared
     * preferences with encryption.
     */
    @Test
    public void validateUsernameIO(){
        String givenName,receivedName , backupName;
        backupName = "";
        givenName = "Jane Doe";
        SharedPreferenceHelper helper = new SharedPreferenceHelper(context);
        boolean isLoggedIn = helper.isLoggedIn();
        if (isLoggedIn) backupName = helper.getUserName();
        helper.setUserName(givenName);
        receivedName = helper.getUserName();
        if (isLoggedIn) helper.setUserName(backupName);

        assertEquals("The given username did not match with the received",givenName,receivedName);

    }


    /**
     * Validates if the assembled url matches the required url.
     */
    @Test
    public void validateAssembledUrl(){
        String expectedUrl="https://opentdb.com/api.php?amount=20&category=21";
        String numberOfQuestions = "20";
        String[] listCategories = context.getResources().getStringArray(R.array.category_names);
        int categoryNumber=0;
        for (int i = 0; i<listCategories.length;i++){
            if (listCategories[i].equals("Sports")) categoryNumber = i;
        }

        String actualUrl = new OpenTriviaHelper(context).getAssembledURL(
            numberOfQuestions,
                categoryNumber,
                "any",
                "any"
        );
        assertEquals(categoryNumber+expectedUrl + "="+actualUrl ,expectedUrl,actualUrl);
    }


}
