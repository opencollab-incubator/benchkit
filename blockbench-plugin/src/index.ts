import config from "./config";
import globals from "./globals";
import socket from "./socket";

import { loadStyles } from "./screens/style-registry";
import { createConfigureDialog } from "./screens/configure/dialog";
import { createConnectDialog } from "./screens/connect/dialog";
import { createExportDialog, createExportModelDialog } from "./screens/export/dialog";

(function () {
    // Workaround for the Plugin class conflicting with the dom types
    // @ts-ignore
    Plugin.register("benchkit", {
        title: "Benchkit",
        author: "lukeeey",
        description: "",
        version: "1.0.0",
        variant: "both",
        onload() {
            // Load CSS styles
            loadStyles();

            initMenuItems();
            initKeybinds();

            // In the future (soon) I want to seperate dialogs into their own
            // classes to reduce the bloat in this file.
        }
    })
})();

function initMenuItems() {
    let menu = new BarMenu("benchkitMenu", [
        new Action("connectToServer", {
            name: "Connect to Minecraft Server",
            icon: "",
            condition: isSkinOrModel,
            click: (event) => {
                let conn;
                if ((conn = config.serverConnection) !== null) {
                    return socket.connect(conn.address, conn.port, conn.key);
                }
                createConnectDialog();
            }
        }),
        new Action("disconnectFromServer", {
            name: "Disconnect from Minecraft Server",
            icon: "",
            condition: isSkinOrModel,
            click: (event) => {
                socket.close("Disconnected via blockbench");
            }
        }),
        new Action("benchkitConfigure", {
            name: "Configure",
            icon: "",
            condition: isSkinOrModel,
            click: (event) => {
                createConfigureDialog();
            }
        }),
        "",
        // TODO: Merge these in the future if possible
        new Action("applySkinOnServer", {
            name: "Apply Skin on Server",
            icon: "",
            condition: () => Format.id === "skin",
            click: (event) => {
                createExportDialog();
            }
        }),
        new Action("applyModelOnServer", {
            name: "Apply Model on Server",
            icon: "",
            condition: () => Format.id === "bedrock",
            click: (event) => {
                createExportModelDialog();
            }
        })
    ], true)

    // Workaround for setting menu title
    // @ts-ignore
    menu.label.innerText = "Benchkit";

    // Update the menu bar since we changed the title
    MenuBar.update();
}

function initKeybinds() {

}

/**
 * @returns true if the project is a skin or bedrock model project
 */
function isSkinOrModel() {
    return Format.id === "skin" || Format.id === "bedrock";
}

let playerListInterval: number;

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

// @ts-ignore
Blockbench.on("benchkit_prefs_updated", (data: any) => {
    if (data.fetch_player_list && globals.playerList.length === 0) {
        updatePlayerList()
    } else {
        globals.playerList = []
        window.clearInterval(playerListInterval)
    }
});