import {realApi} from "./api.js";
import {currentUser, store} from "./store.js";

class ActionQueue {
    constructor() {
        this.actionQueue = [];
        this.listenerMap = new Map();

        setInterval(this.run, 20);
        this.navbar = document.querySelector('real-navbar');
        console.log('action-queue::constructor(): this.navbar:', this.navbar);

        this.alertTag = document.querySelector('real-alert');

        this.actionExecutor = {
            'registerUser': this.registerUserAction
            , 'login': this.loginAction
            , 'route': this.routeAction
            , 'logout': this.logoutAction
            , 'getProfile': this.getProfile
            , 'favorite': this.favoriteAction
            , 'unfavorite': this.unfavoriteAction
            , 'follow': this.followAction
            , 'unfollow': this.unfollowAction
            , 'updateUser': this.updateUser
            , 'registerArticle': this.registerArticle
            , 'getArticles': this.getArticles
            , 'deleteArticle': this.deleteArticle
            , 'addComment': this.addComment
            , 'getComments': this.getComments
            , 'deleteComment': this.deleteComment
            , 'getTags': this.getTags
            , 'selectTag': this.selectTag
            , 'changePage': this.changePage
            , 'pageInfo': this.pageInfo
            , 'tabTitles': this.tabTitles
            , 'activeTab': this.activeTab
        }
    }

    addAction(action) {
        this.actionQueue.push(action);
    }

    run = async () => {
        if (this.actionQueue.length === 0) return;

        const action = this.actionQueue.shift();
        console.log('action-queue::run(): action:', action);

        const executor = this.actionExecutor[action.type];
        if (!executor) return;

        const data = await executor(action.data);
        console.log('action-queue::run(): data:', data);

        if (this.hasError(data)) {
            await this.alertTag.alert(data.message);
            action.callback && action.callback({type: "error", result: data.message});
            return;
        }

        const result = data && Object.values(data)[0];
        action.set && store.set(action.set, data[action.set]);
        this.notifyListener(action, result, data);
        action.nextRoute && addGoAction(action.nextRoute);
    }

    hasError(result) {
        if (!result) return false;
        if (!result.message) return false;

        return true;
    }

    notifyListener(action, result, data) {
        this.listenerMap
            .get(action.type)
            ?.forEach(listener => {
                console.log('action-queue::noMethod(): listener:', listener);
                listener?.callback({type: action.type, result, data});
            });
    }

    addListener(types, listener) {
        console.log('action-queue::addListener(): types:', types);
        console.log('action-queue::addListener(): listener:', listener);

        types = Array.isArray(types) ? types : [types];
        types.forEach(type => {
            const listeners = this.listenerMap.get(type) ?? [];
            listeners.push(listener);
            this.listenerMap.set(type, listeners);
        })
    }

    removeListener(types, removeListener) {
        console.log('action-queue::removeListener(): types:', types);
        console.log('action-queue::removeListener(): removeListener:', removeListener);

        types = Array.isArray(types) ? types : [types];
        types.forEach(type => {
            const listeners = this.listenerMap.get(type) ?? [];
            this.listenerMap.set(type, listeners.filter(listener => listener !== removeListener));
        })
    }

    registerUserAction = async (user) => {
        return await realApi.registerUser(user);
    }

    loginAction = async (user) => {
        return await realApi.loginUser(user);
    }

    logoutAction = () => {
        store.delete('user');
    }

    routeAction = (data) => {
        return data;
    }

    getProfile = async ({value: username}) => {
        console.log('action-queue::getProfile(): username:', username);
        return await realApi.getProfile(username);
    }

    checkAuth() {
        const user = currentUser();
        if (!user)
            throw Error('login first');

        return user;
    }

    favoriteAction = ({value: slug}) => {
        console.log('action-queue::favoriteAction(): slug:', slug);

        if (!this.checkFavoriteAction(slug)) return;

        return realApi.favorite(slug);
    }

    unfavoriteAction = async ({value: slug}) => {
        console.log('action-queue::unfavoriteAction(): slug:', slug);

        if (!this.checkFavoriteAction(slug)) return;

        return realApi.unfavorite(slug);
    }

    checkFavoriteAction = (slug) => {
        const user = currentUser();

        if (!user) {
            addGoAction('/login');
            return false;
        }

        const article = store.getArticle(slug);
        if (article.author.username === user.username) {
            console.log('action-queue::favoriteAction(): article.author.username === user.username');
            return false;
        }

        return true;
    }

    checkFollowAction = (username) => {
        const user = currentUser();

        if (!user) {
            addGoAction('/login');
            return false;
        }

        if (username === user.username) {
            console.log('action-queue::followAction(): username === user.username');
            return false;
        }

        return true;
    }

    followAction = async ({value: username}) => {
        console.log('action-queue::followAction(): username:', username);

        if (!this.checkFollowAction(username)) return;

        return await realApi.follow(username);
    }

    registerArticle = async ({value: article}) => {
        console.log('action-queue::registerArticle(): article', article);
        this.checkAuth();

        return await realApi.registerArticle(article);
    }

    getArticles = async ({value: param, page}) => {
        console.log('action-queue::getArticles(): param, page:', param, page);

        param.limit = 2;
        // param.limit = 20;
        param.offset = (page - 1) * param.limit;

        return await realApi.getArticles(param);
    }

    unfollowAction = async ({value: username}) => {
        console.log('action-queue::unfollowAction(): username:', username);
        if (!this.checkFollowAction(username)) return;

        return await realApi.unfollow(username);
    }

    updateUser = async (user) => {
        console.log('action-queue::updateUser(): user:', user);
        this.checkAuth();

        return await realApi.updateUser(user);
    }

    deleteArticle = async ({value: slug}) => {
        console.log('action-queue::deleteArticle(): slug:', slug);
        this.checkAuth();

        return await realApi.deleteArticle(slug);
    }

    addComment = async ({slug, value: body}) => {
        console.log('action-queue::addComment(): slug, value:', slug, body);
        this.checkAuth();

        const result = await realApi.addComment(slug, body);
        if (! result.message) {
            const comments = store.get('comments');
            store.set('comments', [result.comment, ...comments]);
        }

        return result;
    }

    getComments = async ({value: slug}) => {
        console.log('action-queue::addComment(): slug:', slug);

        return await realApi.getComments(slug);
    }

    deleteComment = async ({slug, value: id}) => {
        console.log('action-queue::deleteComment(): slug, id:', slug, id);
        this.checkAuth();

        const result = await realApi.deleteComment(slug, id);
        if (! result.message) {
            const comments = store.get('comments').filter(comment => comment.id !== Number(id));
            store.set('comments', comments);
        }

        return result;
    }

    getTags = async () => {
        return await realApi.getTop10Tags();
    }

    selectTag = (data) => data;

    changePage = ({value: page}) => {
        console.log('action-queue::changePage(): page:', page);
        return page;
    }

    pageInfo = (data) => data;

    tabTitles = (data) => data;
    activeTab = (data) => data;
}

const actionQueue = new ActionQueue();

const addGoAction = (url) => {
    actionQueue.addAction({
        type: 'route',
        data: {
            value: url,
        },
    });
}

export {
    actionQueue
    , addGoAction
}
