import {iconCdn} from "../services/icon-cdn.js";
import {currentUser, store} from "../services/store.js";
import {actionQueue, addGoAction} from "../services/action-queue.js";

const style = `<style>
    .card {
        border: 1px solid #e5e5e5;
        box-shadow: none !important
    }
    
    .card .card-footer {
        border-top: 1px solid #e5e5e5;
        box-shadow: none !important;
        font-size: .8rem;
        font-weight: 300
    }
    
    .card .comment-author {
        display: inline-block;
        vertical-align: middle
    }
    
    .card .date-posted {
        display: inline-block;
        vertical-align: middle;
        margin-left: 5px;
        color: #bbb
    }
    
    .card .mod-options {
        float: right;
        color: #333;
        font-size: 1rem
    }
    
    .card .mod-options i {
        margin-left: 5px;
        opacity: .6;
        cursor: pointer
    }
    
    .card .mod-options i:hover {
        opacity: 1
    }     
    
    a {
        color: #5CB85C;
    }
</style>`;

const getTemplate = (comment, isOwnComment) => {
    const author = comment.author;

    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/card.css">
        ${style}
        
        <div class="card">
            <div class="card-block">
                <p class="card-text">
                    ${comment.body}
                </p>
            </div>
            <div class="card-footer">
                <a href="/profile/${author.username}" class="comment-author">
                    <img src="${author.image}" class="comment-author-img" />
                </a>
                &nbsp;
                <a href="/profile/${author.username}" class="comment-author">${author.username}</a>
                <span class="date-posted">${comment.createdAt}</span>
                ${isOwnComment ? `
                <span class="mod-options">
                    <i class="ion-trash-a"></i>
                </span>`
                : ''}
            </div>
        </div>
    `;
}

class RealComment extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});
        this.listenTypes = ['deleteComment'];

        this.commentId = +this.getAttribute('id');
        console.log('real-comment::constructor(): this.commentId:', this.commentId);

        this.slug = this.getAttribute('slug');
        console.log('real-comment::constructor(): this.slug:', this.slug);

        const comment = store.getComment(this.commentId);
        console.log('real-comment::constructor(): comment:', comment);

        const user = currentUser();
        const isOwnComment = user?.username === comment.author.username;
        this.shadowRoot.innerHTML = getTemplate(comment, isOwnComment);
    }

    connectedCallback() {
        console.log('real-comment::connectedCallback(): 1:', 1);
        actionQueue.addListener(this.listenTypes, this);

        this.shadowRoot
            .querySelector('.ion-trash-a')
            ?.addEventListener('click', this.deleteComment);

        this.shadowRoot
            .querySelectorAll('a')
            .forEach(aTag => aTag.addEventListener('click', (evt) => {
                evt.preventDefault();
                addGoAction(evt.target.closest('a')?.href);
            }))
    }

    disconnectedCallback() {
        actionQueue.removeListener(this.listenTypes, this);
    }

    deleteComment = (evt) => {
        evt.preventDefault();
        console.log('real-comment::deleteComment(): evt', evt);

        actionQueue.addAction({
            type: 'deleteComment',
            data: {
                slug: this.slug,
                value: this.commentId,
            },
        });
    }

    callback = ({type, result, data}) => {
        console.log('real-comment::callback(): type, result', type, result, data);

        const deleteCommentCallback = (result, id) => {
            if (this.commentId !== id) return;

            this.remove();
        }

        const runCallback = {
            'deleteComment':   deleteCommentCallback,
        }
        runCallback[type] && runCallback[type](result, data);
    }
}
customElements.define('real-comment', RealComment);
export {RealComment}
