package com.linhtn.authory_service.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class I18nHelper {
    private final MessageSource messageSource;

    public I18nHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, key, LocaleContextHolder.getLocale());
    }
}
