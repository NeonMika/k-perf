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
            alert("Error continuing: " + resp.status);
            return;
        }

        const msg = await resp.text();
        console.log("Server says:", msg);

        close();
    } catch (err) {
        console.error("Network error:", err);
        alert("Failed to connect to compiler-server.");
    }
}