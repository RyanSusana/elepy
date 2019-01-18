Vue.component('Trumbowyg', VueTrumbowyg.default);
const app = new Vue({
    el: '#app',
    delimiters: ['((', '))'],
    components: {
        'vuejs-datepicker': vuejsDatepicker,
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
                    UIkit.notification(response.data, {status: 'success'});
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
                    Vue.set(this.bufData, pre + field.name, field.availableValues[0]);
                } else if (field.type === 'DATE') {
                    Vue.set(this.bufData, pre + field.name, '2018-01-01');

                } else if (field.type === 'NUMBER') {
                    Vue.set(this.bufData, pre + field.name, 0);
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

