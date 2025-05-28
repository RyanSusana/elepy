import { createI18n } from 'vue-i18n'
import axios from 'axios'

import IntlFormatter from "./intlFormatter";

const formatter = new IntlFormatter();

export const i18n = createI18n({
    legacy: false,
    fallbackLocale: 'en',
    messageFormat: formatter
})

export function loadLanguage(lang) {

    // If the language was already loaded
    if (loadedLanguages.includes(lang)) {
        return Promise.resolve(setI18nLanguage(lang))
    } else {
        return axios.get('/elepy/translations', {
            headers: {
                'Accept-Language': lang
            }
        }).then(response => {
            i18n.global.setLocaleMessage(lang, response.data)
            loadedLanguages.push(lang)
            return setI18nLanguage(lang)
        });
    }


}

const loadedLanguages = [];

function setI18nLanguage(lang) {
    console.debug(`setting i18n = ${lang}`)
    i18n.global.locale.value = lang
    axios.defaults.headers['Accept-Language'] = lang
    document.querySelector('html').setAttribute('lang', lang)
    formatter.setLocale(lang);
    return lang
}


