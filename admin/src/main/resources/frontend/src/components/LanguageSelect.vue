<template>
  <select @change="switchLanguage" :value="locale" id="" class="language-select light "
          v-if="settings && Object.keys(settings.availableLocales).length>1">
    <option v-for="(localeName, code) in settings.availableLocales" :key="code" :value="code">{{ localeName }}</option>
  </select>
</template>
<style lang="scss">
.language-select {
  background: transparent !important;
  padding: .5em;
  color: #777;
  border-radius: 3px;
  width: 100%;
  min-height: 48px;
  font-size: 12px;

  border-width: 3px;
  border-color: rgba(#000, 0.15);

  &:active, &:focus {
    user-select: none;
    border-color: var(--primary-color);
    outline: 0;
  }

}
</style>
<script>
import { useMainStore } from "@/stores/main";
import { storeToRefs } from 'pinia'

export default {
  name: 'LanguageSelect',
  setup() {
    const store = useMainStore()
    const { locale, settings } = storeToRefs(store)
    
    return {
      locale,
      settings,
      switchLanguage: (event) => {
        return store.changeLocale(event.target.value);
      }
    }
  },
  data() {
    return {
      languages: {
        "en": "English",
        "nl": "Nederlands"
      }
    }
  }
}
</script>
