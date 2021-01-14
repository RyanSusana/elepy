<template>
  <div class="login-section">

    <div class="uk-flex-center uk-flex">


      <div id="register-app"
           class="login-box uk-card uk-margin-top uk-margin-large-bottom">


        <div class="uk-background-primary">
          <img :src="$store.getters.logo"
               alt="logo">
        </div>

        <form @submit.prevent>
          <div class="login-box-content uk-padding">

            <p>{{ $t('elepy.ui.forms.initialMessage') }}</p>

            <hr>
            <div class="uk-margin">
              <label class="uk-form-label">{{ $t('elepy.ui.forms.username') }}</label>
              <div class="uk-form-controls">
                <input name="username" class="uk-input" v-model="user.username" type="text"
                       :placeholder="$t('elepy.ui.forms.username') ">
              </div>
            </div>
            <hr>
            <div class="uk-margin">
              <label class="uk-form-label">{{ $t('elepy.ui.forms.password') }}</label>
              <div class="uk-form-controls">
                <input name="password" class="uk-input" v-model="user.password"
                       type="password"
                       :placeholder="$t('elepy.ui.forms.password')">
              </div>
            </div>

            <div class="uk-margin">
              <label class="uk-form-label">{{ $t('elepy.ui.forms.confirmPassword') }}</label>
              <div class="uk-form-controls">
                <input name="confirm-password" class="uk-input"
                       v-model="confirmPassword"
                       type="password"
                       :placeholder="$t('elepy.ui.forms.confirmPassword')">
              </div>
            </div>

            <div class="uk-flex uk-flex-center">
              <ActionButton type="submit" id="login-button" class="uk-width-1-1 uk-button-primary"
                            :action="register">
                {{ $t('elepy.ui.forms.createUser') }}
              </ActionButton>

            </div>
            <div class="uk-flex uk-flex-right uk-margin-top uk-width-1-1">
              <LanguageSelect class="uk-width-small"></LanguageSelect>
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
import LanguageSelect from "@/components/LanguageSelect";

export default {
  name: "InitialUserView",
  components: {LanguageSelect, ActionButton},
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
            .then(() => this.$store.dispatch('init'));
      } else {
        Utils.displayError("Passwords do not match!");
      }

    }
  }
}
</script>
<style>
.login-box {
  width: 448px;
}
</style>