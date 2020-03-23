<template>
    <div class="login-section">

        <div class="uk-flex-center uk-flex">


            <div id="login-app"
                 class="login-box uk-card uk-width-1-4@xl uk-width-2-5@m uk-width-1-2@s  uk-margin-large-top">


                <div class="uk-background-primary">
                    <img :src="$store.getters.logo"
                         alt="logo">
                </div>

                <form @submit.prevent>
                    <div class="login-box-content uk-padding">

                        <div v-if="initialUser">
                            <p>Login with your recently created user.</p>
                            <hr>
                        </div>

                        <div class="uk-margin">
                            <label class="uk-form-label">Username</label>
                            <div class="uk-form-controls">
                                <input name="username" class="uk-input" v-model="username" type="text"
                                       placeholder="Username">
                            </div>
                        </div>


                        <div class="uk-margin">
                            <label class="uk-form-label">Password</label>
                            <div class="uk-form-controls">
                                <input name="password" class="uk-input" v-model="password"
                                       type="password"
                                       placeholder="Password">
                            </div>
                        </div>

                        <div class="uk-flex uk-flex-center">
                            <ActionButton type="submit" id="login-button" class="uk-width-small uk-button-primary" :action="login">Login</ActionButton>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>

    import Utils from '../utils'
    import ActionButton from '../components/base/ActionButton';

    export default {
        name: "LoginView",
        components: {ActionButton},
        data() {
            return {
                username: "",
                password: "",
                keepLoggedIn: false,
                initialUser: false
            }
        },
        created: function () {
            this.initialUser = this.getQueryParams("initialUser") != null
        },
        methods: {

            getQueryParams: function (qs) {
                let url_string = window.location.href;
                let url = new URL(url_string);
                return url.searchParams.get(qs)
            },

            login() {
                return this.$store.dispatch("logIn", {username: this.username, password: this.password})
                    .then(() => {

                        this.$router.push(this.$route.query.redirect ?? '/')
                    })
                    .catch(error => {

                    });
            }
        }
    }
</script>

<style scoped>

</style>