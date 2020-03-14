<template>
    <input
            :value="(field.generated === true &&(value == null || value === '')) ? 'Generated' : value"
            @input="handleInput"
            class="uk-input"
            :type="field.type"
            v-bind:disabled="field.generated == true || (field.editable == false && (value !== null))"

    >
</template>

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
