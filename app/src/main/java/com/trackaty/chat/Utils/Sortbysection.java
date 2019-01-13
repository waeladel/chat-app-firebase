package com.trackaty.chat.Utils;

import com.trackaty.chat.models.Profile;

import java.util.Comparator;

public class Sortbysection implements Comparator<Profile>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Profile a, Profile b)
    {
        return a.getSection() - b.getSection();
    }
}