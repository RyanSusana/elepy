<template>
    <div class>
        <div :key="field.name" class="uk-margin-bottom" v-for="field in model.properties">

            <GenericField
                    :field="field"
                    :fieldType="field.type"
                    :value="value[field.name]"
                    @input="handleInput(field.name, $event)"
                    :violations="violations"
            />
        </div>
    </div>
</template>


<script>
    import GenericField from "./GenericField.vue";

    export default {
        props: ["model", "value", "fieldType", "violations"],
        name: "ObjectField",
        components: {
            GenericField
        },
        methods: {
            handleInput(fieldName, e) {
                this.value[fieldName] = e;
                this.$emit("input", this.value);
                this.$forceUpdate();
            }
        }
    };
</script>
