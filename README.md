# Pocket Trivia
<img src="/readme_images/PocketTriviaLogoIcon.svg" width="300" height="300" />
<br><br/>

This is a Trivia app that challenges you to either multiple choice questions or boolean questions 
from a large variety of categories. Pocket Trivia uses the [Open Trivia Database](https://opentdb.com/)
for it's question base. This project was built for the Capstone Project in the Android Nanodegree
course by Udacity. I have made attempts to make it GDPR compliant, still new to the concept feedback
would be much appreciated.

<img src="/readme_images/HomeScreen.png" width="280" height="500"/>

# Notes:
* The [Firebase Job Dispatcher](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/utils/DailyTriviaJobDispatcher.java) for the daily trivia is set to 10 min for testing purpose.
  The 24 hr trigger is commented out, right above the 10 min trigger.
* Comment regions are used to make the code more presentable, would advice activating it.
* Loaders were used in this project as it was required by the rubric.

# Requirements:
* Minimum Android Sdk 21
* Android Studio 3

<br><br/>
# Android Features 

## [Builder Pattern](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/helpers/QuestionBuilder.java)

The app contains a builder pattern where there is one parent class that is the [Question.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/data/objects/Question.java)
and two children classes,i.e. [BooleanQuestion.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/data/objects/BooleanQuestion.java) and [MultipleChoiceQuestion.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/data/objects/MultipleChoiceQuestion.java).
The builder method is present in the [QuestionBuilder.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/helpers/QuestionBuilder.java).


## [SQL Database with Content Provider](https://github.com/andre-fernando/PocketTriviaCapstoneProject/tree/master/app/src/main/java/com/andre_fernando/pockettrivia/data/db)

The app uses an SQL database to store the high scores of the users and uses to the content provider
to communicate with the database.

<img src="/readme_images/HighScores.png" width="280" height="500"/>

<img src="/readme_images/HighScoresExpanded.png" width="280" height="500"/>


## [Daily Trivia Widget](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/ui/DailyTriviaWidget.java)

There is a daily trivia widget that allows the user to interact with a daily trivia question
from the home screen. It has a [WidgetIntentService.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/utils/WidgetIntentService.java) to communicate between the widgets and
the rest of the app. It also has a RemoteViews factory class, which gives the widget a scroll feature
for the multiple choices, which is needed due to the lack of screen space. The class is
[WidgetAdapterService.java](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/utils/WidgetAdapterService.java)

<img src="/readme_images/Widget.png" width="280" height="400"/>

## [Google Admob](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/ui/MainActivity.java)

The app has Google Admob setup to show the user banner ads in the MainActivity.

## [Google Sign In](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/ui/SignInFragment.java)

The app allows it's users to sign in via Google Sign in feature. We only collect the username
for the high scores. The collected username is encrypted and stored in the shared preferences.

<img src="/readme_images/LogIn.png" width="280" height="500"/>

## [Firebase Job Dispatcher](https://github.com/andre-fernando/PocketTriviaCapstoneProject/blob/master/app/src/main/java/com/andre_fernando/pockettrivia/utils/DailyTriviaJobDispatcher.java)

The app uses Firebase Job Dispatcher to update the daily trivia every 24hrs. For testing purposes
it is set to trigger every 10 min. This also shows a notification when the there is a new Daily
Trivia question.

## [Fragment Transition Animations](https://github.com/andre-fernando/PocketTriviaCapstoneProject/tree/master/app/src/main/res/anim)

There are simple fragment transitions which are placed in res/anim folder.








