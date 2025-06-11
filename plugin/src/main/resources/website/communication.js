async function closeVisualizer(){
    try {
        const resp = await fetch("/continue", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: null
        });

        if (!resp.ok) {
            const text = await resp.text();
            console.error("Server error:", text);
            alert("Server error: " + text);
            return;
        }
        close();
    } catch (err) {
        console.error("Network error:", err);
        alert("Failed to close visualizer.");
    }
}

function getJSON() {
    const request = new XMLHttpRequest();
    request.open('GET', 'irtree.json', false);
    request.send(null);

    if (request.status === 200) {
        return request.responseText
    } else {
        alert('Failed to load JSON');
    }
}