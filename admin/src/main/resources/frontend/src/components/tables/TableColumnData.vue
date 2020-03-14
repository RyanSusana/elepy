<template>
    <td v-if="field.type === 'FILE_REFERENCE'">
        <ImageLightBox :height="100" :src="valueAsUpload" :width="100" v-if="isImageUrl"/>

        <a :href="valueAsUpload" target="_blank" v-else-if="!value.isEmpty()">Download Link</a>
        <span class="uk-text-muted" v-else>Nothing uploaded</span>
    </td>
    <td v-else-if="field.type === 'HTML'">
        <div v-html="value"></div>
    </td>
    <td v-else>{{toRegularTableData}}</td>
</template>

<script>
    import ImageLightBox from "../modals/ImageLightBox.vue";

    import Utils from "../../utils";
    import moment from "moment/moment";

    export default {
        props: ["value", "field"],

        components: {ImageLightBox},
        computed: {
            isImageUrl: function () {
                return /(http(s?):)*\.(?:jpg|gif|png|svg|jpeg)/g.test(this.value);
            },
            toRegularTableData: function () {
                return this.field.type === "DATE"
                    ? this.toDate(this.value)
                    : this.field.type === "BOOLEAN"
                        ? this.toBoolean(this.field, this.value)
                        : this.field.type === "ENUM"
                            ? this.toEnum(this.field, this.value)
                            : this.value;
            },
            valueAsUpload() {

                if (this.value.includes('/uploads/')) {
                    return Utils.url + this.value;
                }
                return Utils.url + "/uploads/" + this.value;
            }
        },
        methods: {
            toDate: function (milli) {

                if (milli == null) {
                    return '';
                }
                let date = moment(milli);

                if (moment().diff(date, "minutes") > 10) {
                    return date.calendar();
                } else {
                    return moment().to(date);
                }
            },
            toBoolean: function (field, data) {
                if (data === true) {
                    return field.trueValue;
                } else {
                    return field.falseValue;
                }
            },
            toEnum: function (field, data) {
                var toReturn = data;
                field.availableValues.forEach(enumDescription => {
                    if (enumDescription["enumValue"] === data) {
                        toReturn = enumDescription["enumName"];
                    }
                });
                return toReturn;
            }
        }
    };
</script>