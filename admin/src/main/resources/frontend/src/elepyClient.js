import axios from "axios";

export default class SchemaClient {
    constructor(schema) {
        this.schema = schema;
    }

    search(searchTerm, pageSize = 10, pageNumber = 1) {
        return createRequest(this.schema.path, {
            ...(searchTerm !== '') && {q: searchTerm},
            pageSize: pageSize,
            pageNumber: pageNumber
        }).fetch();
    }

    getByIds(ids) {
        return createRequest(this.schema.path, {
            ids: ids.join(',')
        }).fetch()
    }

    getById(id) {
        return axios.get(this.schema.path + "/" + id).then(response => response.data);
    }

}

function createRequest(endpoint, params) {
    return {
        endpoint: endpoint,
        query: params,
        values: [],
        lastPageNumber: 1,
        currentPageNumber: 1,

        fetch() {
            return this.performRequest().then(data => {
                this.lastPageNumber = data.lastPageNumber;
                this.currentPageNumber = data.currentPageNumber;
                return this.values = data.values;
            }).then(() => this);
        },
        performRequest() {
            return axios.get(this.endpoint, {params: this.query}).then(response => response.data);
        },

        nextPage(holdResults = false) {

            if (holdResults) {
                if (this.query.pageNumber < this.lastPageNumber) {
                    this.query.pageNumber++;
                } else {
                    this.query.pageNumber = 1;
                }
                return this.performRequest().then(data => {
                    this.lastPageNumber = data.lastPageNumber;
                    this.currentPageNumber = data.currentPageNumber;
                    return this.values.push(...data.values);
                }).then(() => this);
            } else {
                if (this.query.pageNumber < this.lastPageNumber)
                    this.query.pageNumber++;
                return this.fetch();
            }

        },
        previousPage() {
            if (this.query.pageNumber > 1)
                this.query.pageNumber--;
            return this.fetch();
        }
    };
}