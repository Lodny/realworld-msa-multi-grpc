import {RealArticlePreview} from "./real-article-preview.js";
import {RealPaging} from "./real-paging.js";
import {iconCdn} from "../services/icon-cdn.js";
import {actionQueue} from "../services/action-queue.js";

const style = ` <style>
    .feed-toggle {
        margin-bottom: -1px;
    }

    .nav {
        padding-left: 0;
        margin-bottom: 0;
        list-style: none
    }
    
    .nav-link {
        display: inline-block
    }
    
    .nav-link:focus, .nav-link:hover {
        text-decoration: none
    }
    
    .nav-link.disabled {
        color: #818a91
    }
    
    .nav-pills::after {
        content: "";
        display: table;
        clear: both
    }
    
    .nav-pills .nav-item {
        float: left
    }
    
    .nav-pills .nav-item + .nav-item {
        margin-left: .2rem
    }
    
    .nav-pills .nav-link {
        display: block;
        padding: .5em 1em;
        border-radius: .25rem
    }

    .nav-pills.outline-active .nav-link {
        border-radius: 0;
        border: none;
        border-bottom: 2px solid transparent;
        background: 0 0;
        color: #aaa
    }
    
    .nav-pills.outline-active .nav-link:hover {
        color: #555
    }
    
    .nav-pills.outline-active .nav-link.active {
        background: #fff !important;
        border-bottom: 2px solid #5cb85c !important;
        color: #5cb85c !important
    }
</style>`;

const getTemplate = () => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        ${style}
        
        <div class="feed-toggle">
            <ul class="nav nav-pills outline-active">
<!--                <li class="nav-item">-->
<!--                    <a class="nav-link active" href="Global Feed">Global Feed</a>-->
<!--                </li>-->
            </ul>
        </div>
        <slot></slot>
    `;
}

class RealTab extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.listenTypes = ['tabTitles', 'activeTab'];

        this.shadowRoot.innerHTML = getTemplate();
    }

    async connectedCallback() {
        console.log('real-tab::connectedCallback(): 1:', 1);
        actionQueue.addListener(this.listenTypes, this);

        this.ulTag = this.shadowRoot.querySelector('ul');

        const tabTitles = this.getAttribute('tab-titles').split(',');
        this.activeTab = this.getAttribute('active-tab');
        this.initTabTitles(tabTitles, this.activeTab);
    }

    disconnectedCallback() {
        actionQueue.removeListener(this.listenTypes, this);
    }

    initTabTitles(tabTitles, activeTab) {
        this.ulTag.innerHTML = tabTitles.map(title =>`
                <li class="nav-item">
                    <a class="nav-link ${title === activeTab ? 'active' : ''}" href="${title}">${title}</a>
                </li>`).join('');

        this.ulTag.querySelectorAll('a')
            .forEach(aTag => aTag.addEventListener('click', this.clickLink));
    }

    clickLink = async (evt) => {
        evt.preventDefault();
        console.log('real-tab::clickLink(): evt.target:', evt.target);
        console.log('real-tab::clickLink(): this.activeTab:', this.activeTab);


        if (this.activeTab !== evt.target.innerHTML) {
            if (this.activeTab.startsWith('#')) {
                this.ulTag.querySelector('li:last-child').remove();
            } else {
                this.shadowRoot.querySelector('.nav-link.active')
                    ?.classList.remove('active');
            }

            evt.target.classList.add('active');
        }

        this.activeTab = evt.target.innerHTML;
        actionQueue.addAction({
            type: 'activeTab',
            data: {
                value: this.activeTab
            }
        });
    }

    callback = ({type, result, data}) => {
        console.log('real-tab::callback(): type, result:', type, result);
        console.log('real-tab::callback(): data:', data);

        const tabTitlesCallback = (result, {tabTitles, activeTab}) => {
            this.initTabTitles(tabTitles, activeTab);
            this.activeTab = activeTab;

            actionQueue.addAction({
                type: 'activeTab',
                data: {
                    value: activeTab
                }
            });
        }

        const activeTabCallback = (result, data) => {
            console.log('>>>>> real-tab::activeTabCallback(): 1:', 1);
        }

        const runCallback = {
            'tabTitles': tabTitlesCallback,
            'activeTab': activeTabCallback,
        }
        runCallback[type] && runCallback[type](result, data);
    }
}
customElements.define('real-tab', RealTab);
export {RealTab}

