import html from "./index";
import config from "../../config";

export function createConfigureDialog() {
    // The blockbench types are annoying
    // @ts-ignore
    const dialog = new Dialog({
        id: "benchkit_configure_dialog",
        title: "Configure Benchkit",
        lines: [html],
        onConfirm: () => {
            config.fetchPlayerList = $("#setting_fetch_player_list").is(":checked");
            dialog.hide()

            // @ts-ignore
            Blockbench.dispatchEvent("benchkit_prefs_updated", {
                fetch_player_list: config.fetchPlayerList
            })
        },
    }).show()

    $("#setting_fetch_player_list").prop("checked", config.fetchPlayerList)
    $("#reset_conn_details").on("click", (event) => {
        config.serverConnection = null;
        dialog.hide()
        Blockbench.showQuickMessage("Minecraft Server connection details reset", 2 * 1000)
    })
    $("#reset_last_player_details").on("click", (event) => {
        config.lastPlayerUuid = null;
        dialog.hide()
        Blockbench.showQuickMessage("Last player details reset", 2 * 1000)
    })
}