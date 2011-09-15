package org.nick.wwwjdic.hkr;

import org.nick.wwwjdic.ActionBarActivity;
import org.nick.wwwjdic.R;

import android.os.Bundle;

public class HkrCandidates extends ActionBarActivity {

    public static final String EXTRA_HKR_CANDIDATES = "org.nick.wwwjdic.hkrCandidates";

    public HkrCandidates() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(android.support.v4.view.Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.hkr_candidates);
        setProgressBarIndeterminateVisibility(Boolean.FALSE);
    }


}
