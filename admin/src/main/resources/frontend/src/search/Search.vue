<template>

  <div class="">

    <a class="uk-margin-small-right uk-icon-link " @click="focus(0)" uk-icon="settings"
       v-if="expressions.length === 1"></a>
    <a class="uk-margin-small-right uk-icon-link " @click="clear" v-else uk-icon="history"></a>
    <Expression :model="model" :expression="expression" v-for="(expression, idx) in expressions"
                :key="idx"
                :ref="'expression-'+idx"
                @complete="addNewExpression" @deletePrevious='deletePrevious' @deleteSelf="deleteIndex(idx)"
                class="uk-margin-small-right"
                :autofocus="idx>0"
    />
  </div>
</template>

<script>
import Expression from "@/search/Expression";

export default {
  name: "NewSearch",
  components: {Expression},
  props: ["model"],
  data() {
    return {
      expressions: [
        {
          property: "",
          operator: "",
          value: ""
        }
      ]
    };
  },


  computed: {
    queryString() {
      let query = "";
      for (let expression of this.expressions.filter(x => x.value)) {

        if (query.length > 0) {
          query = query.trim() + " AND "
        }

        if (expression.operator) {
          query += `(${expression.property} ${expression.operator} '${expression.value}')`;
        } else {
          query += `'${expression.value}'`
        }

      }
      return query;
    }
  },
  methods: {
    clear() {
      this.expressions = [];
      this.addNewExpression();
    },

    focus(idx) {
      this.$refs['expression-' + idx][0].focus();
    },
    deletePrevious() {
      if (this.expressions.length <= 1) return;

      this.deleteIndex(this.expressions.length - 2);
      this.focus(this.expressions.length - 2);
    },
    deleteIndex(i) {
      this.expressions.splice(i, 1);
      if (this.expressions.length === 0) {
        this.addNewExpression();
      } else {
        this.changeInput();
      }
    },
    addNewExpression() {
      this.expressions.push({
        property: "",
        operator: "",
        value: ""
      });

      this.changeInput();
    },
    changeInput() {
      this.$emit("input", this.queryString)
    }
  },

}
</script>

<style scoped>

</style>