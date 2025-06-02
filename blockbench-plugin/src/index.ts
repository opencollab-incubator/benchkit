import config from "./config";
import globals from "./globals";
import socket from "./socket";

import { loadStyles } from "./screens/style-registry";
import { createConfigureDialog } from "./screens/configure/dialog";
import { createConnectDialog } from "./screens/connect/dialog";
import { createExportDialog, createExportModelDialog } from "./screens/export/dialog";
import { playerListInterval, updatePlayerList } from "./utils";

(function () {
    BBPlugin.register("benchkit", {
        title: "Benchkit",
        author: "Open Collaboration",
        description: "A Blockbench plugin used in conjunction with a Minecraft server plugin to assist with testing skins and models",
        version: "1.0.0",
        icon: "", // TODO: Create an icon
        variant: "both",
        onload() {
            loadStyles();
            initMenuItems();
            initKeybinds();
        }
    })
})();

function initMenuItems() {
    const isConnectedToServer = () => socket.isConnected();
    const isSkinOrModel = () => Format.id === "skin" || Format.id === "bedrock";

    let menu = new BarMenu("benchkitMenu", [
        new Action("connectToServer", {
            name: "Connect to Minecraft Server",
            icon: "",
            condition: () => isSkinOrModel() && !isConnectedToServer(),
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
            condition: () => isSkinOrModel() && isConnectedToServer(),
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
        new Action("applySkinOnServer", {
            name: "Apply Skin on Server",
            icon: "",
            condition: () => Format.id === "skin" && isConnectedToServer(),
            click: (event) => {
                createExportDialog();
            }
        }),
        new Action("applyModelOnServer", {
            name: "Apply Model on Server",
            icon: "",
            condition: () => Format.id === "bedrock" && isConnectedToServer(),
            click: (event) => {
                createExportModelDialog();
            }
        })
    ], isSkinOrModel);

    // Workaround for setting menu title
    // @ts-ignore
    menu.label.innerText = "Benchkit";

    // Update the menu bar since we changed the title
    MenuBar.update();
}

function initKeybinds() {

}

Blockbench.on("benchkit_prefs_updated", (data: any) => {
    if (data.fetch_player_list && globals.playerList.length === 0) {
        updatePlayerList()
    } else {
        globals.playerList = []
        window.clearInterval(playerListInterval)
    }
});