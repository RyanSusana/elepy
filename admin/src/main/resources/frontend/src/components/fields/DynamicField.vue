<template>
  <multiselect
      v-model="selected"
      :id="_uid"
      placeholder="Type to select"
      open-direction="bottom"
      :options="foundRecords "
      :multiple="false"
      :searchable="field.queryable"
      :loading="isLoading"
      :internal-search="true"
      :options-limit="300"
      :limit="10"
      :max-height="600"

      @input="handleInput"
  />

</template>

<script>
import Multiselect from "vue-multiselect";

export default {
  name: "DynamicField",
  components: {
    Multiselect
  },
  data() {
    return {
      isLoading: false,
      initialLoaded: false,
      selected: null,
      foundRecords: [],

    }
  },
  props: ["value", "field", "root", "parent"],
  methods: {
    handleInput(input) {

    }
  }
}
</script>

<style lang="scss" scoped>

</style>