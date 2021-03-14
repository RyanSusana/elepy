import Vue from 'vue'
import App from './App.vue'
import store from "./store";
import Utils from "./utils"
import router from './router'
import axios from "axios"

const moment = require('moment');
import * as Vibrant from 'node-vibrant'
import {i18n} from "./i18nSetup";

if (process.env.NODE_ENV !== 'production') {
    // This is the URL to the instance that gets run from 'develop_frontend.sh'
    axios.defaults.baseURL = 'http://localhost:7331';
}
const vue = new Vue({
    i18n,
    router,
    store,
    render: h => h(App),
}).$mount('#app');


Vue.config.productionTip = false;


Utils.url = "";
store.dispatch('init');

Vue.filter("t", value => {
    if (!value) return "";


    return i18next.t(value);
});

Vue.filter("relativeTime", value => {
    if (!value) return "";

    return moment(value).fromNow();

});
axios.interceptors.request.use(function (config) {

    const uniqueId = Math.random().toString(36).substring(2) + Date.now().toString(36);

    const timestamp = Date.now();
    const description = config.description ?? "Communicating with Elepy";

    config.requestInfo = {uniqueId, description, timestamp}

    store.commit("ADD_LOAD_ITEM", config.requestInfo)
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
axios.interceptors.response.use((response) => {
    store.commit("REMOVE_LOAD_ITEM", response.config.requestInfo.id);
    logResponse(response);
    return response;
}, (error) => {
    if (error.response && !error.response.config.exceptionHandled) {
        store.commit("REMOVE_LOAD_ITEM", error.response.config.requestInfo.id);
        logResponse(error.response)
        Utils.displayError(error);
    } else if (!error.response) {
        console.error(error)
    }
    return Promise.reject(error);
});

function logResponse(response) {
    const timeSpent = Date.now() - response.config.requestInfo.timestamp
    console.debug(`[${timeSpent}ms]\t${response.status} ${response.config.method.toUpperCase()}\t - ${response.config.url}`)
}

function adjust(color, amount) {
    return '#' + color.replace(/^#/, '').replace(/../g, color => ('0' + Math.min(255, Math.max(0, parseInt(color, 16) + amount)).toString(16)).substr(-2));
}

Vibrant.from(store.getters.logo).getPalette()
    .then((palette) => {

        let primary = palette.Vibrant.hex;
        let primaryHover = adjust(palette.Vibrant.hex, -10);
        let primaryDisabled = palette.DarkVibrant.hsl;


        primaryDisabled = "hsla(" + primaryDisabled[0] * 360 + ", 75%, 82%, 1)";


        document.documentElement.style
            .setProperty('--primary-color', primary);
        document.documentElement.style
            .setProperty('--primary-hover-color', primaryHover);

        document.documentElement.style
            .setProperty('--primary-disabled-color', primaryDisabled);
    });

export default vue