import {iconCdn} from "../services/icon-cdn.js";
import {actionQueue} from "../services/action-queue.js";
import {currentUser} from "../services/store.js";

const style = `<style>
</style>`;

const getTemplate = (user) => {
    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/form.css">
        <link rel="stylesheet" href="../css/register-login.css">
        ${style}
        
        <div class="settings-page">
            <div class="container page">
                <div class="row">
                    <div class="col-md-6 offset-md-3 col-xs-12">
                    <h1 class="text-xs-center">Your Settings</h1>
    
                    <ul class="error-messages">
                        <li>That name is required</li>
                    </ul>
    
                    <form>
                        <fieldset>
                            <fieldset class="form-group">
                                <input class="form-control image" type="text" placeholder="URL of profile picture" value="${user.image}" />
                            </fieldset>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg username" type="text" placeholder="Your Name" value="${user.username}" />
                            </fieldset>
                            <fieldset class="form-group">
                                <textarea
                                    class="form-control form-control-lg bio"
                                    rows="8"
                                    placeholder="Short bio about you"
                                >${user.bio}</textarea>
                            </fieldset>
                            <fieldset class="form-group">
                                <input class="form-control form-control-lg email" type="text" placeholder="Email" value="${user.email}" />
                            </fieldset>
                            <fieldset class="form-group">
                            <input
                                class="form-control form-control-lg"
                                type="password"
                                placeholder="New Password"
                            />
                            </fieldset>
                            <button class="btn btn-lg btn-primary pull-xs-right update">Update Settings</button>
                        </fieldset>
                    </form>
                    <hr />
                    <button class="btn btn-outline-danger logout">Or click here to logout.</button>
                </div>
            </div>
        </div>
    </div>
    `
}

class SettingsPage extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.shadowRoot.innerHTML = getTemplate(currentUser());
    }

    connectedCallback() {
        console.log('settings-page::connectedCallback(): 1:', 1);
        actionQueue.addListener('updateUser', this);

        this.imageInputTag = this.shadowRoot.querySelector('input.image');
        this.usernameInputTag = this.shadowRoot.querySelector('input.username');
        this.bioTextareaTag = this.shadowRoot.querySelector('textarea');
        this.emailInputTag = this.shadowRoot.querySelector('input.email');
        this.passwordInputTag = this.shadowRoot.querySelector('input[type="password"]');

        this.shadowRoot.querySelector('button.update')
            ?.addEventListener('click', this.updateUser);
        this.shadowRoot.querySelector('button.logout')
            ?.addEventListener('click', this.logout);
    }

    disconnectedCallback() {
        actionQueue.removeListener('updateUser', this);
    }

    logout = (evt) => {
        evt.preventDefault();
        actionQueue.addAction({
            type: 'logout',
            nextRoute: '/'
        });
    }

    updateUser = (evt) => {
        evt.preventDefault();
        actionQueue.addAction({
            type: 'updateUser',
            data: {
                image: this.imageInputTag.value,
                username: this.usernameInputTag.value,
                bio: this.bioTextareaTag.value,
                email: this.emailInputTag.value,
                password: this.passwordInputTag.value,
            },
            nextRoute: '/',
            set: 'user',
        });
    }

    callback = ({type, result}) => {
        console.log('settings-page::callback(): type, result:', type, result);

        const updateUserCallback = (result) => {}
        const errorCallback = (result) => {}

        const runCallback = {
            'updateUser':   updateUserCallback,
            'error':        errorCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}
customElements.define('settings-page', SettingsPage);
export {SettingsPage}
