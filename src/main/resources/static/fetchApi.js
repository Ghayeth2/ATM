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

    async post(url, params) {
        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",  // Tells the server we're sending JSON data
                },
                body: JSON.stringify(params),  // Convert the params object to a JSON string
            });

            if (!response.ok) throw new Error(`POST request failed with status: ${response.status}`);

            // Return the response data as JSON
            return await response.json();
        } catch (error) {
            console.error("Error in POST request:", error);
            throw error;
        }
    },


    async delete(url, params) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString
            ? `${window.location.origin}${url}?${queryString}`
            : `${window.location.origin}${url}`;

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