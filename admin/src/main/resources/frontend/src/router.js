import { createRouter, createWebHistory } from 'vue-router'
import { useMainStore } from './stores/main'
import utils from "@/utils";

const requireLogin = (to, from, next) => {
    const store = useMainStore()
    
    const unwatch = store.$subscribe((mutation, state) => {
        if (state.ready) {
            unwatch() // Stop watching
            
            if (store.loggedIn) {
                next();
            } else {
                next({
                    path: '/login',
                    query: {
                        redirect: to.fullPath,
                    },
                });
            }
        }
    }, { immediate: true })
};

const noLogin = (to, from, next) => {
    const store = useMainStore()
    
    if (to.query && to.query.code) {
        return store.loginOAuth(to.query).then(() => {
            return next(to.query.redirect ?? '/')
        }).catch(
            error => {
                utils.displayError(error)
                next('/login')
            }
        );
    }
    
    const unwatch = store.$subscribe((mutation, state) => {
        if (state.hasUsers !== null) {
            unwatch() // Stop watching
            
            if (state.hasUsers && (to.path !== '/login')) {
                next({
                    path: '/login',
                    query: to.query
                });
            } else if (!state.hasUsers && to.path !== '/initial-user') {
                next({
                    path: '/initial-user',
                    query: to.query
                });
            } else {
                next();
            }
        }
    }, { immediate: true })
};

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

const router = createRouter({
    history: createWebHistory('/elepy/admin'),
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
    const store = useMainStore()
    if (store.navigationWarning) {
        UIkit.modal.confirm(store.navigationWarning, {
            labels: {
                ok: 'Yes', // We'll fix this after filters are converted
                cancel: 'No',
            }
        }).then(
            () => {
                store.clearNavigationWarning()
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