<template>
    <input
            :value="(field.generated === true &&(value == null || value === '')) ? 'Generated' : value"
            @input="handleInput"
            class="uk-input"
            :class="{'error': violations.length}"
            :type="field.inputType"
            v-bind:disabled="field.generated == true || (field.editable == false && (value !== null))"

    >
</template>

<style lang="scss" scoped>

    @import "scss/main";
    .error {
        border-color: $global-danger-background;
    }
</style>
<script>
    export default {
        props: ["field", "value", "violations"],
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
