<template>
    <select :value="value" @input="handleInput" class="uk-select" id name>
        <option
                :key="availableValue.enumValue"
                :value="availableValue.enumValue"
                v-for="availableValue in field.availableValues"
        >{{availableValue.enumName}}
        </option>
    </select>
</template>

<script>
    export default {
        props: ["value", "field"],
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
