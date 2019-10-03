<template>
    <div class>
        <vue-ctk-date-time-picker
                :auto-close="!field.includeTime"
                :maxDate="field.maximumDate.toString()"
                :minDate="field.minimumDate.toString()"
                :no-clear-button="field.required || field.type==='ARRAY'"
                :only-date="!field.includeTime"
                :value="this.content"
                @input="handleInput"
                calendar-class="uk-dark"
                input-class="uk-input"
                output-format="x"
                placeholder="Click to select a date"
                v-bind:disabled="field.generated == true || (field.editable == false )"
        ></vue-ctk-date-time-picker>
    </div>
</template>

<style lang="scss">
    .field-input,
    .date-time-picker {
        border-radius: 0 !important;
    }
</style>

<script>
    import VueCtkDateTimePicker from "vue-ctk-date-time-picker";
    import "vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css";

    export default {
        props: ["field", "value"],

        watch: {
            value: function (val) {
                this.content = val;
                this.$forceUpdate();
            }
        },
        components: {VueCtkDateTimePicker},
        data() {
            return {
                content: this.value == null ? "" + Date.now() : "" + this.value
            };
        },
        computed: {
            formattedDate() {
                if (this.value == null) {
                    return "";
                }
                return "" + this.value;
            }
        },
        methods: {
            handleInput(e) {
                this.content = e;
                this.$emit("input", this.content);
            }
        }
    };
    Date.prototype.yyyymmdd = function () {
        const mm = this.getMonth() + 1; // getMonth() is zero-based
        const dd = this.getDate();

        return [
            this.getFullYear(),
            (mm > 9 ? "-" : "-0") + mm,
            (dd > 9 ? "-" : "-0") + dd
        ].join("");
    };
</script>
