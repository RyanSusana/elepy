import { defineStore } from 'pinia'
import axios from "axios"
import { loadLanguage } from "../i18nSetup"

export const useMainStore = defineStore('main', {
    state: () => ({
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
    }),

    getters: {
        getModel: (state) => (modelPath) => state.allModels.filter((m) => m.path.includes(modelPath))[0],

        canExecute: (state) => (action) => {
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

        isModerator: (state) => {
            return state.loggedInUser != null && (
                state.loggedInUser.permissions.includes("moderator")
                || state.loggedInUser.permissions.includes("owner"));
        },

        loggedIn: (state) => state.loggedInUser != null,

        elepyInitialized: (state) => {
            return state.hasUsers
        },

        ready: (state) => state.ready === true,
        isLoading: (state) => state.loadingItems.length > 0,

        logo: () => (axios.defaults.baseURL ?? '') + "/elepy/logo"
    },

    actions: {
        clearNavigationWarning() {
            this.navigationWarning = null
        },

        setNavigationWarning(warning) {
            this.navigationWarning = warning;
        },

        setSettings(settings) {
            this.settings = settings;
        },

        setLocale(lang) {
            this.locale = lang || "en-US";
        },

        selectRows(rows) {
            this.selectedRows.push(...rows)
        },

        setSelectedRows(selectedRows) {
            this.selectedRows = selectedRows;
        },

        setModels(models) {
            this.allModels = models;
        },

        setUser(user) {
            this.loggedInUser = user
        },

        setToken(token) {
            this.token = token;
        },

        setHasUsers(value) {
            this.hasUsers = value
        },

        setReady() {
            this.ready = true;
        },

        addLoadItem(loadItem) {
            if (typeof loadItem == "string") {
                let uniqueId = Math.random().toString(36).substring(2) + Date.now().toString(36);
                this.loadingItems.push({id: uniqueId, description: loadItem})
            } else {
                this.loadingItems.push(loadItem);
            }
        },

        removeLoadItem(loadItemId) {
            this.loadingItems = this.loadingItems.filter(item => item.id !== loadItemId);
        },

        async getModels() {
            if (!this.loggedIn) {
                return;
            }
            const {data} = await axios.get("/elepy/schemas", {exceptionHandled: true});
            this.setModels(data.filter(m => m.viewableOnCMS))
        },

        async changeLocale(newLocale) {
            const lang = await loadLanguage(newLocale)
            this.setLocale(lang)

            try {
                this.getModels();
            } catch (e) {
                console.warn(e);
            }
        },

        async init() {
            console.debug('initializing store')
            await this.changeLocale(this.locale)

            await axios.get("/elepy/locales", {exceptionHandled: true}).then(({data}) => {
                this.setSettings(data)
            })
            await axios.get("/elepy/has-users", {exceptionHandled: true})
                .then(() =>
                    this.setHasUsers(true))
                .catch(error => {
                        error.handled = true;
                        this.setHasUsers(false)
                    }
                );

            if (this.token != null) {
                return this.logInWithToken(this.token)
                    .catch(error => {
                        error.response.handled = true
                        return window.localStorage.removeItem('token');
                    })
                    .finally(() => this.setReady());
            } else {
                return this.setReady();
            }
        },

        async logInWithToken(loginResponseToken, oauth) {
            delete axios.defaults.headers["authorization"];
            let userResponse = (await axios({
                url: "/elepy/logged-in-user",
                method: 'get',
                headers: {'Authorization': 'Bearer ' + loginResponseToken},
                exceptionHandled: !oauth && loginResponseToken !== null
            })).data;

            axios.defaults.headers.authorization = 'Bearer ' + loginResponseToken;

            this.setUser(userResponse);

            await this.getModels();
            this.setToken(loginResponseToken)

            // auto-logout
            const timeout = userResponse.maxDate - Date.now();
            setTimeout(() => {
                this.logOut();
                window.location.reload();
            }, timeout)
        },

        async loginOAuth(query) {
            let loginResponseToken = (await axios({
                url: "/elepy/token-login",
                method: 'post',
                params: query
            })).data;
            return this.logInWithToken(loginResponseToken, true)
        },

        async logIn(loginAttempt) {
            let loginResponseToken = (await axios({
                url: "/elepy/token-login",
                method: 'post',
                auth: {
                    username: loginAttempt.username,
                    password: loginAttempt.password
                }
            })).data;
            return this.logInWithToken(loginResponseToken)
        },

        logOut() {
            document.cookie = "ELEPY_TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.localStorage.removeItem('token');
            delete axios.defaults.headers["authorization"];
            this.setUser(null);
            this.setToken(null);
        }
    },

    persist: {
        paths: ["token", "locale"]
    }
})

function matchRuleShort(str, rule) {
    if (str === 'authenticated') {
        return true;
    }
    let escapeRegex = (str) => str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    return new RegExp("^" + rule.split("*").map(escapeRegex).join(".*") + "$").test(str);
}