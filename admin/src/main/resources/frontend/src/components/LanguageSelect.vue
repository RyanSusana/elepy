<template>
  <select @change="switchLanguage" :value="locale" id="" class="language-select light "
          v-if="Object.keys(settings.availableLocales).length>1">
    <option v-for="(localeName, code) in settings.availableLocales" :value="code">{{ localeName }}</option>
  </select>
</template>
<style lang="scss">
.language-select {
  background: transparent !important;
  padding: .5em;
  color: #777;
  border-radius: 3px;
  width: 100%;
  height: 3em;
  font-size: 12px;


  &:active, &:focus {
    user-select: none;
    border: var(--primary-color) solid 1px;
    outline: 0;
  }

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
