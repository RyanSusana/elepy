<template>
  <div class>
    <div :key="field.name" class="uk-margin-bottom"
         v-for="(field, idx) in model.properties">

      <div class="" v-if="!root"></div>
      <GenericField
          :field="field"
          :fieldType="field.type"
          :value="value[field.name]"
          @input="handleInput(field.name, $event)"
          :violations="violations"
          :root="root"
          :parent="value"
      />
    </div>
  </div>
</template>


<script>
import GenericField from "./GenericField.vue";

export default {
  props: ["model", "value", "fieldType", "violations", "root"],
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
