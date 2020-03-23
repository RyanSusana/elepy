<template>
    <div class="login-section">

        <div class="uk-flex-center uk-flex">


            <div id="register-app"
                 class="login-box uk-card uk-width-1-4@xl uk-width-2-5@m uk-width-1-2@s uk-margin-large-top">


                <div class="uk-background-primary">
                    <img :src="$store.getters.logo"
                         alt="logo">
                </div>

                <form @submit.prevent>
                    <div class="login-box-content uk-padding">

                        <p>Create the initial super-user of the CMS. This user has the most privileges and can only be
                            created
                            once.</p>

                        <hr>
                        <div class="uk-margin">
                            <label class="uk-form-label">Username</label>
                            <div class="uk-form-controls">
                                <input name="username" class="uk-input" v-model="user.username" type="text"
                                       placeholder="Username">
                            </div>
                        </div>
                        <hr>
                        <div class="uk-margin">
                            <label class="uk-form-label">Password</label>
                            <div class="uk-form-controls">
                                <input name="password" class="uk-input" v-model="user.password"
                                       type="password"
                                       placeholder="Password">
                            </div>
                        </div>

                        <div class="uk-margin">
                            <label class="uk-form-label">Confirm Password</label>
                            <div class="uk-form-controls">
                                <input name="confirm-password" class="uk-input"
                                       v-model="confirmPassword"
                                       type="password"
                                       placeholder="Confirm Password">
                            </div>
                        </div>

                        <div class="uk-flex uk-flex-center">
                            <ActionButton type="submit" id="login-button" class="uk-width-small uk-button-primary" :action="register">
                                Create User
                            </ActionButton>

                        </div>
                    </div>
                </form>
            </div>


        </div>

    </div>
</template>

<script>
    import axios from "axios";
    import ActionButton from '../components/base/ActionButton';
    import Utils from '../utils';

    export default {
        name: "InitialUserView",
        components: {ActionButton},
        data() {
            return {
                confirmPassword: "",
                user: {
                    username: "",
                    password: ""
                }
            }
        },
        methods: {
            register() {
                if (this.confirmPassword === this.user.password) {
                    return axios.post("/users", this.user)
                        .then(() => this.$store.dispatch('init'))
                        .catch((error) => Utils.displayError(error));
                } else {
                    Utils.displayError("Passwords do not match!");
                }

            }
        }
    }
</script>

<style scoped>

</style>