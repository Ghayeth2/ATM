const fetchAPI = {
    async get(url, params) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString
            ? `${window.location.origin}${url}?${queryString}`
            : `${window.location.origin}${url}`;
        try {
            const response = await fetch(fullUrl, {
                method: "GET",
            });
            if (!response.ok) throw new Error(`GET request failed with status: ${response.status}`);
            return await response.json();
        } catch (error) {
            console.error("Error in GET request:", error);
            throw error;
        }
    },

    async delete(url, params) {
        const queryString = new URLSearchParams(params).toString();
        const endpoint = queryString ? `${url}?${queryString}` : url;

        try {
            const response = await fetch(endpoint, {
                method: "DELETE",
            });
            if (!response.ok) throw new Error(`DELETE request failed with status: ${response.status}`);
        } catch (error) {
            console.error("Error in DELETE request:", error);
            throw error;
        }
    }
};