import Vue from 'vue'
import App from './App.vue'
import store from "./store";
import Utils from "./utils"
import router from './router'
import axios from "axios"
import * as Vibrant from 'node-vibrant'

Vue.config.productionTip = false;


if (process.env.NODE_ENV !== 'production') {
    // This is the URL to the instance that gets run from 'develop_frontend.sh'
    axios.defaults.baseURL = 'http://localhost:7331';
}
new Vue({
    router,
    store,
    render: h => h(App),
}).$mount('#app');

Utils.url = "";
store.dispatch('init');


axios.interceptors.request.use(function (config) {

    config.popo = "ok";
    let uniqueId = Math.random().toString(36).substring(2) + Date.now().toString(36);

    let description = config.description ?? "Communicating with Elepy";

    config.requestInfo = {uniqueId, description}

    store.commit("ADD_LOAD_ITEM", config.requestInfo)
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
axios.interceptors.response.use(function (response) {
    store.commit("REMOVE_LOAD_ITEM", response.config.requestInfo.id);
    return response;
}, function (error) {
    store.commit("REMOVE_LOAD_ITEM", error.response.config.requestInfo.id);
    Utils.displayError(error);
    return Promise.reject(error);
});


function adjust(color, amount) {
    return '#' + color.replace(/^#/, '').replace(/../g, color => ('0' + Math.min(255, Math.max(0, parseInt(color, 16) + amount)).toString(16)).substr(-2));
}

Vibrant.from(axios.defaults.baseURL + '/elepy/logo').getPalette()
    .then((palette) => {
        document.documentElement.style
            .setProperty('--primary-color', palette.Vibrant.hex)
        document.documentElement.style
            .setProperty('--primary-hover-color', adjust(palette.Vibrant.hex, -10))
    });