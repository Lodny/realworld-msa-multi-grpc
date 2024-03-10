import {iconCdn} from "../services/icon-cdn.js";
import {currentUser, store} from "../services/store.js";
import {actionQueue, addGoAction} from "../services/action-queue.js";

const getStyle = (color) => {
    return `<style>
        a {
            color: ${color};
        }
        
        .article-meta .info {
            text-align: left
        }
    </style>`;
}

const getTemplate = (article, isMyArticle, color) => {
    const author = article.author;

    return `
        ${iconCdn}
        <link rel="stylesheet" href="../css/common.css">
        <link rel="stylesheet" href="../css/article-meta.css">
        ${getStyle(color)}

        <div class="article-meta">
            <a href="/profile/${author.username}"><img src="${author.image}" /></a>
            <div class="info">
                <a href="/profile/${author.username}" class="author">${author.username}</a>
                <span class="date">${article.createdAt}</span>
            </div>
            
            ${isMyArticle ? `
            <button class="btn btn-sm btn-outline-secondary edit">
                <i class="ion-edit"></i> Edit Article
            </button>
            <button class="btn btn-sm btn-outline-danger delete">
                <i class="ion-trash-a"></i> Delete Article
            </button>`
            : `
            <button class="btn btn-sm btn-outline-secondary follow">
                <i class="ion-plus-round"></i> &nbsp; Follow ${author.username}
            </button>
            <button class="btn btn-sm btn-outline-primary favorite">
                <i class="ion-heart"></i>
                 &nbsp; Favorite Article <span class="counter">(${article.favoritesCount})</span>
            </button>
            `}
        </div>        
    `;
}

class RealArticleMeta extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({mode: 'open'});

        const slug = this.getAttribute('slug');
        console.log('real-comment-list::constructor(): slug:', slug);
        this.article = store.getArticle(slug);
        console.log('real-article-meta::constructor(): this.article:', this.article);

        const user = currentUser();
        console.log('real-comment-list::constructor(): user:', user);
        const isMyArticle = user && user.username === this.article.author.username;
        console.log('real-article-meta::constructor(): isMyArticle:', isMyArticle);

        const color = this.getAttribute('color');
        console.log('real-article-meta::constructor(): color:', color);

        this.shadowRoot.innerHTML = getTemplate(this.article, isMyArticle, color);
    }

    connectedCallback() {
        console.log('real-article-meta::connectedCallback(): 1:', 1);
        actionQueue.addListener(['follow', 'unfollow', 'favorite', 'unfavorite'], this);

        this.shadowRoot.querySelector('button.edit')
            ?.addEventListener('click', this.editArticle);
        this.shadowRoot.querySelector('button.delete')
            ?.addEventListener('click', this.deleteArticle);
        this.followBtn = this.shadowRoot.querySelector('button.follow');
        this.followBtn?.addEventListener('click', this.follow);
        this.favoriteBtn = this.shadowRoot.querySelector('button.favorite');
        this.favoriteBtn?.addEventListener('click', this.favorite);
        this.favoritesCountTag = this.shadowRoot.querySelector('.counter');
        this.shadowRoot.querySelectorAll('.article-meta a')
            .forEach(link => link.addEventListener('click', this.goLink));

        this.updateFollowing(this.article.author);
        this.updateFavorite(this.article);
    }

    disconnectedCallback() {
        actionQueue.removeListener(['follow', 'unfollow', 'favorite', 'unfavorite'], this);
    }

    goLink = (evt) => {
        evt.preventDefault();
        console.log('real-article-meta::goLink(): evt.target.closest(a):', evt.target.closest('a'));

        const url = evt.target.closest('a')?.href;
        console.log('real-article-meta::goLink(): url:', url);
        addGoAction(url)
    }

    editArticle = (evt) => {
        evt.preventDefault();
        console.log('real-article-meta::editArticle(): evt.target:', evt.target);

        addGoAction(`/editor/${this.article.slug}`)
    }

    deleteArticle = (evt) => {
        evt.preventDefault();
        console.log('real-article-meta::deleteArticle(): evt.target:', evt.target);

        actionQueue.addAction({
            type: 'deleteArticle',
            data: {
                value: this.article.slug
            },
            nextRoute: '/'
        })
    }

    follow = async (evt) => {
        evt.preventDefault();
        console.log('real-article-meta::follow1(): evt.target', evt.target);

        const author = this.article.author;
        actionQueue.addAction({
            type: author.following ? 'unfollow' : 'follow',
            data: {
                value: author.username,
            },
        });
    }

    favorite = (evt) => {
        evt.preventDefault();
        console.log('real-article-meta::favorite(): 1:', 1);

        actionQueue.addAction({
            type: this.article.favorited === false ? 'favorite' : 'unfavorite',
            data: {
                value: this.article.slug,
            },
        });
    }

    updateFavorite(article) {
        console.log('real-article-meta::updateFavorite(): article:', article);

        const user = currentUser();
        if (article.author.username === user?.username) return;

        this.favoritesCountTag.innerHTML = `(${article.favoritesCount})`;
        article.favorited
            ? this.favoriteBtn.classList.add('focus')
            : this.favoriteBtn.classList.remove('focus');
    }

    updateFollowing(author) {
        console.log('real-article-meta::updateFollowing(): author:', author);

        const user = currentUser();
        if (author.username === user?.username) return;

        if (author.following)
            this.followBtn.innerHTML = `<i class="ion-minus-round"></i> &nbsp; Unfollow ${author.username}`;
        else
            this.followBtn.innerHTML = `<i class="ion-plus-round"></i> &nbsp; Follow ${author.username}`;
    }

    callback = ({type, result}) => {
        console.log('real-article-meta::callback(): type, result:', type, result);

        const favoriteCallback = (result) => {
            this.article = result;
            store.setArticle(this.article);
            this.updateFavorite(this.article);
        }

        const followOrUnfollowCallback = (following) => {
            this.article.author.following = following;
            this.updateFollowing(this.article.author);
        }

        const followCallback = () => {
            followOrUnfollowCallback(true);
        }

        const unfollowCallback = (result) => {
            followOrUnfollowCallback(false);
        }

        const runCallback = {
            'favorite':   favoriteCallback,
            'unfavorite':   favoriteCallback,
            'follow':     followCallback,
            'unfollow':     unfollowCallback,
        }
        runCallback[type] && runCallback[type](result);
    }
}
customElements.define('real-article-meta', RealArticleMeta);
export {RealArticleMeta}
