<template>
    <div id="sidebar" class="">
        <a href="/admin" class="sidebar-header uk-flex uk-flex-center">
            <img class="banner-image" :src="$store.getters.logo" alt="logo">
        </a>
        <div class="uk-padding">
            <div class="models uk-light">
                <h4 class="uk-heading-bullet">Resources</h4>
                <ul class="link-list uk-list">

                    <li v-for="model in this.allModels" v-if="canExecute(model.defaultActions.find)" :key="model.name">
                        <router-link :to="model.path">{{model.name}}</router-link>
                    </li>

                </ul>
                <h4 class="uk-heading-bullet">More Links</h4>
                <ul class="link-list uk-list">

                    <li>
                        <router-link to="/">Admin Home</router-link>
                    </li>
                    <li><a @click="logOut">Log out</a></li>

                </ul>
            </div>
            <div class="sidebar-footer">
                <p>
                    Proudly powered by <a href="https://elepy.com" target="_blank">Elepy </a>
                </p>
            </div>
        </div>


    </div>
</template>
<script>
    import {mapGetters, mapState} from "vuex";

    export default {
        name: 'SideBar',

        computed: {
            ...mapState(['allModels']),
            ...mapGetters(['canExecute'])
        },
        methods: {
            logOut() {
                this.$store.dispatch('logOut').then(() => this.$router.push('/login'))
            }
        }
    }
</script>
<style lang="scss">

    html,
    body {
        height: 100%;
    }


    #sidebar {
        box-sizing: border-box;

        background-color: var(--sidebar-bg);

        top: 0;
        overflow-y: auto;
        height: 100vh;

        .sidebar-header {

            max-height: 150px;

            .banner-image {
                object-fit: cover;
            }
        }

    }

</style>
