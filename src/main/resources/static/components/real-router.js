import {getCustomTagHTML} from "../services/routes.js";
import {actionQueue} from "../services/action-queue.js";

const getTemplate = () => {
    return `
        <home-page></home-page>
    `
}

class RealRouter extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate();
    }

    connectedCallback() {
        console.log('real-router::connectedCallback(): 1:', 1);
        actionQueue.addListener('route', this);
    }

    disconnectedCallback() {
        actionQueue.removeListener('route', this);
    }

    callback = ({type, result}) => {
        console.log('real-router::callback(): type, result:', type, result);

        const changeRouteCallback = (url) => {
            this.shadowRoot.innerHTML = getCustomTagHTML(url);
        }

        const runCallback = {
            'route':  changeRouteCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}

customElements.define('real-router', RealRouter);
export {RealRouter}
// customElements.whenDefined()
