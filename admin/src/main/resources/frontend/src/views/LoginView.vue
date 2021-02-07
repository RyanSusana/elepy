<template>
  <div class="login-section">

    <div class="uk-flex-center uk-flex">


      <div id="login-app"
           class="login-box uk-card uk-margin-large-top uk-margin-xlarge-bottom">


        <div class=" logo">
          <img :src="$store.getters.logo"
               alt="logo">
        </div>


        <form @submit.prevent>

          <div class="login-box-content uk-padding">

            <div class="uk-margin">
              <label class="uk-form-label">{{ $t('elepy.ui.forms.username') }}</label>
              <div class="uk-form-controls">
                <input name="username" class="uk-input" v-model="username" type="text"
                       :placeholder="$t('elepy.ui.forms.username')">
              </div>
            </div>


            <div class="uk-margin">
              <label class="uk-form-label">{{ $t('elepy.ui.forms.password') }}</label>
              <div class="uk-form-controls">
                <input name="password" class="uk-input" v-model="password"
                       type="password"
                       :placeholder="$t('elepy.ui.forms.password')">
              </div>
            </div>

            <div class="uk-flex uk-flex-center uk-flex-row">
              <ActionButton type="submit" id="login-button" class=" uk-button-primary uk-width-1-1"
                            :action="login">{{ $t('elepy.ui.login') }}
              </ActionButton>


            </div>
            <div class="uk-flex uk-flex-right uk-width-1-1 uk-margin-top">

            </div>


            <OAuthButtons/>
            <div class="uk-flex uk-flex-center uk-margin-top uk-width-1-1">
              <LanguageSelect class="uk-width-3-5"></LanguageSelect>
            </div>

          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>

import ActionButton from '../components/base/ActionButton';
import LanguageSelect from "@/components/LanguageSelect";
import OAuthButtons from "@/components/OAuthButtons";

export default {
  name: "LoginView",
  components: {OAuthButtons, LanguageSelect, ActionButton},
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
  computed: {
    hasCode() {
      return this.$route.query && this.$route.query.code;
    }
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
    }
  }
}
</script>

<style>
.login-box {
  width: 448px;
}
</style>