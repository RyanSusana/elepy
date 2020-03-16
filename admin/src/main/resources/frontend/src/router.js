import Vue from 'vue'
import VueRouter from 'vue-router'

import store from './store'

const requireLogin = (to, from, next) => {

    function proceed() {
        if (store.getters.loggedIn) {
            next();
        }
    }

    store.watch(
        (state, getters) => getters.ready,
        (value) => {
            if (value === true) {
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
        beforeEnter: noLogin
    },
    {
        path: '/initial-user',
        component: () => import( './views/InitialUserView.vue'),
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
            },
            {
                path: '/:modelPath',
                name: 'generic',
                // route level code-splitting
                // this generates a separate chunk (about.[hash].js) for this route
                // which is lazy-loaded when the route is visited.
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
    base: '/elepy-admin',
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return {x: 0, y: 0}
        }
    },
});

export default router