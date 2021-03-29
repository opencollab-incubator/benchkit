/// <reference types="blockbench-types" />

import config from "./config";
import globals from "./globals";
import socket from "./socket";

import configureScreen from "../screens/configure.html";

(function() {
    // Workaround for the Plugin class conflicting with the dom types
    // @ts-ignore
    Plugin.register("benchkit", {
        title: "Benchkit",
        author: "lukeeey",
        description: "",
        version: "1.0.0",
        variant: "both",
        onload() {
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
                showConnectDialog();
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
                showConfigureDialog();
            }
        }),
        "",
        // TODO: Merge these in the future if possible
        new Action("applySkinOnServer", {
            name: "Apply Skin on Server",
            icon: "",
            condition: () => Format.id === "skin",
            click: (event) => {
                showExportDialog();
            }
        }),
        new Action("applyModelOnServer", {
            name: "Apply Model on Server",
            icon: "",
            condition: () => Format.id === "bedrock",
            click: (event) => {
                showExportModelDialog();
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

function isSkinOrModel() {
    return Format.id === "skin" || Format.id === "bedrock";
}

let playerListInterval: number;

export function updatePlayerList() {
    let fetchPlayerList = function() {
        if (!config.fetchPlayerList) {
            globals.playerList = [];
            return window.clearInterval(playerListInterval);
        }
        socket.send('fetch_player_list', {});
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
})


/**
 * 
 * 
 * This needs to be cleaned up ASAP.
 * 
 * 
 */
function showConnectDialog() {
    // @ts-ignore: TODO
    var dialog = new Dialog({
        id: 'connect_to_server_dialog',
        title: 'Connect to Minecraft Server',
        lines: [
            'Enter the address of a Minecraft server with the Benchkit plugin installed and the key specified in the config.',
            `<hr>`
        ],
        form: {
            address: { label: 'Server address', type: 'text' },
            port: { label: 'Server port', type: 'text' },
            key: { label: 'Key', type: 'text' },
            remember: { label: 'Save details', type: 'checkbox' }
        },
        onConfirm: function (formData: any) {
            dialog.hide();
            socket.connect(formData.address, formData.port, formData.key);

            if (formData.remember) {
                config.serverConnection = {
                    address: formData.address,
                    port: formData.port,
                    key: formData.key
                }
            }
        }
    }).show();
}

function showExportDialog() {
    // @ts-ignore
    var dialog = new Dialog({
        id: 'export_to_server_dialog',
        title: 'Apply Skin on Server',
        form: {
            entityUuid: { label: 'Player UUID', type: 'text' } // TODO: Store last used player id in local storage and auto fill,
        },
        onConfirm: function (formData: any) {
            dialog.hide()
            socket.send('apply_skin', {
                entityUuid: formData.entityUuid,
                // @ts-ignore
                texture: textures[0].img.src
            })
            config.lastPlayerUuid = formData.entityUuid;
            Blockbench.showQuickMessage('Skin applied!')
        }
    })

    if (config.fetchPlayerList) {
        var options = {}

        for (var player of globals.playerList) {
            options[player.uuid] = player.name + ' (' + player.uuid + ')'
        }

        // @ts-ignore
        dialog.form = {
            entityUuid: { label: 'Select player', type: 'select', options: options }
        }
    }

    dialog.show()
}

// TODO: Merge this with the above function
function showExportModelDialog() {
    // @ts-ignore
    var dialog = new Dialog({
        id: 'export_model_to_server_dialog',
        title: 'Apply Model on Server',
        form: {
            // @ts-ignore
            entityUuid: { label: 'Player UUID', type: 'text' } 
        },
        onConfirm: function (formData) {
            socket.send('apply_model', {
                // @ts-ignore
                entityUuid: formData.entityUuid,
                // @ts-ignore
                identifier: Project.geometry_name,
                model: Codecs.bedrock.compile()
            })
            // @ts-ignore
            config.lastPlayerUuid = formData.entityUuid;
            Blockbench.showQuickMessage('Model applied!')
            dialog.hide()
        }
    })

    if (config.fetchPlayerList) {
        var options = {}

        for (var player of globals.playerList) {
            options[player.uuid] = player.name + ' (' + player.uuid + ')'
        }

        // @ts-ignore
        dialog.form = {
            entityUuid: { label: 'Select player', type: 'select', options: options }
        }
    }

    dialog.show()
}

function showConfigureDialog() {
    // @ts-ignore
    var dialog = new Dialog({
        id: 'benchkit_configure_dialog',
        title: 'Configure Benchkit',
        lines: [configureScreen],
        onConfirm: function () {
            config.fetchPlayerList = $('#setting_fetch_player_list').is(':checked');
            dialog.hide()

            Blockbench.dispatchEvent('benchkit_prefs_updated', {
                fetch_player_list: config.fetchPlayerList
            })
        },
    }).show()

    $('#setting_fetch_player_list').prop('checked', config.fetchPlayerList)
    $('#reset_conn_details').on('click', (event) => {
        config.serverConnection = null;
        dialog.hide()
        Blockbench.showQuickMessage('Minecraft Server connection details reset', 2 * 1000)
    })
    $('#reset_last_player_details').on('click', (event) => {
        config.lastPlayerUuid = null;
        dialog.hide()
        Blockbench.showQuickMessage('Last player details reset', 2 * 1000)
    })
}