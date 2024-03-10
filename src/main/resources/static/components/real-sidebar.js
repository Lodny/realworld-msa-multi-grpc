import {iconCdn} from "../services/icon-cdn.js";
import {actionQueue} from "../services/action-queue.js";

const style = `<style>
    .sidebar {
        padding: 5px 10px 10px;
        background: #f3f3f3;
        border-radius: 4px
    }
    
    .sidebar p {
        margin-bottom: .2rem
    }            
</style>`;

const getTemplate = () => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/tag.css">
        ${style}
        
        <div class="sidebar">
            <p>Popular Tags</p>

            <div class="tag-list">
<!--                <a href="" class="tag-pill tag-default">programming</a>-->
            </div>
        </div>    
    `;
}

class RealSidebar extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate();
    }

    async connectedCallback() {
        actionQueue.addListener(['getTags'], this);

        actionQueue.addAction({
            type: 'getTags',
        });
    }

    disconnectedCallback() {
        actionQueue.removeListener(['getTags'], this);
    }

    clickTag = (evt) => {
        evt.preventDefault();
        actionQueue.addAction({
            type: 'selectTag',
            data: {
                value: evt.target.innerHTML,
            }
        });
    }

    callback = ({type, result}) => {
        console.log('real-sidebar::callback(): type, result:', type, result);

        const getTagsCallback = (tags) => {
            this.shadowRoot.querySelector('.tag-list').innerHTML = tags
                .map(tag => `<a href="" class="tag-pill tag-default">${tag}</a>`)
                .join('');

            this.shadowRoot.querySelectorAll('a')
                .forEach(link => link.addEventListener('click', this.clickTag))
        }

        const runCallback = {
            'getTags':   getTagsCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}
customElements.define('real-sidebar', RealSidebar);
export {RealSidebar}

