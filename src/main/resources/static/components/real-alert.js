import {iconCdn} from "../services/icon-cdn.js";

const style = `<style>
    .modal-content {
        position: relative;
        background-color: #fefefe;
        border-radius: 10px;
        margin: 15% auto;
        padding: 30px;
        border: 1px solid #888;
        width: 600px;
        text-align: center;
        box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2), 0 6px 20px 0 rgba(0,0,0,0.19);
    }
    
    .close {
        position: absolute;
        right: 0;
        top: 0;                
        color: #aaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
        line-height: 1.2;
        padding: 0 8px;
    }
    
    .close:hover,
    .close:focus {
        color: black;
        text-decoration: none;
        cursor: pointer;
    }
    
    p {
        color: black;
        font-size: 20px;
    }
</style>`;

const getTemplate = () => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        ${style}
        
        <div class="modal-content">
            <span class="close">&times;</span>
            <p>message<p>
        </div>
    `
}

class RealAlert extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate();

        this.style.display = 'none';
        this.style.position = 'fixed';
        this.style.zIndex = 1;
        this.style.left = 0;
        this.style.top = 0;
        this.style.width = '100%';
        this.style.height = '100%';
        this.style.overflow = 'auto';
        this.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
    }

    connectedCallback() {
        this.messageTag = this.shadowRoot.querySelector('p');
        this.shadowRoot.querySelector('.close').addEventListener('click', () => this.close());
    }

    close() {
        this.style.display = 'none';
    }

    alert = async (message) => {
        this.messageTag.innerHTML = message;
        this.style.display = 'block';

        return new Promise((resolve) => {
            document.addEventListener('click', () => {
                this.close();
                resolve();
            }, { once: true });
            // document.addEventListener('keydown', () => {
            //     this.close();
            //     resolve();
            // }, { once: true });
        });
    }
}

customElements.define('real-alert', RealAlert);
export {RealAlert}
