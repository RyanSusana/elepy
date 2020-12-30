import Vue from 'vue'
import VueI18n from 'vue-i18n'
import axios from 'axios'

import IntlFormatter from "./intlFormatter";

const formatter = new IntlFormatter();
Vue.use(VueI18n)

export const i18n = new VueI18n({
    fallbackLocale: 'en',
    formatter: formatter
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
            i18n.setLocaleMessage(lang, response.data)
            loadedLanguages.push(lang)
            return setI18nLanguage(lang)
        });
    }


}

const loadedLanguages = [];

function setI18nLanguage(lang) {
    console.debug(`setting i18n = ${lang}`)
    i18n.locale = lang
    axios.defaults.headers['Accept-Language'] = lang
    document.querySelector('html').setAttribute('lang', lang)
    formatter.setLocale(lang);
    return lang
}


