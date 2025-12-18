package com.zarvekule.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern DOUBLE_DASH = Pattern.compile("[-]{2,}");
    private static final Pattern DIACRITIC = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");


    public static String createSlug(String customSlug, String title) {
        String input = (customSlug != null && !customSlug.isBlank()) ? customSlug : title;

        if (input == null || input.isBlank()) {
            return "";
        }

        String slug = input.toLowerCase(Locale.forLanguageTag("tr"));

        slug = slug.replace("ı", "i")
                .replace("ö", "o")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ğ", "g")
                .replace("ç", "c");

        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = DIACRITIC.matcher(slug).replaceAll("");

        slug = WHITESPACE.matcher(slug).replaceAll("-");

        slug = NON_LATIN.matcher(slug).replaceAll("");

        slug = DOUBLE_DASH.matcher(slug).replaceAll("-");

        return slug.trim().replaceAll("^-|-$", "");
    }
}