package org.nick.wwwjdic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import org.nick.wwwjdic.history.HistoryBase;
import org.nick.wwwjdic.history.HistoryDbHelper;
import org.nick.wwwjdic.hkr.RecognizeKanjiActivity;
import org.nick.wwwjdic.krad.KradChart;
import org.nick.wwwjdic.ocr.OcrActivity;
import org.nick.wwwjdic.utils.Analytics;
import org.nick.wwwjdic.utils.IntentSpan;
import org.nick.wwwjdic.utils.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class KanjiLookupFragment extends WwwjdicFragmentBase implements
        OnClickListener, OnItemSelectedListener {

    private static final String TAG = KanjiLookupFragment.class.getSimpleName();

    private static final Map<Integer, String> IDX_TO_CODE = new HashMap<Integer, String>();

    private EditText kanjiInputText;
    private Spinner kanjiSearchTypeSpinner;

    private EditText radicalEditText;
    private Button selectRadicalButton;
    private EditText strokeCountMinInput;
    private EditText strokeCountMaxInput;

    private HistoryDbHelper dbHelper;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateIdxToCode();

        findViews();
        setupListeners();
        setupSpinners();
        setupTabOrder();
        toggleRadicalStrokeCountPanel(false);

        Bundle extras = getArguments();
        if (extras != null) {
            String searchKey = extras.getString(Constants.SEARCH_TEXT_KEY);
            int searchType = extras.getInt(Constants.SEARCH_TYPE);
            if (searchKey != null) {
                switch (searchType) {
                case SearchCriteria.CRITERIA_TYPE_KANJI:
                    kanjiInputText.setText(searchKey);
                    break;
                default:
                    // do nothing
                }
                inputTextFromBundle = true;
            }
        }

        dbHelper = HistoryDbHelper.getInstance(getActivity());

        setupFavoritesHistoryFragments(HistoryBase.FILTER_KANJI);

        setupClickableLinks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.kanji_lookup, container, false);

        return v;
    }

    private void populateIdxToCode() {
        if (IDX_TO_CODE.isEmpty()) {
            String[] kanjiSearchCodesArray = getResources().getStringArray(
                    R.array.kanji_search_codes_array);
            for (int i = 0; i < kanjiSearchCodesArray.length; i++) {
                IDX_TO_CODE.put(i, kanjiSearchCodesArray[i]);
            }
        }
    }

    private void setupClickableLinks() {
        View historyView = getView().findViewById(R.id.kanji_history_summary);
        if (historyView != null) {
            historyView.setNextFocusDownId(R.id.hwrSearchLink);
        }

        TextView textView = (TextView) getView().findViewById(
                R.id.hwrSearchLink);
        makeClickable(textView, new Intent(getActivity(),
                RecognizeKanjiActivity.class));
        textView.setNextFocusUpId(R.id.kanji_history_summary);
        textView.setNextFocusDownId(R.id.ocrSearchLink);

        textView = (TextView) getView().findViewById(R.id.ocrSearchLink);
        makeClickable(textView, new Intent(getActivity(), OcrActivity.class));
        textView.setNextFocusUpId(R.id.hwrSearchLink);
        textView.setNextFocusDownId(R.id.multiRadicalSearchLink);

        textView = (TextView) getView().findViewById(
                R.id.multiRadicalSearchLink);
        makeClickable(textView, new Intent(getActivity(), KradChart.class));
        textView.setNextFocusUpId(R.id.ocrSearchLink);
    }

    private void makeClickable(TextView textView, Intent intent) {
        String text = textView.getText().toString();
        SpannableString str = new SpannableString(text);
        str.setSpan(new IntentSpan(getActivity(), intent), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        textView.setText(str);
        textView.setLinkTextColor(Color.WHITE);
        MovementMethod m = textView.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (textView.getLinksClickable()) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        setupClickableLinks();
    }

    private void setupListeners() {
        View kanjiSearchButton = getView().findViewById(R.id.kanjiSearchButton);
        kanjiSearchButton.setOnClickListener(this);

        // kanjiInputText.setOnFocusChangeListener(this);
        selectRadicalButton.setOnClickListener(this);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> kajiSearchTypeAdapter = ArrayAdapter
                .createFromResource(getActivity(),
                        R.array.kanji_search_types_array, R.layout.spinner_text);
        kajiSearchTypeAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kanjiSearchTypeSpinner.setAdapter(kajiSearchTypeAdapter);
        kanjiSearchTypeSpinner.setOnItemSelectedListener(this);
    }

    private void setupTabOrder() {
        strokeCountMinInput
                .setOnEditorActionListener(new OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        switch (actionId) {
                        case EditorInfo.IME_ACTION_NEXT:
                            EditText v1 = (EditText) v
                                    .focusSearch(View.FOCUS_RIGHT);
                            if (v1 != null) {
                                if (!v1.requestFocus(View.FOCUS_RIGHT)) {
                                    throw new IllegalStateException(
                                            "unfocucsable view");
                                }
                            }
                            break;
                        default:
                            break;
                        }
                        return true;
                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.kanjiSearchButton:
            // hideKeyboard();

            String kanjiInput = kanjiInputText.getText().toString();

            try {
                int searchTypeIdx = kanjiSearchTypeSpinner
                        .getSelectedItemPosition();
                String searchType = IDX_TO_CODE.get(searchTypeIdx);
                Log.i(TAG, Integer.toString(searchTypeIdx));
                Log.i(TAG, "kanji search type: " + searchType);
                if (searchType == null) {
                    // reading/kanji
                    searchType = "J";
                }

                String minStr = strokeCountMinInput.getText().toString();
                String maxStr = strokeCountMaxInput.getText().toString();
                Integer minStrokeCount = tryParseInt(minStr);
                Integer maxStrokeCount = tryParseInt(maxStr);
                SearchCriteria criteria = SearchCriteria.createWithStrokeCount(
                        kanjiInput, searchType, minStrokeCount, maxStrokeCount);

                Intent intent = new Intent(getActivity(),
                        KanjiResultListView.class);
                intent.putExtra(Constants.CRITERIA_KEY, criteria);

                if (!StringUtils.isEmpty(criteria.getQueryString())) {
                    dbHelper.addSearchCriteria(criteria);
                }

                Analytics.event("kanjiSearch", getActivity());

                startActivity(intent);
            } catch (RejectedExecutionException e) {
                Log.e(TAG, "RejectedExecutionException", e);
            }
            break;
        case R.id.selectRadicalButton:
            Intent i = new Intent(getActivity(), RadicalChart.class);

            startActivityForResult(i, Constants.RADICAL_RETURN_RESULT);
            break;
        default:
            // do nothing
        }
    }

    private Integer tryParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.RADICAL_RETURN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                Radical radical = (Radical) intent.getExtras().getSerializable(
                        Constants.RADICAL_KEY);
                kanjiInputText.setText(Integer.toString(radical.getNumber()));
                radicalEditText.setText(radical.getGlyph().substring(0, 1));
            }
        }
    }

    private void findViews() {
        kanjiInputText = (EditText) getView().findViewById(R.id.kanjiInputText);
        kanjiSearchTypeSpinner = (Spinner) getView().findViewById(
                R.id.kanjiSearchTypeSpinner);

        radicalEditText = (EditText) getView().findViewById(
                R.id.radicalInputText);
        strokeCountMinInput = (EditText) getView().findViewById(
                R.id.strokeCountMinInput);
        strokeCountMaxInput = (EditText) getView().findViewById(
                R.id.strokeCountMaxInput);
        selectRadicalButton = (Button) getView().findViewById(
                R.id.selectRadicalButton);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        if (inputTextFromBundle) {
            inputTextFromBundle = false;

            return;
        }

        switch (parent.getId()) {
        case R.id.kanjiSearchTypeSpinner:
            kanjiInputText.setText("");
            kanjiInputText.requestFocus();

            // radical number or number of strokes
            if (position == 1 || position == 2) {
                kanjiInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                kanjiInputText.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            if (position != 2) {
                toggleRadicalStrokeCountPanel(false);
            } else {
                toggleRadicalStrokeCountPanel(true);
            }
            break;
        }
    }

    private void toggleRadicalStrokeCountPanel(boolean isEnabled) {
        selectRadicalButton.setEnabled(isEnabled);
        strokeCountMinInput.setEnabled(isEnabled);
        strokeCountMinInput.setFocusableInTouchMode(isEnabled);
        strokeCountMaxInput.setEnabled(isEnabled);
        strokeCountMaxInput.setFocusableInTouchMode(isEnabled);
        if (!isEnabled) {
            strokeCountMinInput.setText("");
            strokeCountMaxInput.setText("");
            radicalEditText.setText("");
            kanjiInputText.requestFocus();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

}
