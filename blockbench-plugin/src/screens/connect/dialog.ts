import socket from "../../socket";
import config from "../../config";

export function createConnectDialog() {
    // @ts-ignore: TODO
    const dialog = new Dialog({
        id: "connect_to_server_dialog",
        title: "Connect to Minecraft Server",
        lines: [
            "Enter the address of a Minecraft server with the Benchkit plugin installed and the key specified in the config.",
            `<hr>`
        ],
        form: {
            address: { label: "Server address", type: "text" },
            port: { label: "Server port", type: "text" },
            key: { label: "Key", type: "text" },
            remember: { label: "Save details", type: "checkbox" }
        },
        onConfirm: (formData: any) => {
            if (!formData.address || !formData.port) {
                return Blockbench.showQuickMessage("Please enter server address and port", 3000);
            }

            dialog.hide();

            try {
                socket.connect(formData.address, formData.port, formData.key);
            } catch (e) {
                return Blockbench.showQuickMessage(`Failed to connect to socket: ${e.message}`, 5000);
            }

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