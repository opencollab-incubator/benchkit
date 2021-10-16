import config from "./config";
import globals from "./globals";
import { updatePlayerList } from "./index";

class BenchkitSocket {
    private socket?: WebSocket;
    private address?: string;
    private port?: number;
    private key?: string;

    connect(address: string, port: number, key: string) {
        if (this.socket !== undefined) {
            throw Error("Tried to connect to socket before terminating existing connection");
        }
        this.socket = new WebSocket(`ws://${address}:${port}`);

        this.socket.onopen = (event) => {
            this.key = key;
            this.address = address;
            this.port = port;
            this.onOpen(event);
        }
        this.socket.onmessage = this.onMessage;
        this.socket.onclose = this.onClose;
        this.socket.onerror = this.onError;
    }

    close(reason: string) {
        if (this.socket === undefined) {
            throw new Error("Tried to close uninitialized socket");
        }
        this.socket.close(1000, reason);
    }

    send(type: string, data?: object) {
        if (this.socket === undefined) {
            throw new Error("Tried to send data to socket before initializing");
        }
        this.socket.send(JSON.stringify({
            type: type,
            key: this.key,
            data: data
        }))
    }

    private onOpen(event: any) {
        Blockbench.showStatusMessage(`Connected to ${this.address}:${this.port}`, 3 * 1000);
        this.send("authenticate");
    }

    private onMessage(event: any) {
        let data = JSON.parse(event.data);

        if (data.type === "authenticate") {
            if (data.key !== this.key) {
                return this.close("Failed to authenticate: Keys did not match");
            }
            if (config.fetchPlayerList) {
                updatePlayerList();
            }
        }
        if (data.type === "fetch_player_list") {
            globals.playerList = data.data.players;
            Blockbench.showStatusMessage(`Updated player list (${data.data.players.length} players)`, 1.5 * 1000);
        }
    }

    private onClose(event: any) {
        Blockbench.showStatusMessage("Disconnected from Minecraft Server" + (event.reason != null ? " (" + event.reason + ")" : ""), 5 * 1000)

        if (event.wasClean) {
            Blockbench.showQuickMessage("Socket connection closed: " + event.reason, 3 * 1000)
        } else {
            Blockbench.showQuickMessage("Socket connection died")
        }

        this.address = undefined;
        this.port = undefined;
        this.key = undefined;
    }

    private onError(error: any) {
        alert("Socket error: " + error.message);
    }
}

export default new BenchkitSocket;