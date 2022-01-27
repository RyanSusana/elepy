<template>
  <div class="suggestions-box" v-if="shouldShow && (filteredDownSuggestions.length > 0)"
       v-on:keydown.tab="focusSuggestion(0)"
       ref="suggestions-box"
       uk-dropdown="mode: click">
    <ul class="suggestion-list">
      <li v-for="(suggestion, idx) of filteredDownSuggestions" tabindex="0" :ref="'suggestion-'+idx"
          v-on:click.prevent="handleClick(suggestion)"
          v-on:keyup.enter="handleClick(suggestion)"
          v-on:keydown.down.prevent="focusSuggestion(idx+1)"
          v-on:keydown.up.prevent="focusSuggestion(idx-1)"
          class="suggestion">
        {{ suggestion }}
      </li>
    </ul>
  </div>
</template>

<script>
import 'uikit'

export default {
  name: "SuggestionsBox",
  props: ["currentValue", "availableSuggestions", "focused"],
  computed: {
    filteredDownSuggestions() {
      return this.availableSuggestions.filter(x => x.toLowerCase().includes(this.currentValue.toLowerCase()))
    },
    itemIsFocused() {
      return false;
    },
    shouldShow() {
      return this.focused;
    }
  },
  methods: {
    showSuggestions() {
      UIkit.dropdown(this.$refs["suggestions-box"]).show();
    },
    focus() {
      this.focusSuggestion(0)
    },
    focusSuggestion(idx) {
      this.showSuggestions();
      const clampedIdx = Math.min(Math.max(idx, 0), this.filteredDownSuggestions.length - 1)
      let $ref = this.$refs['suggestion-' + clampedIdx];
      $ref[0].focus();
    },
    handleClick(suggestion) {
      this.$emit("input", suggestion)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "scss/main.scss";

.suggestions-box {
  //position: absolute;
  background: $global-muted-background;


  min-width: 192px;
  height: 196px;
  overflow-y: auto;
}

.suggestion-list {
  list-style: none;

  margin: 0;
  padding: 4px;

  .suggestion {
    padding: 4px;

    &:hover, &:focus {
      background: darken($global-muted-background, 10%);
      outline: none;
    }
  }
}
</style>