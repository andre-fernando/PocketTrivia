package com.andre_fernando.pockettrivia.utils;

/**
 * This interface helps to communicate between
 * the Main Activity and it's fragments.
 */
public interface MainActivityFragmentCommunicator {

    void startQuiz(String url,int categoryId, String difficulty, String type);

}
