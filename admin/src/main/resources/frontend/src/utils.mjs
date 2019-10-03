import UIkit from "uikit";
import marked from 'marked';

export default {
    url: "",
    displayResponse(response) {
        this.display(response);
    },
    displayError(error) {
        this.display(error.response)
    },
    display(response) {
        if (response.data.properties == null) {
            UIkit.notification(response.data.message, {
                status: "primary",
                pos: "bottom-center"
            });
        }
        switch (response.data.properties.type) {
            case "HTML":
                UIkit.modal.dialog(response.data.message);
                break;
            case "MARKDOWN":
                UIkit.modal.dialog(marked(response.data.message, {
                    sanitize: false
                }));
                break;
            case "MESSAGE":
                UIkit.notification(response.data.message, {
                    status: "success",
                    pos: "bottom-center"
                });
                break;
            case "ERROR":
                UIkit.notification(response.data.message, {
                    status: "danger",
                    pos: "bottom-center"
                });
                break;
            case "REDIRECT":
                window.location = response.data.message;
                break;
            default:
                if (response.status < 300) {
                    UIkit.notification(response.data.message, {
                        status: "success",
                        pos: "bottom-center"
                    });
                } else {
                    UIkit.notification(response.data.message, {
                        status: "danger",
                        pos: "bottom-center"
                    });
                }
                break;
        }


    },
    redir(error) {
        if (error.response.data.message != null) {
            window.location = error.response.data.message;
        } else {
            window.location = error.response.data;

        }
    }
}