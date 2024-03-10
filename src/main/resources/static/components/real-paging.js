import {actionQueue} from "../services/action-queue.js";

const style = `<style>
    .pagination {
        display: inline-block;
        padding-left: 0;
        margin-top: 1rem;
        margin-bottom: 1rem;
        border-radius: .25rem
    }
    
    .page-item {
        display: inline
    }
    
    .page-item:first-child .page-link {
        margin-left: 0;
        border-bottom-left-radius: .25rem;
        border-top-left-radius: .25rem
    }
    
    .page-item:last-child .page-link {
        border-bottom-right-radius: .25rem;
        border-top-right-radius: .25rem
    }
    
    .page-item.active .page-link, .page-item.active .page-link:focus, .page-item.active .page-link:hover {
        z-index: 2;
        color: #fff;
        cursor: default;
        background-color: #5cb85c;
        border-color: #5cb85c
    }
    
    .page-item.disabled .page-link, .page-item.disabled .page-link:focus, .page-item.disabled .page-link:hover {
        color: #818a91;
        pointer-events: none;
        cursor: not-allowed;
        background-color: #fff;
        border-color: #ddd
    }
    
    .page-link {
        position: relative;
        float: left;
        padding: .5rem .75rem;
        margin-left: -1px;
        color: #5cb85c;
        text-decoration: none;
        background-color: #fff;
        border: 1px solid #ddd
    }
    
    .page-link:focus, .page-link:hover {
        color: #3d8b3d;
        background-color: #eceeef;
        border-color: #ddd
    }            
</style>`;

const getTemplate = () => {
    return `
        <link rel="stylesheet" href="../css/common.css">

        ${style}
        
        <ul class="pagination">
<!--            <li class="page-item active">-->
<!--                <a class="page-link" href="">1</a>-->
<!--            </li>-->
<!--            <li class="page-item">-->
<!--                <a class="page-link" href="">2</a>-->
<!--            </li>-->
        </ul>
    `;
}

class RealPaging extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.listenTypes = ['changePage', 'pageInfo'];
        this.shadowRoot.innerHTML = getTemplate();
    }

    connectedCallback() {
        console.log('real-paging::connectedCallback(): 1:', 1);
        actionQueue.addListener(this.listenTypes, this);
    }

    disconnectedCallback() {
        actionQueue.removeListener(this.listenTypes, this);
    }

    // static get observedAttributes() {
    //     return ['page', 'page-count'];
    // }
    //
    // attributeChangedCallback(name, oldValue, newValue) {
    //     console.log('real-paging::attributeChangedCallback(): name, oldValue, newValue:', name, oldValue, newValue);
    //
    //     if (name === 'page-count') {
    //         this.updatePages(newValue);
    //     } else if (name === 'page') {
    //         this.updateActivePage(oldValue, newValue);
    //     }
    // }

    clickPage = (evt) => {
        evt.preventDefault();
        console.log('real-paging::noMethod(): evt.target.innerText:', evt.target.innerText);

        actionQueue.addAction({
            type: 'changePage',
            data: {
                value: evt.target.innerText
            },
        });
    }

    callback = ({type, result, data}) => {
        console.log('real-paging::callback(): type, result:', type, result);
        console.log('real-paging::callback(): data:', data);

        const changePageCallback = (page) => {
            console.log('>>>>> real-paging::changePageCallback(): page:', page);
        }

        const pageInfoCallback = (result, pageInfo) => {
            this.shadowRoot.querySelector('ul').innerHTML = new Array(+pageInfo.totalPages)
                .fill()
                .map((_, index) => ` 
                <li class="page-item ${index === +pageInfo.number ? 'active': ''}">
                    <a class="page-link" href="">${index + 1}</a>
                </li>`)
                .join('');

            this.shadowRoot
                .querySelectorAll('a')
                .forEach(link => link.addEventListener('click', this.clickPage));
        }

        const runCallback = {
            'changePage':   changePageCallback,
            'pageInfo':     pageInfoCallback,
        }
        runCallback[type] && runCallback[type](result, data);
    }
}
customElements.define('real-paging', RealPaging);
export {RealPaging}
