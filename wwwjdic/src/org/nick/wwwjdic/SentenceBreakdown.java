package org.nick.wwwjdic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItem;

public class SentenceBreakdown extends ResultListBase {

    public static final String EXTRA_SENTENCE = "org.nick.wwwjdic.SENTENCE";
    public static final String EXTRA_SENTENCE_TRANSLATION = "org.nick.wwwjdic.SENTENCE_TRANSLATION";

    private boolean exampleBreakdown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String sentence = getIntent().getExtras().getString(EXTRA_SENTENCE);
        String translation = getIntent().getExtras().getString(
                EXTRA_SENTENCE_TRANSLATION);
        if (translation != null) {
            exampleBreakdown = true;
        }

        // setContentView(R.layout.sentence_breakdown);
        if (savedInstanceState == null) {
            SentenceBreakdownFragment breakDown = SentenceBreakdownFragment
                    .newInstance(0, sentence, translation);
            breakDown.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, breakDown).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this,
                    exampleBreakdown ? ExamplesResultList.class
                            : Wwwjdic.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            // do nothing
        }

        return super.onOptionsItemSelected(item);
    }

}
