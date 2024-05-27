import socket from "../../socket";
import config from "../../config";
import globals from "../../globals";

export function createExportDialog() {
    // @ts-ignore
    var dialog = new Dialog({
        id: "export_to_server_dialog",
        title: "Apply Skin on Server",
        form: {
            entityUuid: { label: "Player UUID", type: "text" } // TODO: Store last used player id in local storage and auto fill,
        },
        onConfirm: (formData: any) => {
            socket.send("apply_skin", {
                entityUuid: formData.entityUuid,
                // @ts-ignore
                texture: Project.textures[0].img.src
            });
            config.lastPlayerUuid = formData.entityUuid;
            Blockbench.showQuickMessage("Skin applied!", 3000);
            dialog.hide();
        }
    });
    
    if (config.fetchPlayerList) {
        var options = {}

        for (var player of globals.playerList) {
            options[player.uuid] = player.name + " (" + player.uuid + ")"
        }

        // @ts-ignore
        dialog.form = {
            entityUuid: { label: "Select player", type: "select", options: options }
        }
    }
    
    dialog.show();
}

// TODO: Merge this with the above function
export function createExportModelDialog() {
    // @ts-ignore
    var dialog = new Dialog({
        id: "export_model_to_server_dialog",
        title: "Apply Model on Server",
        form: {
            // @ts-ignore
            entityUuid: { label: "Player UUID", type: "text" }
        },
        onConfirm: (formData: any) => {
            socket.send("apply_model", {
                // @ts-ignore
                entityUuid: formData.entityUuid,
                // @ts-ignore
                identifier: Project.geometry_name,
                model: Codecs.bedrock.compile()
            })
            // @ts-ignore
            config.lastPlayerUuid = formData.entityUuid;
            Blockbench.showQuickMessage("Model applied!")
            dialog.hide()
        }
    })

    if (config.fetchPlayerList) {
        var options = {}

        for (var player of globals.playerList) {
            options[player.uuid] = player.name + " (" + player.uuid + ")"
        }

        // @ts-ignore
        dialog.form = {
            entityUuid: { label: "Select player", type: "select", options: options }
        }
    }

    dialog.show()
}