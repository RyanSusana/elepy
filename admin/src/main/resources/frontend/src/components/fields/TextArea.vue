<template>
  <textarea
          :value="value"
          @input="handleInput"
          class="uk-input"
          rows="5"
          type="text"
          v-bind:disabled="field.generated == true || (field.editable == false && !creating)"

  ></textarea>
</template>

<style lang="scss">
    textarea {
        padding: 10px !important;
        min-height: 10em;
        line-height: 1.5em !important;
    }
</style>

<script>
    export default {
        props: ["field", "value"],
        data() {
            return {
                content: this.value
            };
        },
        methods: {
            handleInput(e) {
                this.$emit("input", e.target.value);
            }
        }
    };
</script>
