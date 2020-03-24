import axios from "axios"
import Vue from "vue"
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
    plugins: [createPersistedState()],
    state: {
        allModels: [],
        ready: false,
        loggedInUser: null,
        token: null,
        hasUsers: null,
        settings: null,
        loadingItems: []
    },
    mutations: {
        SET_MODELS(state, models) {
            state.allModels = models;
        },
        SET_USER(state, user) {
            state.loggedInUser = user
        },
        SET_TOKEN(state, token) {
            state.token = token;
        },
        SET_HAS_USERS(state, value) {
            state.hasUsers = value
        },
        READY(state) {
            state.ready = true;
        },

        ADD_LOAD_ITEM(state, loadItem) {
            if (typeof loadItem == "string") {
                let uniqueId = Math.random().toString(36).substring(2) + Date.now().toString(36);
                state.loadingItems.push({id: uniqueId, description: loadItem})
            } else {
                state.loadingItems.push(loadItem);
            }
        },

        REMOVE_LOAD_ITEM(state, loadItemId) {
            state.loadingItems = state.loadingItems.filter(item => item.id !== loadItemId);
        },

    },
    actions: {
        async getModels({commit}) {
            return axios.get("/elepy/config")
                .then(response => commit('SET_MODELS', response.data.filter(m => m.viewableOnCMS)));
        },

        async init({dispatch, commit, state}) {
            await axios.get("/elepy/has-users")
                .then(() =>
                    commit('SET_HAS_USERS', true))
                .catch(() =>
                    commit('SET_HAS_USERS', false));

            if (state.token != null) {
                return dispatch('logInWithToken', state.token)
                    .catch(() => window.localStorage.removeItem('token'))
                    .finally(() => commit("READY"));
            } else {
                return commit("READY");
            }
        },
        async logInWithToken({commit, dispatch}, loginResponseToken) {
            let userResponse = (await axios({
                url: "/elepy/logged-in-user",
                method: 'get',
                headers: {'Authorization': 'Bearer ' + loginResponseToken}
            })).data;

            await dispatch('getModels');

            axios.defaults.headers.authorization = 'Bearer ' + loginResponseToken;

            commit("SET_USER", userResponse);
            commit("SET_TOKEN", loginResponseToken)
        },
        async logIn({dispatch}, loginAttempt) {
            delete axios.defaults.headers["authorization"];
            let loginResponseToken = (await axios({
                url: "/elepy/token-login",
                method: 'post',
                auth: {
                    username: loginAttempt.username,
                    password: loginAttempt.password
                }
            })).data;
            return dispatch('logInWithToken', loginResponseToken)
        },

        logOut({commit}) {
            document.cookie = "ELEPY_TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.localStorage.removeItem('token');
            delete axios.defaults.headers["authorization"];
            commit("SET_USER", null);
            commit("SET_TOKEN", null)
        }
    },
    getters: {
        getModel: state => (modelPath) => state.allModels.filter((m) => m.path.includes(modelPath))[0],

        isModerator: (state, getters) =>
            getters.loggedIn && (
            state.loggedInUser.permissions.includes("moderator")
            || state.loggedInUser.permissions.includes("owner")),

        loggedIn: state => state.loggedInUser != null,

        elepyInitialized: state => {
            return state.hasUsers
        },

        ready: state => state.ready === true,
        isLoading: (state) => state.loadingItems.length > 0,

        logo: () => (axios.defaults.baseURL ?? '') + "/elepy/logo"

    }
});