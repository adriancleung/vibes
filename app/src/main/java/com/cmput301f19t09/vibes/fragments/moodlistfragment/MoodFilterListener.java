package com.cmput301f19t09.vibes.fragments.moodlistfragment;

import androidx.annotation.Nullable;

import java.io.Serializable;

/*
For part 4, this will be notified when MoodListFIlter is changed,
should possibly be replaced with observers
 */
public interface MoodFilterListener extends Serializable
{
    void showOwnMoods();
    void showFollowedMoods();

    void setFilter(String emotion);

}
