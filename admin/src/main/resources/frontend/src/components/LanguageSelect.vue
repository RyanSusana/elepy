<template>
  <select @change="switchLanguage" :value="locale" id="" class="language-select uk-select"
          v-if="Object.keys(settings.availableLocales).length>1">
    <option v-for="(localeName, code) in settings.availableLocales" :value="code">{{ localeName }}</option>
  </select>
</template>
<style lang="scss">
.language-select {
  background-color: transparent;
  padding: 8px;
  color: #777;
}
</style>
<script>
import {mapState} from "vuex";

export default {
  name: 'LanguageSelect',
  computed: {
    ...mapState(['locale', 'settings']),
  },
  data() {
    return {
      languages: {
        "en": "English",
        "nl": "Nederlands"
      }
    }
  },
  methods: {
    switchLanguage(event) {
      return this.$store.dispatch('changeLocale', event.target.value);
    }
  }
}
</script>
