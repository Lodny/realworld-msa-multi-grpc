class Store extends Map {
    constructor() {
        super();

        this.set('articles', []);
    }

    setArticles(articles) {
        console.log('store::addArticles(): articles:', articles);
        this.set('articles', articles);
    }

    getArticles() {
        return this.get('articles');
    }

    getArticle(slug) {
        return this.get('articles').find(article => article.slug === slug);
    }

    setArticle(newArticle) {
        const articles = this.get('articles');
        const idx = articles.findIndex(article => article.slug === newArticle.slug);
        if (idx < 0)
            throw Error('not found article');

        articles[idx] = newArticle;
    }

    setFavorite(article) {
        const foundArticle = this.getArticle(article.slug);
        if (!foundArticle) return article;

        foundArticle.favorited = !foundArticle.favorited;
        return foundArticle;
    }

    getComment(id) {
        return this.get('comments').find(comment => comment.id === id);
    }
}
const store = new Store();

const currentUser = () => store.get('user');

export {
    store
    , currentUser
}
