import Vue from 'vue'
import App from './App.vue'
import store from "./store";
import Utils from "./utils"
import router from './router'
import axios from "axios"
import UIkit from 'uikit';

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
