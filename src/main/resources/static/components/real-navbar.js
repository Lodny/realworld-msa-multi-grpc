import {currentUser, store} from "../services/store.js";
import {iconCdn} from "../services/icon-cdn.js"
import {getBaseUrlByUrl} from "../services/routes.js";
import {actionQueue, addGoAction} from "../services/action-queue.js";

const style = `
    <style>
        .navbar .container {
            display: flex;
            justify-content: space-between;
            align-items: end;
        }
        
        .navbar .navbar-brand {
            position: relative;
            padding: 0.5rem 1rem;
            
            font-family: "Titillium Web", sans-serif;
            font-size: 1.5rem;
            float: left;
            /*padding-top: 0;*/
            /*padding-bottom: 0.25rem;*/
            margin-right: 2rem;
            color: #5CB85C;
        }
        
        .navbar ul {
            display: flex;
        }
        
        .navbar ul li {
            list-style: none;
            padding: 0 1rem;
            /*margin-right: 1rem;                            */
        }
        
        .navbar ul li a {
            color: rgba(0, 0, 0, 0.3);
        }
        
        .navbar ul li a.active {
            color: rgba(0, 0, 0, 0.8);
        }
        
        .navbar ul li a:hover {
            color: rgba(0, 0, 0, 0.8);
        }            
    </style>`;

const getTemplate = () => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        ${style}
            
        <nav class="navbar navbar-light">
            <div class="container">
                <a class="navbar-brand" href="/">conduit</a>
                <ul class="nav navbar-nav pull-xs-right">
                    <li class="nav-item">
                        <a class="nav-link active" href="/">Home</a>
                    </li>
                    <li class="nav-item off">
                        <a class="nav-link" href="/login">Sign in</a>
                    </li>
                    <li class="nav-item off">
                        <a class="nav-link" href="/register">Sign up</a>
                    </li>
                    <li class="nav-item on" style="display: none">
                        <a class="nav-link" href="/editor"><i class="ion-compose"></i> New Article</a>
                    </li>
                    <li class="nav-item on" style="display: none">
                        <a class="nav-link" href="/settings"><i class="ion-gear-a"></i> Settings</a>
                    </li>
                    <li class="nav-item on" style="display: none">
                        <a class="nav-link username" href="/profile/username">
                            <img src="" class="user-pic" />username
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
    `;
}

class RealNavbar extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate();
    }

    connectedCallback() {
        console.log('real-navbar::connectedCallback(): 1:', 1);
        actionQueue.addListener('route', this);

        this.links = Array.from(this.shadowRoot.querySelectorAll('a'));
        this.links.forEach(aTag => aTag.addEventListener('click', this.clickLink));
        this.updateActiveLink('/')
    }

    disconnectedCallback() {
        actionQueue.removeListener('route', this);
    }

    clickLink = (evt) => {
        evt.preventDefault();
        console.log('real-navbar::clickLink(): evt.target.href:', evt.target.href);

        addGoAction(evt.target.href);
    }

    updateLinks() {
        const user = currentUser();
        console.log('real-navbar::updateLinks(): user:', user);

        this.shadowRoot.querySelectorAll('.nav-item.off')
            .forEach(link => link.style.display = user ? 'none' : 'block');
        this.shadowRoot.querySelectorAll('.nav-item.on')
            .forEach(link => link.style.display = user ? 'block' : 'none');

        if (user) {
            const usernameLink = this.shadowRoot.querySelector('.nav-link.username');
            usernameLink.innerHTML = `<img src="" class="user-pic"/>${user.username}`;
            usernameLink.href = `href="/profile/${user.username}`
        }
    }

    updateActiveLink(url) {
        console.log('real-navbar::updateActiveLink(): url:', url);

        this.shadowRoot.querySelector('.nav .nav-item .nav-link.active')
            ?.classList.remove('active');

        this.links.toReversed()
            .find(link => getBaseUrlByUrl(link.href) === getBaseUrlByUrl(url))
            ?.classList.add('active');
    }

    callback = ({type, result}) => {
        console.log('real-navbar::callback(): type, result:', type, result);

        const routeCallback = (url) => {
            this.updateLinks();
            this.updateActiveLink(url);
        }

        const runCallback = {
            'route':   routeCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}
customElements.define('real-navbar', RealNavbar);

