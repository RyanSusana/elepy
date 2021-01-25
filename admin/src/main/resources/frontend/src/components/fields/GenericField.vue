<template>
  <div :property="field.name" v-if="showField">
    <div
        v-if="!partOfArray">

      <h3 class="uk-text-emphasis uk-text-bold label" :class="{'uk-text-danger': hasErrors}">{{ field.label }}<span
          v-if="hasErrors"
          uk-icon="warning" class="uk-margin-small-left"></span></h3>
      <p v-if="field.description" class="description uk-text-muted">{{ field.description }}</p>

      <ul>
        <li class="uk-text-danger" v-for="violation in formattedViolations">{{ violation.message }}</li>
      </ul>
    </div>


    <DatePicker
        :field="field"
        :value="value"
        @input="handleInput"
        v-if="trueFieldType === 'DATE'"
        :root="root"
        :parent="parent"
    />

    <CustomField
        :field="field"
        :value="value"
        @input="handleInput"
        :violations="filteredViolations"
        v-if="trueFieldType === 'CUSTOM'"
        :root="root"
        :parent="parent"
    />

    <ReferenceField
        :field="field"
        :value="value"
        :root="root"
        :parent="parent"
        @input="handleInput"
        v-if="trueFieldType === 'REFERENCE' "
    />
    <NumberField
        :field="field"
        :value="value"
        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'NUMBER'"
    />

    <TextField
        :field="field"
        :root="root"
        :value="value"
        @input="handleInput"
        :violations="filteredViolations"
        v-if="trueFieldType === 'INPUT'"
    />

    <EnumPicker
        :field="field"
        :value="value"
        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'ENUM'"/>

    <BooleanPicker
        :field="field"
        :value="value"
        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'BOOLEAN'"
    />

    <TextArea
        :field="field"
        :value="value"
        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'TEXTAREA'"
    />

    <MarkdownEditor
        :field="field"
        :value="value"
        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'MARKDOWN'"
    />

    <HtmlEditor
        :field="field"
        :value="value"
        @input="handleInput"
        :root="root"
        v-if="trueFieldType === 'HTML'"
    />

    <ArrayField
        :field="field"
        :value="value || []"
        :root="root"
        :parent="parent"
        @input="handleInput"
        v-if="trueFieldType === 'ARRAY'"
        :violations="filteredViolations"
    />

    <FileField
        :field="field"
        :value="value || ''"

        :root="root"
        @input="handleInput"
        v-if="trueFieldType === 'FILE_REFERENCE'"
    />

    <div class="background-darken" uk-accordion="multiple: true" v-if="trueFieldType === 'OBJECT'">
      <div>
        <a class="uk-accordion-title uk-padding-small background-darken"
           :class="{'uk-text-danger': formattedViolations.length}" href="#"><span
            v-if="formattedViolations.length"
            uk-icon="warning" class="uk-margin-small-right"></span>{{ objectName }}</a>
        <div class="uk-accordion-content uk-padding-small">
          <ul v-if="partOfArray">
            <li class="uk-text-danger" v-for="violation in formattedViolations">{{ violation.message }}</li>
          </ul>
          <ObjectField :model="field" :violations="partOfArray ? violations :filteredViolations"
                       :value="value == null ? {} : value"
                       :root="root"
                       @input="handleInput"/>

        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>

.label {
  font-size: 16px;
  margin-bottom: 6px;

  margin-top: 30px;
}

.description {
  margin-top: 0;
  font-size: 14px;
  max-width: 512px;
}

.uk-accordion {
  list-style: none;

  .uk-accordion-title::before {
    float: left;
    margin-left: 0;
    margin-right: 10px;
  }

}

.uk-accordion {
  margin-bottom: 0 !important;
}

.background-darken {
  background-color: rgba($color: #000000, $alpha: 0.04);
}

.inner-object {
  padding: 15px;
}
</style>


<script>
import DatePicker from "./DatePicker";
import NumberField from "./NumberField";
import TextField from "./InputField";
import TextArea from "./TextArea";
import MarkdownEditor from "./MarkdownEditor.vue";
import HtmlEditor from "./HtmlEditor.vue";
import EnumPicker from "./EnumPicker.vue";
import BooleanPicker from "./BooleanPicker.vue";
import ArrayField from "./ArrayField.vue";
import FileField from "./FileField.vue";
import ReferenceField from "./ReferenceField";
import CustomField from "./CustomField";
import jexl from 'jexl'
import eventBus from "./eventBus"

export default {
  props: ["value", "fieldType", "field", "violations", "partOfArray", "parent", "root"],

  name: "GenericField",
  components: {
    CustomField,
    ReferenceField,
    NumberField,
    TextField,
    TextArea,
    MarkdownEditor,
    DatePicker,
    HtmlEditor,
    EnumPicker,
    BooleanPicker,
    ArrayField,
    FileField,
    ObjectField: () => import("./ObjectField.vue")
  },
  computed: {

    belongsInArray() {
      return this.field.arrayType && this.field.arrayType !== this.trueFieldType;
    },
    hasErrors() {
      return this.violations && this.violations.filter(violation => {
        return violation === '' || violation.propertyPath.startsWith(this.field.name);
      }).length > 0
    },
    formattedViolations() {
      return !this.violations ? [] : this.violations.filter(violation => {
        return (violation.propertyPath === '' && this.trueFieldType === 'OBJECT') || violation.propertyPath === this.field.name;
      })
    },
    trueFieldType() {
      return this.fieldType || this.field.type;
    },

    objectName: function () {
      if (this.field.type === 'ARRAY') {
        let featuredProperty = this.field.featuredProperty;
        return this.value[featuredProperty] || 'One of the ' + this.field.label;
      }
      return this.field.label
    },

    filteredViolations() {
      return this.violations
          .filter(violation => {
            const beginPropertyPath = violation.propertyPath.split('.')[0];

            const beginPropertyPathWithoutArray = beginPropertyPath.replaceAll(/\[.*]/, '');
            return beginPropertyPathWithoutArray === this.field.name;
          })
          .map(violation => {
            return {
              ...violation,
              propertyPath: violation.propertyPath.replace(`${this.field.name}`, '').replace(/^\./, "")
            }
          })
    }
  },
  data() {
    return {
      showField: true,
      notifyChange: true
    }
  },
  mounted() {
    this.setShowField();
    eventBus.$on('change', (e) => {
      this.setShowField();
    });
  },
  methods: {
    setShowField() {
      let context = {value: this.value, parent: this.parent, root: this.root}
      try {
        this.showField = !!jexl.evalSync(this.field.showIf, context)
      } catch (e) {
        console.error(e);
        this.showField = true
      }
    },
    handleInput(e) {
      this.$emit("input", e);
      eventBus.$emit('change')

    }
  }
};
</script>