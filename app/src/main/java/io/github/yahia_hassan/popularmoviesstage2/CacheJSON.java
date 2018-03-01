package io.github.yahia_hassan.popularmoviesstage2;


public class CacheJSON {

    /* Why I made this String variable?
     *
     * I have 3 Activities, MainActivities, DetailsActivities and SettingsActivities (Which contains the SettingsFragment).
     * After implementing the AsyncTaskLoader the app had a small bug, every time I navigate back from the DetailsActivity to the MainActivity
     * the data is downloaded again and the RecyclerView will jump to the top. (I asked for help on the forums:  https://discussions.udacity.com/t/recyclerview-scrolls-back-to-the-top-after-back-button-is-pressed/600443)
     * To solve this problem I cached the data by overriding deliverResult(). And it worked, the RecyclerView kept its scrolled position.
     *
     * Then I added the SettingsActivity and a new problem happened, after changing the Settings (from Popular to Top Rated for example)
     * and going back to the MainActivity the data is not reloaded because it is cached.
     *
     * The only solution I found to solve this problem is to make this variable and access it in both deliverResult() and
     * in the PreferenceFragment.onSharedPreferenceChanged().
     *
     * At the end of the PreferenceFragment.onSharedPreferenceChanged() I set this variable to null so when I return to MainActivity the loader will
     * reload the data again.
     *
     * I know this isn't the best solution, I am getting a leak warning in the AsyncTaskLoader.
     * Is there a better solution?
     *
     */





    public static String mMovieJSON = null;
}
