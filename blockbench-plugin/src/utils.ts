import config from "./config";
import globals from "./globals";
import socket from "./socket";

export let playerListInterval: number;

export function updatePlayerList() {
    let fetchPlayerList = function () {
        if (!config.fetchPlayerList) {
            globals.playerList = [];
            return window.clearInterval(playerListInterval);
        }
        socket.send("fetch_player_list", {});
    }
    fetchPlayerList()
    playerListInterval = window.setInterval(fetchPlayerList, 10 * 1000);
}

// https://stackoverflow.com/a/67600346
export async function createSHA256Hash(input: string) {
    const textAsBuffer = new TextEncoder().encode(input);
    const hashBuffer = await window.crypto.subtle.digest("SHA-256", textAsBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hash = hashArray
        .map((item) => item.toString(16).padStart(2, "0"))
        .join("");
    return hash;
};