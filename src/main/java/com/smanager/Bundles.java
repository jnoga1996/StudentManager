package com.smanager;

import com.smanager.dao.models.Bundle;
import com.smanager.dao.repositories.BundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

public class Bundles {

    private static BundleRepository bundleRepository;

    @Autowired
    public Bundles(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    public static String getMessage(String bundle, String key) {
        Locale locale = LocaleContextHolder.getLocale();
        String localeValue = locale.getLanguage().toLowerCase();
        Bundle localizedBundle = bundleRepository.findBundle(localeValue, bundle, key);
        return localizedBundle.getMessage();
    }
}
