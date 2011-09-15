package org.nick.wwwjdic.model;

import java.io.Serializable;


public class SearchCriteria extends WwwjdicQuery implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6703864987202245997L;

    private static final String KANJI_TEXT_LOOKUP_CODE = "J";
    private static final String KANJI_RADICAL_LOOKUP_CODE = "B";
    private static final String ENGLISH_MEANING_LOOKUP_CODE = "E";

    private static final String DICTIONARY_CODE_GENERAL = "1";

    private static final int DEFAULT_MAX_RESULTS = 20;

    public static final int CRITERIA_TYPE_DICT = 0;
    public static final int CRITERIA_TYPE_KANJI = 1;
    public static final int CRITERIA_TYPE_EXAMPLES = 2;

    public static final int KANJI_COMPOUND_SEARCH_TYPE_NONE = 0;
    public static final int KANJI_COMPOUND_SEARCH_TYPE_STARTING = 1;
    public static final int KANJI_COMPOUND_SEARCH_TYPE_ANY = 2;

    private Long id;

    private final int type;

    private boolean isExactMatch;
    private boolean isKanjiLookup;
    private boolean isRomanizedJapanese;
    private boolean isCommonWordsOnly;
    private int kanjiCompoundSearchType;
    private String dictionaryCode;
    private String kanjiSearchType;
    private Integer minStrokeCount;
    private Integer maxStrokeCount;
    private Integer numMaxResults;

    public static SearchCriteria createForDictionary(String queryString,
            boolean isExactMatch, boolean isRomanized,
            boolean isCommonWordsOnly, String dictionary) {
        return new SearchCriteria(CRITERIA_TYPE_DICT, queryString,
                isExactMatch, false, isRomanized, isCommonWordsOnly,
                KANJI_COMPOUND_SEARCH_TYPE_NONE, dictionary, null, null, null,
                null);
    }

    public static SearchCriteria createForDictionaryDefault(String queryString) {
        return createForDictionary(queryString, false, false, false,
                DICTIONARY_CODE_GENERAL);
    }

    public static SearchCriteria createForKanjiCompounds(String queryString,
            int compoundSearchType, boolean isCommonWordsOnly, String dictionary) {
        return new SearchCriteria(CRITERIA_TYPE_DICT, queryString, false,
                false, false, isCommonWordsOnly, compoundSearchType,
                dictionary, null, null, null, null);
    }

    public static SearchCriteria createForKanji(String queryString,
            String searchType) {
        return new SearchCriteria(CRITERIA_TYPE_KANJI, queryString, false,
                true, true, false, KANJI_COMPOUND_SEARCH_TYPE_NONE, null,
                searchType, null, null, null);
    }

    public static SearchCriteria createForKanjiOrReading(String queryString) {
        return new SearchCriteria(CRITERIA_TYPE_KANJI, queryString, false,
                true, true, false, KANJI_COMPOUND_SEARCH_TYPE_NONE, null,
                KANJI_TEXT_LOOKUP_CODE, null, null, null);
    }

    public static SearchCriteria createWithStrokeCount(String queryString,
            String searchType, Integer minStrokeCount, Integer maxStrokeCount) {
        return new SearchCriteria(CRITERIA_TYPE_KANJI, queryString, false,
                true, true, false, KANJI_COMPOUND_SEARCH_TYPE_NONE, null,
                searchType, minStrokeCount, maxStrokeCount, null);
    }

    public static SearchCriteria createForExampleSearch(String queryString,
            boolean isExactMatch, int numMaxResults) {
        return new SearchCriteria(CRITERIA_TYPE_EXAMPLES, queryString,
                isExactMatch, false, false, false,
                KANJI_COMPOUND_SEARCH_TYPE_NONE, null, null, null, null,
                numMaxResults);
    }

    public static SearchCriteria createForExampleSearchDefault(
            String queryString) {
        return createForExampleSearch(queryString, false, DEFAULT_MAX_RESULTS);
    }

    private SearchCriteria(int type, String queryString, boolean isExactMatch,
            boolean isKanjiLookup, boolean isRomanizedJapanese,
            boolean isCommonWordsOnly, int kanjiCompoundSearchType,
            String dictionary, String kanjiSearchType, Integer minStrokeCount,
            Integer maxStrokeCount, Integer numMaxResults) {
        super(queryString);
        this.type = type;
        this.queryString = queryString;
        this.isExactMatch = isExactMatch;
        this.isKanjiLookup = isKanjiLookup;
        this.isRomanizedJapanese = isRomanizedJapanese;
        this.isCommonWordsOnly = isCommonWordsOnly;
        this.kanjiCompoundSearchType = kanjiCompoundSearchType;
        this.dictionaryCode = dictionary;
        this.kanjiSearchType = kanjiSearchType;
        this.minStrokeCount = minStrokeCount;
        this.maxStrokeCount = maxStrokeCount;
        this.numMaxResults = numMaxResults;
    }

    public boolean isExactMatch() {
        return isExactMatch;
    }

    public boolean isKanjiLookup() {
        return isKanjiLookup;
    }

    public boolean isRomanizedJapanese() {
        return isRomanizedJapanese;
    }

    public boolean isCommonWordsOnly() {
        return isCommonWordsOnly;
    }

    public String getDictionaryCode() {
        return dictionaryCode;
    }

    public String getKanjiSearchType() {
        return kanjiSearchType;
    }

    public boolean isKanjiCodeLookup() {
        return !KANJI_TEXT_LOOKUP_CODE.equals(kanjiSearchType)
                && !ENGLISH_MEANING_LOOKUP_CODE.equals(kanjiSearchType);
    }

    public boolean isKanjiRadicalLookup() {
        return KANJI_RADICAL_LOOKUP_CODE.equals(kanjiSearchType);
    }

    public Integer getMinStrokeCount() {
        return minStrokeCount;
    }

    public Integer getMaxStrokeCount() {
        return maxStrokeCount;
    }

    public boolean hasStrokes() {
        return minStrokeCount != null || maxStrokeCount != null;
    }

    public boolean hasMinStrokes() {
        return minStrokeCount != null;
    }

    public boolean hasMaxStrokes() {
        return maxStrokeCount != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNarrowedDown() {
        return isExactMatch || isCommonWordsOnly;
    }

    public Integer getNumMaxResults() {
        return numMaxResults;
    }

    public int getType() {
        return type;
    }

    public boolean isKanjiCompoundSearch() {
        return kanjiCompoundSearchType != KANJI_COMPOUND_SEARCH_TYPE_NONE;
    }

    public int getKanjiCompoundSearchType() {
        return kanjiCompoundSearchType;
    }

    public boolean isStartingKanjiCompoundSearch() {
        return kanjiCompoundSearchType == KANJI_COMPOUND_SEARCH_TYPE_STARTING;
    }

}
