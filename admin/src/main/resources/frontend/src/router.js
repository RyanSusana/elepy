import Vue from 'vue'
import VueRouter from 'vue-router'
import vue from "./main"

import store from './store'

const requireLogin = (to, from, next) => {

    function proceed() {
        if (store.getters.loggedIn) {
            next();
        }
    }

    store.watch(
        (state, getters) => getters.ready,
        (ready) => {
            if (ready === true) {
                if (!store.getters.loggedIn) {
                    next({
                        path: '/login',
                        query: {
                            redirect: to.fullPath,
                        },
                    });
                } else {
                    proceed();
                }
            } else {
                proceed()
            }
        },
        {immediate: true}
    )


};

const noLogin = (to, from, next) => {

    store.watch(
        (state, getters) => getters.elepyInitialized,
        (value) => {
            if (value === true && to.path !== '/login') {
                next({
                    path: '/login',
                });
            } else if (value === false && to.path !== '/initial-user') {
                next({
                    path: '/initial-user',
                });

            } else {
                next();
            }

        },
        {immediate: true}
    );

};
Vue.use(VueRouter)

const routes = [
    {
        path: '/login',
        component: () => import( './views/LoginView.vue'),
        meta: {
            title: "Login"
        },
        beforeEnter: noLogin
    },
    {
        path: '/initial-user',
        component: () => import( './views/InitialUserView.vue'),
        meta: {
            title: "Initial User"
        },
        beforeEnter: noLogin
    },
    {
        path: '/',
        component: () => import( './views/MainView.vue'),

        beforeEnter: requireLogin,
        children: [

            {
                path: '/',
                component: () => import( './views/HomeView.vue'),
                meta: {
                    title: "Home"
                },
            },
            {
                path: '/:modelPath',
                name: 'generic',
                component: () => import( './views/ModelView.vue'),
                meta: {
                    title: null,
                    auth: false
                },
            },
            {
                path: '/:modelPath/edit/:recordId',
                component: () => import( './views/EditRecordView.vue')
            },
            {
                path: '/:modelPath/add',
                component: () => import( './views/AddRecordView.vue')
            }
        ]
    },


];

const router = new VueRouter({
    mode: 'history',
    base: '/elepy/admin',
    routes,

    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return {x: 0, y: 0}
        }
    },
});

router.beforeEach((to, from, next) => {
    if (store.state.navigationWarning) {
        UIkit.modal.confirm(store.state.navigationWarning, {
            labels: {
                ok: vue.$t('elepy.ui.yes'),
                cancel: vue.$t('elepy.ui.no'),
            }
        }).then(
            () => {
                store.commit('CLEAR_NAVIGATION_WARNING')
                next();
            },
            () => {
            }
        );
    } else {
        next();
    }
})

export default router