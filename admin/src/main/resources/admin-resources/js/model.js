Vue.component('Trumbowyg', VueTrumbowyg.default);
Vue.component('vuejs-datepicker', window['vue-ctk-date-time-picker']);

const app = new Vue({
    el: '#app',
    delimiters: ['((', '))'],
    components: {

        'vuejs-colorpicker': VueColor.Sketch
    },
    data: {
        bufData: {},
        newData: {},
        selectedData: null,
        selectedModel: null,
        models: [],
        modelData: null,
        searchTimeout: null,
        searchQuery: "",
        lastSelectedPageNum: 1,
        curPage: {
            currentPageNumber: 1
        }

    },
    computed: {

        lastPageNumber: function () {
            return this.curPage.lastPageNumber;
        }
    },

    methods: {
        log: function (s) {
            console.log(s);
        },
        next: function () {
            if (this.curPage.currentPageNumber < this.curPage.lastPageNumber) {
                this.page(this.curPage.currentPageNumber + 1);
            } else {
                this.page(1);
            }
        }
        ,
        previous: function () {
            if (this.curPage.currentPageNumber > 1) {
                this.page(this.curPage.currentPageNumber - 1);
            } else {
                this.page(this.curPage.lastPageNumber);
            }
        }
        ,
        search: function () {
            var ref = this;
            console.log("search");
            clearTimeout(this.searchTimeout);

            this.searchTimeout = setTimeout(function () {
                ref.getModelData();
            }, 500);
        },
        compileMarkdown: function (item) {
            return marked(item, {sanitize: false})
        },

        fromDate: function (field, index) {
            this.selectedData[field] = document.getElementById('date-form-field-' + index).value;
            this.newData[field] = document.getElementById('date-form2-field-' + index).value;

            console.log(document.getElementById('date-form2-field-' + index).value);

        },
        toDate: function (milli) {
            var date = new Date(milli);
            return date.yyyymmdd();
        },
        toBoolean: function (field, data) {

            if (data === true) {
                return field.trueValue;
            } else {
                return field.falseValue
            }

        },
        toEnum: function (field, data) {
            var toReturn = data;
            field.availableValues.forEach(enumDescription => {
                if (enumDescription["enumValue"] === data) {
                    toReturn = enumDescription["enumName"];
                }
            })
            return toReturn;

        },
        editModal: function () {
            UIkit.modal(document.getElementById("edit-modal"), {stack: true, 'bg-close': false}).show();
        },
        saveData: function () {
            var ref = this;
            axios({
                method: 'put',
                url: ref.selectedModel.slug + "/" + ref.selectedData[ref.selectedModel.idField],
                data: ref.selectedData
            }).then(function (response) {
                //console.log(response.data)
                //ref.modelData = response.data
                UIkit.modal(document.getElementById("edit-modal")).hide();
                ref.page(ref.curPage.currentPageNumber);
                UIkit.notification("Successfully updated the item!", {status: 'success'})

            })
                .catch(function (error) {
                    console.log(error.response.status);
                    UIkit.notification(error.response.data.message, {status: 'danger'})

                });

        },
        getModelData: function () {
            this.page(this.curPage.currentPageNumber);

        },
        page: function (pageNumber) {
            var ref = this;
            axios.get(ref.selectedModel.slug + "?pageSize=10&q=" + ref.searchQuery + "&pageNumber=" + pageNumber)
                .then(function (response) {


                    ref.modelData = response.data.values;
                    ref.curPage = response.data;
                })
                .catch(function (error) {
                    UIkit.notification(error.response.status, {status: 'danger'})
                });
        }
        ,
        getModel: function () {
            var ref = this;
            axios.get('/admin/config' + document.getElementById("slug").innerText)
                .then(function (response) {
                    ref.selectedModel = response.data;
                    ref.initData(ref.selectedModel, '');
                    ref.getModelData();
                })
                .catch(function (error) {
                    UIkit.notification(error.response.status, {status: 'danger'})
                });
        },
        deleteData: function () {
            var ref = this;
            UIkit.modal.confirm('Are you sure that you want to delete this item?', {
                labels: {
                    ok: "Yes",
                    cancel: "Cancel"
                }, stack: true
            }).then(function () {
                console.log(ref.selectedModel.slug + '/' + ref.selectedData[ref.selectedModel.idField]);
                axios({
                    method: 'delete',
                    url: ref.selectedModel.slug + '/' + ref.selectedData[ref.selectedModel.idField]
                }).then(function (response) {

                    UIkit.modal(document.getElementById("edit-modal"), {stack: true, 'bg-close': false}).hide();
                    UIkit.notification(response.data.message, {status: 'success'});
                    ref.getModelData();
                })
                    .catch(function (error) {
                        UIkit.notification(error.response.data.message, {status: 'danger'})
                    });
            }, function () {

            });
        },
        createData: function () {
            var ref = this;
            axios({
                method: 'post',
                url: ref.selectedModel.slug,
                data: ref.newData
            }).then(function (response) {
                //console.log(response.data)
                //ref.modelData = response.data
                UIkit.modal(document.getElementById("add-modal")).hide();
                ref.initData(ref.selectedModel, '');
                UIkit.notification("Successfully created the item!", {status: 'success'});

                ref.getModelData();

            })
                .catch(function (error) {
                    console.log(error.response.status);
                    UIkit.notification(error.response.data.message, {status: 'danger'})

                });

        },

        createModal: function () {
            UIkit.modal(document.getElementById("add-modal"), {stack: true}).show();
        },
        initData: function (model, pre) {
            for (var i = 0; i < model.fields.length; i++) {
                var field = model.fields[i];
                if (field.type === 'OBJECT') {
                    Vue.set(this.bufData, pre + field.name, {});
                    this.initData(field, field.name + '.');
                } else if (field.type === 'ENUM') {
                    Vue.set(this.bufData, pre + field.name, field.availableValues[0]["enumValue"]);
                } else if (field.type === 'DATE') {
                    Vue.set(this.bufData, pre + field.name, new Date().getTime());
                } else if (field.type === 'NUMBER') {
                    Vue.set(this.bufData, pre + field.name, 0);
                } else if (field.type === 'BOOLEAN') {
                    Vue.set(this.bufData, pre + field.name, false);
                } else {
                    Vue.set(this.bufData, pre + field.name, '');
                }
            }
            //this.newData = transform(this.newData);

            const source = this.bufData;
            let target = {};

            Object.keys(source).forEach(key => {
                _.set(target, key, source[key]);
            });
            console.log(target);
            this.newData = target;
        }
    },
    created: function () {
        this.getModel();

    }
});
Date.prototype.yyyymmdd = function () {
    const mm = this.getMonth() + 1; // getMonth() is zero-based
    const dd = this.getDate();

    return [this.getFullYear(),
        (mm > 9 ? '-' : '-0') + mm,
        (dd > 9 ? '-' : '-0') + dd
    ].join('');
};


var template = `
<div>
        <div v-for="(field, index) in selectedmodel.fields" v-if="field.type != 'OBJECT' "
             class="uk-margin">
            <label class="uk-form-label" v-if="!(selectedmodel.idField === field.name && creating && field.generated)">((field.prettyName))</label>
            <div class="uk-form-controls">
                <!-- TEXTFIELD -->
                <input class="uk-input"
                       v-if="!(selectedmodel.idField === field.name && creating && field.generated) && (field.type == 'STRING' || (field.type == 'TEXT' && field.textType == 'TEXTFIELD') ||  (field.type == 'TEXT' && field.textType == 'IMAGE_LINK'))"
                       v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                       v-model="selecteddata[field.name]" type="text"
                       placeholder="">
                <!-- PASSWORD -->
                <input class="uk-input"
                       v-if=" (field.type == 'TEXT' && field.textType == 'PASSWORD')"
                       v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                       v-model="selecteddata[field.name]"
                       type="password"
                       placeholder="">
                <!-- IMAGE -->
                <div v-if="(field.type == 'TEXT' && field.textType == 'IMAGE_LINK')"
                     class="uk-padding">

                    <img :src="selecteddata[field.name]" alt="">
                </div>
                <!-- DATE -->
                <vuejs-datepicker v-if="field.type == 'DATE'" v-model="selecteddata[field.name]"
                                  v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                  placeholder="Click to select a date" input-class="uk-input"
                                  calendar-class="uk-dark"
                                  output-format = "x" :minDate="field.minimumDate"  :maxDate="field.maximumDate" :auto-close = "!field.includeTime" :only-date="!field.includeTime">
                                  
</vuejs-datepicker>
                <!-- COLOR -->
                <vuejs-colorpicker v-if="field.type == 'TEXT' && field.textType == 'COLOR'"
                                   v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                   v-bind:value="selecteddata[field.name]"
                                   v-on:input="selecteddata[field.name] = $event.hex"
                ></vuejs-colorpicker>
                <!-- NUMBER -->
                <input class="uk-input" v-if="field.type == 'NUMBER' && !(selectedmodel.idField === field.name && creating && field.generated)"
                       v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                       v-model="selecteddata[field.name]"
                       type="number">
                <!-- TEXTAREA -->

                <textarea class="uk-textarea"
                          v-if="(field.type == 'TEXT' && field.textType == 'TEXTAREA')"
                          v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                          v-model="selecteddata[field.name]" type="text"
                          placeholder="" rows="5"></textarea>

                <!-- MARKDOWN -->
                <div class="uk-padding uk-background-muted"
                     v-html="compileMarkdown(selecteddata[field.name])"
                     v-if="(field.type == 'TEXT' && field.textType == 'MARKDOWN')">
                </div>
                <textarea class="uk-textarea"
                          v-if="(field.type == 'TEXT' && field.textType == 'MARKDOWN') && (field.editable === true)"
                          v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                          v-model="selecteddata[field.name]" type="text"
                          placeholder="" rows="13"></textarea>


                <!-- HTML -->
                <trumbowyg v-model="selecteddata[field.name]"
                           v-if="(field.type == 'TEXT' && field.textType == 'HTML') && (field.editable === true)"
                           svg-path="https://unpkg.com/trumbowyg@2.9.4/dist/ui/icons.svg"
                           class="editor"
                ></trumbowyg>

                <!-- ENUM -->
                <div v-if="field.type == 'ENUM' " class="uk-form-controls">
                    <select class="uk-select" v-model="selecteddata[field.name]"
                            v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                    >
                        <option v-for="value in field.availableValues"
                                v-bind:value="value.enumValue">((value.enumName))
                        </option>

                    </select>
                </div>

                <!-- BOOLEAN -->
                <div v-if="field.type == 'BOOLEAN' " class="uk-form-controls">
                    <select class="uk-select" v-model="selecteddata[field.name]"
                            v-bind:disabled="field.generated == true || (field.editable == false && !creating)">
                        <option v-bind:value="true">((field.trueValue))</option>
                        <option v-bind:value="false">((field.falseValue))</option>

                    </select>
                </div>


            </div>
        </div>
        <ul class="uk-margin-top" uk-accordion>
            <li v-for="(fieldOuter, index) in selectedmodel.fields"
                v-if="fieldOuter.type == 'OBJECT' ">
                <a class="uk-accordion-title" href="#">((fieldOuter.prettyName))</a>
                <div class="uk-accordion-content">
                    <div v-for="(field, index) in fieldOuter.fields"
                         class="uk-form-controls">
                        <label class="uk-form-label">((field.prettyName))</label>
                        <!-- TEXTFIELD -->
                        <input class="uk-input"
                               v-if="field.type == 'STRING' || (field.type == 'TEXT' && field.textType == 'TEXTFIELD') || (field.type == 'TEXT' && field.textType == 'IMAGE_LINK')"
                               v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                               v-model="selecteddata[fieldOuter.name][field.name]"
                               type="text"
                               placeholder="">
                        <!-- PASSWORD -->
                        <input class="uk-input"
                               v-if=" (field.type == 'TEXT' && field.textType == 'PASSWORD')"
                               v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                               v-model="selecteddata[fieldOuter.name][field.name]"
                               type="password"
                               placeholder="">

                        <!-- IMAGE -->

                        <div v-if="(field.type == 'TEXT' && field.textType == 'IMAGE_LINK')"
                             class="uk-padding">

                            <img :src="selecteddata[fieldOuter.name][field.name]" alt="">
                        </div>
                        <!-- DATE -->
                        <vuejs-datepicker v-if="field.type == 'DATE'"
                                          v-model="selecteddata[fieldOuter.name][field.name]"
                                          v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                          placeholder="Click to select a date"
                                          input-class="uk-input"
                                          format="x"
                                          calendar-class="uk-dark"
                                          output-format = "x" :minDate="field.minimumDate" :maxDate="field.maximumDate" :auto-close = "!field.includeTime" :only-date="!field.includeTime"></vuejs-datepicker>

                        <!-- COLOR -->
                        <vuejs-colorpicker
                                v-if="field.type == 'TEXT' && field.textType == 'COLOR'"
                                v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                v-bind:value="selecteddata[fieldOuter.name][field.name]"
                                v-on:input="selecteddata[fieldOuter.name][field.name] = $event.hex"
                        ></vuejs-colorpicker>

                        <!-- NUMBER -->
                        <input class="uk-input" v-if="field.type == 'NUMBER'"
                               v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                               v-model="selecteddata[fieldOuter.name][field.name]"
                               type="number">
                        <!-- TEXTAREA -->

                        <textarea class="uk-textarea"
                                  v-if="(field.type == 'TEXT' && field.textType == 'TEXTAREA')"
                                  v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                  v-model="selecteddata[fieldOuter.name][field.name]"
                                  type="text"
                                  placeholder=""
                                  rows="5"></textarea>
                        <!-- MARKDOWN -->
                        <div class="uk-padding uk-background-muted"
                             v-html="compileMarkdown(selecteddata[fieldOuter.name][field.name])"
                             v-if="(field.type == 'TEXT' && field.textType == 'MARKDOWN')">
                        </div>


                        <textarea class="uk-textarea"
                                  v-if="(field.type == 'TEXT' && field.textType == 'MARKDOWN') && (field.editable === true)"
                                  v-bind:disabled="field.generated == true || (field.editable == false && !creating)"
                                  v-model="selecteddata[fieldOuter.name][field.name]"
                                  type="text"
                                  placeholder="" rows="13"></textarea>


                        <!-- HTML -->
                        <trumbowyg v-model="selecteddata[fieldOuter.name][field.name]"
                                   v-if="(field.type == 'TEXT' && field.textType == 'HTML') && (field.editable === true)"
                                   svg-path="https://unpkg.com/trumbowyg@2.9.4/dist/ui/icons.svg"
                                   class="editor"
                        ></trumbowyg>

                        <!-- ENUM -->
                        <div v-if="field.type == 'ENUM' " class="uk-form-controls">
                            <select class="uk-select"
                                    v-model="selecteddata[fieldOuter.name][field.name]"
                                    v-bind:disabled="field.generated == true || (field.editable == false && !creating)">
                                <option v-for="value in field.availableValues"
                                        v-bind:value="value.enumValue">((value.enumName))
                                </option>


                            </select>
                        </div>

                        <!-- BOOLEAN -->
                        <div v-if="field.type == 'BOOLEAN' " class="uk-form-controls">
                            <select class="uk-select"
                                    v-model="selecteddata[fieldOuter.name][field.name]"
                                    v-bind:disabled="field.generated == true || (field.editable == false && !creating)">
                                <option v-bind:value="true">((field.trueValue))</option>
                                <option v-bind:value="false">((field.falseValue))</option>

                            </select>
                        </div>

                    </div>
                </div>
            </li>

        </ul>
</div>
`
Vue.component('elepy-form', {
    props: ['selecteddata', 'selectedmodel', 'creating'],
    template: template,
    delimiters: ['((', '))'],
    methods: {
        compileMarkdown: function (item) {
            return marked(item, {sanitize: false})
        },
        fromDate: function (field, index) {
            this.selectedData[field] = document.getElementById('date-form-field-' + index).value;
            this.newData[field] = document.getElementById('date-form2-field-' + index).value;

            console.log(document.getElementById('date-form2-field-' + index).value);

        },
        toDate: function (milli) {
            var date = new Date(milli);
            return date.yyyymmdd();
        },
    }
})