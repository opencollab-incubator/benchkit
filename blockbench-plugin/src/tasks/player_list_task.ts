import globals from "../globals";
import socket from "../socket";

class PlayerListTask implements Task {

    execute(data: object) {
        // TODO: Find a better way to do this!
        // TODO: More validation
        // @ts-ignore
        let texture = textures[0].img.src;

        socket.send("apply_skin", )
    }

    handle(data: object) {
        // @ts-ignore
        globals.playerList = data.players;
    }
}