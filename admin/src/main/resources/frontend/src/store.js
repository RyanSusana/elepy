import axios from "axios"
import Vue from "vue"
import Vuex from "vuex";
import {loadLanguage} from "./i18nSetup";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
    plugins: [createPersistedState({
        paths: ["token", "locale"]
    })],
    state: {
        allModels: [],
        locale: "en",
        ready: false,
        loggedInUser: null,
        token: null,
        hasUsers: null,
        settings: null,
        navigationWarning: null,
        selectedRows: [],
        loadingItems: [],
    },
    mutations: {
        CLEAR_NAVIGATION_WARNING(state) {
            state.navigationWarning = null

        },
        SET_NAVIGATION_WARNING(state, warning) {
            state.navigationWarning = warning;
        },
        SET_SETTINGS(state, settings) {
            state.settings = settings;
        },
        SET_LOCALE(state, lang) {
            state.locale = lang || "en-US";
        },
        SELECT_ROWS(state, rows) {
            state.selectedRows.push(...rows)
        },
        SET_SELECTED_ROWS(state, selectedRows) {
            state.selectedRows = selectedRows;
        },
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
        async getModels({commit, getters}) {
            if (!getters.loggedIn) {
                return;
            }
            const {data} = await axios.get("/elepy/schemas", {exceptionHandled: true});
            commit('SET_MODELS', data.filter(m => m.viewableOnCMS))


        },
        async changeLocale({dispatch, commit}, newLocale) {

            const lang = await loadLanguage(newLocale)
            commit('SET_LOCALE', lang)

            try {
                dispatch('getModels');
            } catch (e) {
                console.warn(e);
            }
        },
        async init({dispatch, commit, state}) {
            console.debug('initializing store')
            await dispatch('changeLocale', state.locale)

            await axios.get("/elepy/settings", {exceptionHandled: true}).then(({data}) => {
                commit('SET_SETTINGS', data)
            })
            await axios.get("/elepy/has-users", {exceptionHandled: true})
                .then(() =>
                    commit('SET_HAS_USERS', true))
                .catch(error => {
                        error.handled = true;
                        commit('SET_HAS_USERS', false)
                    }
                );

            if (state.token != null) {
                return dispatch('logInWithToken', state.token)
                    .catch(error => {
                        error.response.handled = true
                        return window.localStorage.removeItem('token');
                    })
                    .finally(() => commit("READY"));
            } else {
                return commit("READY");
            }
        },
        async logInWithToken({commit, dispatch}, loginResponseToken, oauth) {
            delete axios.defaults.headers["authorization"];
            let userResponse = (await axios({
                url: "/elepy/logged-in-user",
                method: 'get',
                headers: {'Authorization': 'Bearer ' + loginResponseToken},
                exceptionHandled: !oauth && loginResponseToken !== null
            })).data;

            axios.defaults.headers.authorization = 'Bearer ' + loginResponseToken;


            commit("SET_USER", userResponse);

            await dispatch('getModels');
            commit("SET_TOKEN", loginResponseToken)

            // auto-logout
            const timeout = userResponse.maxDate - Date.now();
            setTimeout(() => {
                dispatch('logOut');

                window.location.reload();
            }, timeout)
        },
        async loginOAuth({dispatch}, query) {
            let loginResponseToken = (await axios({
                url: "/elepy/token-login",
                method: 'post',
                params: query
            })).data;
            return dispatch('logInWithToken', loginResponseToken, true)

        },
        async logIn({dispatch}, loginAttempt) {

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

        logOut({commit, getters}) {
            document.cookie = "ELEPY_TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.localStorage.removeItem('token');
            delete axios.defaults.headers["authorization"];
            commit("SET_USER", null);
            commit("SET_TOKEN", null);
        }
    },
    getters: {
        getModel: state => (modelPath) => state.allModels.filter((m) => m.path.includes(modelPath))[0],

        canExecute: state => action => {
            if (state.loggedInUser == null) {
                return false;
            }
            return true;
            let grantedPermissions = state.loggedInUser.permissions ?? [];
            let requiredPermissions = action.requiredPermissions ?? [];

            if (requiredPermissions.includes("disabled")) {
                return false;
            }

            return requiredPermissions
                .every(requiredPermission => grantedPermissions
                    .some(grantedPermission => matchRuleShort(requiredPermission, grantedPermission)));

        },

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

function matchRuleShort(str, rule) {
    if (str === 'authenticated') {
        return true;
    }
    let escapeRegex = (str) => str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    return new RegExp("^" + rule.split("*").map(escapeRegex).join(".*") + "$").test(str);
}