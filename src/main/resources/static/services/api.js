import {store} from "./store.js";

const BASE_URL = 'http://localhost:8080/api';

const initHeaders = () => {
    const user = store.get("user");
    return user ? {'Authorization': 'Token ' + user.token} : {};
}

const apiGetOrDelete = (method, url) => {
    console.log('api::apiGetOrDelete(): method, BASE_URL + url:', method, BASE_URL + url);

    const headers = initHeaders();
    return fetch(BASE_URL + url, {
        method,
        headers
    })
        .then(response => response.json())
        .catch(error => console.log('[E] api::apiGetOrDelete():', error));

    // try {
    //     const response = await fetch(BASE_URL + url, {headers});
    //     console.log('api::apiGet(): response:', response);
    //
    //     return response.json();
    // } catch (e) {
    //     console.log('[E] api::apiGet(): e:', e);
    // }
}

const apiPostOrPut = (method, url, data) => {
    console.log('api::apiPostOrPut(): method, BASE_URL + url:', method, BASE_URL + url);

    const headers = {...initHeaders(), 'Content-Type': 'application/json'};
    return fetch(BASE_URL + url, {
        method,
        headers,
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .catch(error => console.log('[E] api::apiPostOrPut():', error));
}

const apiGet = (url, data) => {
    return apiGetOrDelete('GET', url, data);
}

const apiPost = (url, data) => {
    return apiPostOrPut('POST', url, data);
}

const apiPut = (url, data) => {
    return apiPostOrPut('PUT', url, data);
}

const apiDelete = (url, data) => {
    return apiGetOrDelete('DELETE', url, data);
}



class RealApi {
    constructor() {
    }

    registerUser = (user) => {
        return apiPost('/users', {user});
    }

    loginUser = (user) => {
        return apiPost('/users/login', {user});
    }

    favorite = (slug) => {
        return apiPost(`/articles/${slug}/favorite`, {});
    }

    unfavorite = (slug) => {
        return apiDelete(`/articles/${slug}/favorite`, {});
    }

    getProfile = (username) => {
        return apiGet(`/profiles/${username}`);
    }

    follow = (username) => {
        return apiPost(`/profiles/${username}/follow`, {});
    }

    unfollow = (username) => {
        return apiDelete(`/profiles/${username}/follow`, {});
    }

    registerArticle = (article) => {
        return apiPost(`/articles`, {article});
    }

    getArticles = (param) => {
        const {
            tag,
            author,
            favorited,
            feed,
            offset,
            limit,
        } = param;
        console.log('api::realApi.getArticles(): param:', param);

        console.log('api::realApi.getArticles(): tag:', tag);
        console.log('api::realApi.getArticles(): author:', author);
        console.log('api::realApi.getArticles(): favorited:', favorited);

        console.log('api::realApi.getArticles(): offset:', offset);
        console.log('api::realApi.getArticles(): limit:', limit);

        let url = `/articles?`;
        if (tag)
            url += `tag=${tag}&`;
        else if (author)
            url += `author=${author}&`;
        else if (favorited)
            url += `favorited=${favorited}&`;
        else if (feed)
            url = `/articles/feed?`;

        return apiGet(`${url}offset=${offset}&limit=${limit}`);
    }

    deleteArticle = (slug) => {
        return apiDelete(`/articles/${slug}`, {});
    }

    getComments = (slug) => {
        return apiGet(`/articles/${slug}/comments`);
    }

    addComment = (slug, body) => {
        return apiPost(`/articles/${slug}/comments`, {comment: {body}});
    }

    deleteComment = (slug, id) => {
        return apiDelete(`/articles/${slug}/comments/${id}`, {});
    }

    getTop10Tags = () => {
        return apiGet(`/tags`);
    }

    updateUser = (user) => {
        return apiPut(`/users`, {user});
    }
}
const realApi = new RealApi();
export {realApi}
