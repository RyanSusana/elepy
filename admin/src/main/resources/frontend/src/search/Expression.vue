<template>

  <div class="uk-form-custom" tabindex="0">

    <div class="expression-creator uk-padding-small  uk-flex uk-flex-center uk-flex-middle"
         :class="{'complete': complete }">
      <div class="part" v-if="this.expression.property">{{ this.expression.property }}</div>
      <div class="part uk-text-primary uk-text-bold" v-if="this.expression.operator">{{
          this.expression.operator
        }}
      </div>
      <div class="part" v-if="this.expression.value">{{ this.expression.value }}</div>

      <div class="uk-relative" v-if="!complete">
        <input id="search-input" class="empty-form" v-model="content" v-on:keyup.enter="handleEnter"
               :key
               :placeholder="this.expression.property ? '':$t('elepy.ui.filter')"
               @keypress="focus"
               @keydown.down.prevent="$refs.suggestionsBox.focus()"
               @keyup.delete="handleDelete" ref="input"
               @focus="focus"
               @keydown.esc="unFocus"
        >
        <SuggestionsBox :current-value="content" :available-suggestions="suggestions" ref="suggestionsBox"
                        :focused="focused"

                        @input="handleEnter"/>
      </div>


      <a @click="emitDeleteSelf" class="uk-icon-link uk-margin-small-left" uk-icon="close" v-if="complete"></a>
    </div>

  </div>
</template>

<script>

import SuggestionsBox from "@/search/SuggestionsBox";

export default {
  name: "Expression",
  components: {SuggestionsBox},
  props: ["model", "expression", "autofocus"],
  data() {
    return {
      focused: false,
      content: "",
      contentOnLastBackspace: "",
    }
  },
  computed: {
    complete() {
      return !!this.expression.value;
    },
    filterableProperties() {
      return this.model.properties;
    },
    suggestions() {
      if (this.expression.value || this.expression.operator) return [];

      if (this.expression.property) {
        return this.getProperty(this.expression.property).availableFilters.flatMap(x => x.synonyms)
      } else {
        return this.filterableProperties.map(x => x.label);
      }
    },
    validProp() {
      return this.propIsValid(this.expression.property)
    }
  },
  methods: {
    focus() {
      this.focused = true;
      this.$refs["suggestionsBox"].showSuggestions();
      this.$refs.input.focus();
    },
    unFocus() {
      this.focused = false;
    },
    emitDeleteSelf() {
      this.$emit("deleteSelf")
    },
    emitDeletePrevious() {
      this.$emit("deletePrevious")
    },
    getProperty(name) {
      return this.filterableProperties.find(x => x.name === name || x.label === name)
    },
    operatorIsValid(operator) {
      return true;
    },
    propIsValid(prop) {
      let property = this.getProperty(prop);
      return !!property
    },

    getFilterPropertyForInput(input) {
      if (!this.expression.property) {
        if (this.propIsValid(input)) {
          return "property";
        }
      } else if (!this.expression.operator) {
        if (this.operatorIsValid(input)) {
          return "operator";
        } else {
          return null
        }
      }
      return "value";
    },
    handleDelete(e) {
      // Checks if string is blank and previous backspace was also blank
      if (!e.target.value && !this.contentOnLastBackspace) {

        if (!this.expression.property) this.emitDeletePrevious()
        else if (this.expression.value) this.expression.value = ""
        else if (this.expression.operator) this.expression.operator = ""
        else this.expression.property = ""
      }

      this.contentOnLastBackspace = e.target.value
    },
    handleEnter(e) {
      const input = (typeof e) === 'string' ? e : e.target.value;
      if (input) {
        let expressionPropertyForInput = this.getFilterPropertyForInput(input);

        this.expression[expressionPropertyForInput] = input;
        this.content = "";
        this.contentOnLastBackspace = input;
        this.$refs.input.focus();
      }
      if (this.complete) {
        this.$emit("complete")
      }
    }
  },
  mounted() {
    if (this.autofocus)
      this.$refs.input.focus();
  }
}
</script>

<style lang="scss" scoped>
@import "scss/main.scss";

.expression-creator {
  height: 18px;
  border-radius: 1000px;


  &.complete {
    background: darken($global-muted-background, 5%);
  }

  .part {
    margin-right: 4px;
  }

  *, input, input:focus {
    font-family: $global-font-family;
    line-height: 1 !important;
    font-size: 16px;
    padding: 0;
  }

  ::placeholder {
    color: #bbb;
  }
}

.empty-form:focus, .empty-form {
  font-size: 0.8rem;
  border: none;
  outline: none;
}

</style>