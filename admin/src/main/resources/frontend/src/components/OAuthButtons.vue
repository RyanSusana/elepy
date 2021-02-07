<template>

  <div class="" v-if="schemes.length">
    <p class="hr-text uk-text-small uk-width-3-4 uk-align-center uk-text-muted uk-margin-top uk-margin-bottom">
      {{ $t('elepy.ui.forms.orContinueWith') }}</p>
    <div class="button-list">

      <a :href="getUrl(scheme)" class="button" v-for="scheme in schemes"
      ><img class="icon"
            :src="scheme.icon"
            alt=""><span class="text">{{ scheme.scheme }}</span>

      </a>
    </div>
    <hr class="uk-width-3-4 uk-align-center">
  </div>


</template>

<script>
import axios from "axios";

export default {
  name: "OAuthButtons",
  data() {
    return {
      schemes: []
    }
  },
  methods: {
    getUrl(scheme) {
      const loc = window.location;
      const redirectUri = `${loc.protocol}//${loc.host}/elepy/admin/login`
      return axios.defaults.baseURL + `/elepy/auth-url?scheme=${scheme.scheme}&redirect_uri=${redirectUri}`
    },
    async loadSchemes() {
      try {

        const response = await axios.get("/elepy/auth-schemes", {exceptionHandled: true})
        this.schemes = response.data;
      } catch {
        this.schemes = []
      }

    }
  },
  mounted() {
    this.loadSchemes();
  }
}
</script>

<style lang="scss" scoped>
.button-list {

  display: flex;
  width: 100%;
  flex-wrap: wrap;
  justify-content: center;

  box-sizing: border-box;

  .button {
    flex: 1 1 0px;
    margin: 6px;
    outline: none;

    background: #eee;
    padding: 16px 36px;
    border-radius: 4px;
    font-weight: bold;
    color: #777;
    text-align: center;

    box-sizing: border-box;
    transition: all .2s;
    display: flex;

    align-items: center;
    align-content: center;

    justify-content: center;

    &:hover {
      text-decoration: none;
      background: #ddd;
    }

    &:nth-child(n+3) {
      max-width: 50%;
    }

    .text {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }

    .icon {
      height: 24px;
      max-width: 30px;
      object-fit: contain;
      margin-right: 12px;
    }

  }
}
</style>